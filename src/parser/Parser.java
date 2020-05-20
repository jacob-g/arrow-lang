package parser;

import java.util.List;

import lexer.Token;

public interface Parser<T> {
	ParseResult<T> parse(List<Token<T>> tokens);
}
