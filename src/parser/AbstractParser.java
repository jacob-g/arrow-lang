package parser;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import lexer.Token;
import parser.tree.EmptyParseTreeNode;

public abstract class AbstractParser<T> implements Parser<T> {
	protected static final void requireNonNegative(int number) {
		if (number < 0) {
			throw new IllegalArgumentException("Received non-positive where positive expected");
		}
	}
	
	protected final ParseResult<T> requireType(List<Token<T>> tokens, T type, int multiplicity) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(tokens);
		
		requireNonNegative(multiplicity);
		
		Iterator<Token<T>> iter = tokens.iterator();
		int counter = 0;
		while (counter < multiplicity) {
			if (!iter.hasNext() || !iter.next().getType().equals(type)) {
				return ParseResult.failure("Expected " + type + " " + multiplicity + " times, was not found", tokens.subList(counter, tokens.size() - 1));
			}
			
			counter++;
		}
		
		return ParseResult.of(new EmptyParseTreeNode(), tokens.subList(multiplicity, tokens.size()));
	}
}
