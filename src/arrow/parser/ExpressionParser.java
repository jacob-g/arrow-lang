package arrow.parser;

import java.util.List;
import java.util.Objects;

import arrow.ArrowTokenType;
import arrow.symboltable.SymbolTableEntry;
import arrow.symboltable.SymbolTableStack;
import lexer.Token;
import parser.ParseResult;
import parser.tree.VariableParseTreeNode;

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
		case IDENTIFIER:
			return parseIdentifier(tokens);
		default:
			return ParseResult.failure("Unexpected symbol in expression", tokens);
		}
	}
	
	private ParseResult<ArrowTokenType> parseIdentifier(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty() && tokens.get(0).getType() == ArrowTokenType.IDENTIFIER;
		
		final String identifier = tokens.get(0).getContent();
		
		if (!symbolTable.contains(identifier)) {
			return ParseResult.failure("Use of undeclared variable in expression: " + identifier, tokens);
		}
		
		SymbolTableEntry variableEntry = symbolTable.lookup(identifier);
		switch (variableEntry.getType()) {
		case FUNCTION:
			throw new UnsupportedOperationException("Not yet implemented");
		case VARIABLE:
			return ParseResult.of(VariableParseTreeNode.of(variableEntry), tokens.subList(1, tokens.size()));
		default:
			return ParseResult.failure("Invalid identifier in expression", tokens);
		}
	}

}
