package arrow.parser;

import arrow.ArrowTokenType;

import java.util.List;
import lexer.Token;
import parser.AbstractParser;
import parser.ParseResult;
import symboltable.StaticSymbolTableStack;

abstract class AbstractArrowParser extends AbstractParser<ArrowTokenType> {
	protected final int indentation;
	protected final StaticSymbolTableStack symbolTable;
	
	protected final ParseResult<ArrowTokenType> parseIndentation(List<Token<ArrowTokenType>> tokens) {
		return requireType(tokens, ArrowTokenType.PIPE, indentation);
	}
	
	protected AbstractArrowParser(int indentation, StaticSymbolTableStack symbolTable) {
		this.indentation = indentation;
		this.symbolTable = symbolTable;
	}
}
