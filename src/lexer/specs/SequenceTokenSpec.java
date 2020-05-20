package lexer.specs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lexer.Token;
import lexer.TokenLexResult;

public final class SequenceTokenSpec<T> implements TokenSpec<T> {
	private final List<TokenSpec<T>> specs;
	
	private SequenceTokenSpec(List<TokenSpec<T>> specs) {
		assert specs != null;
		
		this.specs = specs;
	}
	
	public static <T> TokenSpec<T> of(List<TokenSpec<T>> specs) {
		Objects.requireNonNull(specs);
		
		return new SequenceTokenSpec<T>(specs);
	}
	
	@SafeVarargs
	public static <T> TokenSpec<T> of(TokenSpec<T>... specs) {
		Objects.requireNonNull(specs);
		
		return of(Arrays.asList(specs));
	}
	
	@Override
	public TokenLexResult<T> parse(String text) {
		Objects.requireNonNull(text);
		
		assert specs != null;
		
		String remainder = text;
		List<Token<T>> outResults = new ArrayList<>();
		
		for (TokenSpec<T> spec : specs) {
			TokenLexResult<T> result = spec.parse(remainder);
			
			if (result.getSuccess()) {
				outResults.addAll(result.getResults());
				remainder = result.getRemainder();
			} else {
				return TokenLexResult.failure();
			}
		}
		
		return TokenLexResult.of(outResults, remainder);
	}

}
