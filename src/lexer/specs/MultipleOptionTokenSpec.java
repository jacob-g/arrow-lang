package lexer.specs;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lexer.TokenLexResult;

public final class MultipleOptionTokenSpec<T> implements TokenSpec<T> {
	private final List<TokenSpec<T>> options;
	
	private MultipleOptionTokenSpec(List<TokenSpec<T>> options) {
		assert options != null;
		
		this.options = options;
	}
	
	public static <T> TokenSpec<T> of(List<TokenSpec<T>> options) {
		Objects.requireNonNull(options);
		
		return new MultipleOptionTokenSpec<T>(options);
	}
	
	@SafeVarargs
	public static <T> TokenSpec<T> of(TokenSpec<T>... options) {
		Objects.requireNonNull(options);
		
		return of(Arrays.asList(options));
	}
	
	public TokenLexResult<T> parse(String text) {
		Objects.requireNonNull(text);
		
		assert options != null;
		
		for (TokenSpec<T> spec : options) {
			TokenLexResult<T> result = spec.parse(text);
			if (result.getSuccess()) {
				return result;
			}
		}
		
		return TokenLexResult.failure();
	}
}
