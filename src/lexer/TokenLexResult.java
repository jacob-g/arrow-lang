package lexer;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TokenLexResult<T> {
	private final Optional<List<Token<T>>> results;
	private final String remainder;
	
	private TokenLexResult(Optional<List<Token<T>>> results, String remainder) {
		assert results != null;
		
		this.results = results;
		this.remainder = remainder;
	}
	
	public static <T, L> TokenLexResult<T> of(List<Token<T>> results, String remainder) {
		Objects.requireNonNull(results);
		
		return new TokenLexResult<T>(Optional.of(results), remainder);
	}
	
	public static <T, L> TokenLexResult<T> failure() {
		return new TokenLexResult<T>(Optional.empty(), null);
	}
	
	public boolean getSuccess() {
		return results.isPresent();
	}
	
	private void requireSuccess() {
		if (!getSuccess()) {
			throw new IllegalStateException("Cannot get results from a non-successful parse");
		}
	}
	
	public List<Token<T>> getResults() {
		requireSuccess();
		
		assert results.isPresent();
		
		return Collections.unmodifiableList(results.get());
	}
	
	public String getRemainder() {
		requireSuccess();
		
		assert remainder != null;
		
		return remainder;
	}
	
	public String toString() {
		return getSuccess() ? results.get().stream().map(token -> token.toString()).collect(Collectors.joining(",", "[", "]")) + "\n" + "Remainder: " + getRemainder() : "<<<LEXER FAILURE>>>";
	}
}
