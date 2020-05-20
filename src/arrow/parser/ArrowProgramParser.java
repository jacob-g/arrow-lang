package arrow.parser;

import java.util.List;

import arrow.ArrowTokenType;
import arrow.symboltable.SymbolTableEntryType;
import arrow.symboltable.SymbolTableStack;
import lexer.Token;
import parser.ParseResult;

public class ArrowProgramParser extends AbstractArrowParser {

	public ArrowProgramParser() {
		super(0, SymbolTableStack.build());
	
		//TODO: have a definition for built-in types
		symbolTable.add("int", SymbolTableEntryType.TYPE);
		symbolTable.add("bool", SymbolTableEntryType.TYPE);
	}

	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		ParseResult<ArrowTokenType> mainResult = parseMain(tokens);
		
		return mainResult;
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
		while (!remainder.isEmpty()) {
			ParseResult<ArrowTokenType> lineResult = LineParser.of(0, symbolTable).parse(remainder);
			
			if (!lineResult.getSuccess()) {
				return lineResult;
			}
			
			remainder = lineResult.getRemainder();
		}
		
		return null;
	}
}
