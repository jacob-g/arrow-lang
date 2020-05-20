package lexer.specs;

import java.util.Arrays;
import java.util.Objects;

import lexer.StringToken;
import lexer.TokenLexResult;

public final class FixedStringTokenSpec<T> implements TokenSpec<T> {
	private final String value;
	
	private FixedStringTokenSpec(String value) {
		assert value != null;
		
		this.value = value;
	}
	
	public static <T> TokenSpec<T> of(String value) {
		Objects.requireNonNull(value);
		
		return new FixedStringTokenSpec<T>(value);
	}
	
	public TokenLexResult<T> parse(String text) {
		Objects.requireNonNull(text);
		
		assert value != null;
		
		return text.startsWith(value) ? TokenLexResult.of(Arrays.asList(StringToken.of(value)), text.substring(value.length())) : TokenLexResult.failure(text);
	}
}
