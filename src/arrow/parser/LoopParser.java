package arrow.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import arrow.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;
import parser.tree.ControlParseTreeNode;
import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;
import symboltable.StaticSymbolTableStack;

final class LoopParser extends AbstractArrowParser {

	private LoopParser(int indentation, StaticSymbolTableStack symbolTable) {
		super(indentation, symbolTable);
	}
	
	public static LoopParser of(int indentation, StaticSymbolTableStack symbolTable) {
		requireNonNegative(indentation);
		Objects.requireNonNull(symbolTable);
		
		return new LoopParser(indentation, symbolTable);
	}

	//TODO: get rid of repeated code with the loop parser
	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		assert tokens.size() > 1;
		
		List<Token<ArrowTokenType>> remainder = tokens.subList(1, tokens.size());
		
		//consume the newline
		ParseResult<ArrowTokenType> newLineResult = requireType(remainder, ArrowTokenType.NEWLINE, 1);
		if (!newLineResult.getSuccess()) {
			return newLineResult;
		}
		remainder = newLineResult.getRemainder();
		
		//push another layer onto the symbol table since we're entering a block
		symbolTable.push();
		
		//parse the body until we get to the end
		List<ParseTreeNode> children = new LinkedList<>();
		boolean moreBody = true;
		while (moreBody) {
			assert indentation >= 0;
			
			ParseResult<ArrowTokenType> indentationResult = requireType(remainder, ArrowTokenType.PIPE, indentation);
			if (!indentationResult.getSuccess()) {
				return indentationResult;
			}
			remainder = indentationResult.getRemainder();
			if (remainder.isEmpty()) {
				return ParseResult.failure("Unexpected end-of-file", tokens);
			}
			
			switch (remainder.get(0).getType()) {
			case PIPE:
				//for a pipe, it's just regular body, so parse the line like normal
				ParseResult<ArrowTokenType> nextResult = LineParser.of(indentation + 1, symbolTable).parse(remainder.subList(1, remainder.size()));
				if (!nextResult.getSuccess()) {
					return nextResult;
				}
				children.add(nextResult.getNode());
				remainder = nextResult.getRemainder();
				break;
			
			case END_DO_WHILE:
				//for an end-if, we're done with the if, so bail out
				symbolTable.pop();
				moreBody = false;
				remainder = remainder.subList(1, remainder.size()); //pop the remainder off the list
				break;
			default:
				return ParseResult.failure("Unexpected token in loop body: " + indentationResult.getRemainder().get(0).getType(), indentationResult.getRemainder());
			}
		}
		
		ParseResult<ArrowTokenType> conditionResult = ExpressionParser.of(indentation, symbolTable).parse(remainder);
		if (!conditionResult.getSuccess()) {
			return conditionResult;
		}
		
		ParseTreeNode conditionNode = conditionResult.getNode();
		remainder = conditionResult.getRemainder();
				
		return ParseResult.of(ControlParseTreeNode.of(ParseTreeNodeType.LOOP, conditionNode, children), remainder);
	}
}
