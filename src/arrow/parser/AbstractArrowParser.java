package arrow.parser;

import arrow.ArrowTokenType;
import arrow.symboltable.SymbolTableStack;

import java.util.List;
import lexer.Token;
import parser.AbstractParser;
import parser.ParseResult;

public abstract class AbstractArrowParser extends AbstractParser<ArrowTokenType> {
	protected final int indentation;
	protected final SymbolTableStack symbolTable;
	
	protected final ParseResult<ArrowTokenType> parseIndentation(List<Token<ArrowTokenType>> tokens) {
		return requireType(tokens, ArrowTokenType.PIPE, indentation);
	}
	
	protected AbstractArrowParser(int indentation, SymbolTableStack symbolTable) {
		this.indentation = indentation;
		this.symbolTable = symbolTable;
	}
}
