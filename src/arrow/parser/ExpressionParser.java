package arrow.parser;

import java.util.List;
import java.util.Objects;

import arrow.ArrowTokenType;
import arrow.symboltable.SymbolTableStack;
import lexer.Token;
import parser.ParseResult;

public class ExpressionParser extends AbstractArrowParser {

	private ExpressionParser(int indentation, SymbolTableStack symbolTable) {
		super(indentation, symbolTable);
	}
	
	public static ExpressionParser of(int indentation, SymbolTableStack symbolTable) {
		requireNonNegative(indentation);
		Objects.requireNonNull(symbolTable);
		
		return new ExpressionParser(indentation, symbolTable);
	}

	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		if (tokens.size() < 2) {
			return ParseResult.failure("Unexpected end of data", tokens);
		}
		
		if (tokens.get(0).getType() == ArrowTokenType.MINUS) {
			//negate
		}
		
		ParseResult<ArrowTokenType> firstFactorParseResult = parseAddend(tokens);
		if (!firstFactorParseResult.getSuccess()) {
			return firstFactorParseResult;
		}
		
		//TODO: see if we have an operator
		
		return firstFactorParseResult;
	}
	
	private ParseResult<ArrowTokenType> parseAddend(List<Token<ArrowTokenType>> tokens) {
		return parseFactor(tokens);
	}
	
	private ParseResult<ArrowTokenType> parseFactor(List<Token<ArrowTokenType>> tokens) {
		if (tokens.isEmpty()) {
			return ParseResult.failure("Unexpected end of data", tokens);
		}
		
		switch (tokens.get(0).getType()) {
		default:
			return ParseResult.failure("Unexpected symbol in expression", tokens);
		}
	}

}
