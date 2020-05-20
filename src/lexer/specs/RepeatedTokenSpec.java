package lexer.specs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lexer.Token;
import lexer.TokenLexResult;

public final class RepeatedTokenSpec<T> implements TokenSpec<T> {
	private final TokenSpec<T> spec;
	private final boolean mustBePresentOnce;
	
	private RepeatedTokenSpec(TokenSpec<T> spec, boolean mustBePresentOnce) {
		assert spec != null;
		
		this.spec = spec;
		this.mustBePresentOnce = mustBePresentOnce;
	}
	
	public static <T> TokenSpec<T> of(TokenSpec<T> spec, boolean mustBePresentOnce) {
		assert spec != null;
		
		return new RepeatedTokenSpec<T>(spec, mustBePresentOnce);
	}
	
	public TokenLexResult<T> parse(String text) {
		Objects.requireNonNull(text);
		
		assert spec != null;
		
		List<Token<T>> outTokens = new ArrayList<>();
		String remainder = text;
		boolean anyOccurrences = false;
		boolean moreToParse = true;
		int lastRemainderLength = text.length();
		
		do {
			TokenLexResult<T> result = spec.parse(remainder);
			
			if (result.getSuccess()) {
				anyOccurrences = true;
				outTokens.addAll(result.getResults());
				remainder = result.getRemainder();
				
				moreToParse = remainder.length() < lastRemainderLength;
				
				lastRemainderLength = remainder.length();
			} else {
				moreToParse = false;
			}
		} while (!remainder.isEmpty() && moreToParse);
		
		return anyOccurrences || !mustBePresentOnce ? TokenLexResult.of(outTokens, remainder) : TokenLexResult.failure(text);
	}
}
