package lexer.specs;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import lexer.StringToken;
import lexer.TokenLexResult;

public final class CharPredicateSpec<T> implements TokenSpec<T> {
	private final Predicate<Character> predicate;
	
	private CharPredicateSpec(Predicate<Character> predicate) {
		assert predicate != null;
		
		this.predicate = predicate;
	}
	
	public static <T> TokenSpec<T> of(Predicate<Character> predicate) {
		Objects.requireNonNull(predicate);
		
		return new CharPredicateSpec<T>(predicate);
	}
	
	@Override
	public TokenLexResult<T> parse(String text) {
		if (text.isEmpty()) {
			return TokenLexResult.failure("No characters to match");
		}
		
		return predicate.test(text.charAt(0)) ? TokenLexResult.of(Arrays.asList(StringToken.of(text.substring(0, 1))), text.substring(1)) : TokenLexResult.failure("Predicate did not match");
	}
	
}
