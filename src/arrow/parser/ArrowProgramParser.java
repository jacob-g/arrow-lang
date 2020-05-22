package arrow.parser;

import java.util.LinkedList;
import java.util.List;

import arrow.lexer.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;
import parser.tree.EmptyParseTreeNode;
import parser.tree.ParseTreeNode;
import parser.tree.ProgramNode;
import symboltable.StaticSymbolTableStack;
import symboltable.SymbolTableEntryType;

public class ArrowProgramParser extends AbstractArrowParser {

	public ArrowProgramParser() {
		super(0, new StaticSymbolTableStack());
	
		//TODO: have a definition for built-in types
		symbolTable.add("int", SymbolTableEntryType.TYPE);
		symbolTable.add("bool", SymbolTableEntryType.TYPE);
	}

	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		ParseResult<ArrowTokenType> functionResult = parseFunctions(tokens);
		if (!functionResult.getSuccess()) {
			return functionResult;
		}
		
		ParseResult<ArrowTokenType> mainResult = parseMain(functionResult.getRemainder());
		return mainResult;
	}
	
	public ParseResult<ArrowTokenType> parseFunctions(List<Token<ArrowTokenType>> tokens) {
		List<Token<ArrowTokenType>> remainder = tokens;
		
		boolean moreFunctions = !tokens.isEmpty();
		
		while (moreFunctions) {
			Token<ArrowTokenType> firstToken = remainder.get(0);
			switch (firstToken.getType()) {
			case NEWLINE:
				remainder = remainder.subList(1, remainder.size()); break;
			case FUNCTION:
				ParseResult<ArrowTokenType> functionParseResult = ArrowFunctionParser.of(indentation, symbolTable).parse(tokens);
				if (!functionParseResult.getSuccess()) {
					return functionParseResult;
				}
				remainder = functionParseResult.getRemainder();
				break;
			case MAIN:
				//we hit the main block, so bail out
				moreFunctions = false;
				break;
			default:
				return ParseResult.failure("Unexpected token in function declaration section: " + firstToken.getType(), remainder);
			}
		}
		
		return ParseResult.of(new EmptyParseTreeNode(), remainder);
	}

	public ParseResult<ArrowTokenType> parseMain(List<Token<ArrowTokenType>> tokens) {
		symbolTable.push(symbolTable.getRoot());
		
		ParseResult<ArrowTokenType> starterResult = requireType(tokens, ArrowTokenType.MAIN, 1);
		if (!starterResult.getSuccess()) {
			return starterResult;
		}
		
		ParseResult<ArrowTokenType> newLineResult = requireType(starterResult.getRemainder(), ArrowTokenType.NEWLINE, 1);
		if (!newLineResult.getSuccess()) {
			return newLineResult;
		}
		
		List<Token<ArrowTokenType>> remainder = newLineResult.getRemainder();
		List<ParseTreeNode> children = new LinkedList<>();
		while (!remainder.isEmpty()) {
			ParseResult<ArrowTokenType> lineResult = LineParser.of(0, symbolTable).parse(remainder);
			
			if (!lineResult.getSuccess()) {
				return lineResult;
			}
			
			children.add(lineResult.getNode());
			
			remainder = lineResult.getRemainder();
		}
		
		return ParseResult.of(ProgramNode.of(children), new LinkedList<>());
	}
}
