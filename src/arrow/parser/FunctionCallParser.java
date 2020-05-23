package arrow.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import arrow.lexer.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;
import parser.tree.FunctionCallNode;
import parser.tree.ParseTreeAttributeType;
import parser.tree.ParseTreeNode;
import symboltable.StaticSymbolTableStack;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;

final class FunctionCallParser extends AbstractArrowParser {

	protected FunctionCallParser(int indentation, StaticSymbolTableStack symbolTable) {
		super(indentation, symbolTable);
	}
	
	public static FunctionCallParser of(int indentation, StaticSymbolTableStack symbolTable) {
		Objects.requireNonNull(symbolTable);
		requireNonNegative(indentation);
		
		return new FunctionCallParser(indentation, symbolTable);
	}

	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		if (tokens.size() < 3) {
			return ParseResult.failure("Improperly formatted function call", tokens);
		}
		
		if (tokens.get(0).getType() != ArrowTokenType.IDENTIFIER) {
			return ParseResult.failure("Improperly formed function call", tokens);
		}
		if (!symbolTable.contains(tokens.get(0).getContent())) {
			return ParseResult.failure("Function call to undefined function", tokens);
		}
		SymbolTableEntry functionIdentifier = symbolTable.lookup(tokens.get(0).getContent());
		if (functionIdentifier.getType() != SymbolTableEntryType.FUNCTION) {
			return ParseResult.failure("Function call to non-function", tokens);
		}
		
		List<Token<ArrowTokenType>> remainder = tokens.subList(1, tokens.size());
		
		//check the opening parenthesis
		ParseResult<ArrowTokenType> openParenParseResult = requireType(remainder, ArrowTokenType.OPEN_PAREN, 1);
		if (!openParenParseResult.getSuccess()) {
			return openParenParseResult;
		}
		remainder = openParenParseResult.getRemainder();
		
		List<ParseTreeNode> argNodes = new LinkedList<>();
		
		while (true) {			
			if (remainder.isEmpty()) {
				return ParseResult.failure("Unexpected end of data", remainder);
			}
			
			if (remainder.get(0).getType() == ArrowTokenType.CLOSE_PAREN) {
				remainder = remainder.subList(1, remainder.size());
				break;
			}
			
			ParseResult<ArrowTokenType> argParseResult = ExpressionParser.of(indentation, symbolTable).parse(remainder);
			if (!argParseResult.getSuccess()) {
				return argParseResult;
			}
			argNodes.add(argParseResult.getNode());
			
			remainder = argParseResult.getRemainder();
			
			if (remainder.get(0).getType() == ArrowTokenType.COMMA) {
				remainder = remainder.subList(1, remainder.size());
			}
		}
		
		if (argNodes.size() != functionIdentifier.getPayload().getAttribute(ParseTreeAttributeType.ARGUMENTS).getChildren().size()) {
			return ParseResult.failure("Wrong argument count", tokens);
		}
		
		return ParseResult.of(FunctionCallNode.of(functionIdentifier, argNodes), remainder);
	}

}
