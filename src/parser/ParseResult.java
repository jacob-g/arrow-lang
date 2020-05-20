package parser;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lexer.Token;
import parser.tree.ParseTreeNode;

public class ParseResult<T> {
	
	private final Optional<ParseTreeNode> node;
	private final List<Token<T>> remainder;
	private final String failureMessage;
	
	private ParseResult(Optional<ParseTreeNode> node, List<Token<T>> remainder, String failureMessage) {
		assert remainder != null;
		
		this.node = node;
		this.remainder = remainder;
		this.failureMessage = failureMessage;
	}
	
	public static <T> ParseResult<T> failure(String message, List<Token<T>> remainder) {
		Objects.requireNonNull(remainder);
		Objects.requireNonNull(message);
		
		return new ParseResult<T>(Optional.empty(), remainder, message);
	}
	
	public static <T> ParseResult<T> of(ParseTreeNode node, List<Token<T>> remainder) {
		Objects.requireNonNull(node);
		Objects.requireNonNull(remainder);
		
		return new ParseResult<T>(Optional.of(node), remainder, null);
	}
	
	public boolean getSuccess() {
		assert node != null;
		
		return node.isPresent();
	}
	
	private void requireSuccess(boolean requiredSuccess) {
		if (getSuccess() != requiredSuccess) {
			throw new IllegalStateException("Performing operation on result that does not support it");
		}
	}
	
	public String getFailureMessage() {
		requireSuccess(false);
		
		assert failureMessage != null;
		
		return failureMessage;
	}
	
	public List<Token<T>> getRemainder() {
		assert remainder != null;
		
		return Collections.unmodifiableList(remainder);
	}
	
	public ParseTreeNode getNode() {
		requireSuccess(true);
		
		assert node != null;
		
		return node.get();
	}
}
