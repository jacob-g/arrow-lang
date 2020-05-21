package lexer.specs;

import lexer.TokenLexResult;

public interface TokenSpec<T> {	
	public static <T> TokenSpec<T> getLetterSpec() {
		return CharPredicateSpec.of(Character::isAlphabetic);
	}
	
	public static <T> TokenSpec<T> getDigitSpec() {
		return CharPredicateSpec.of(Character::isDigit);
	}
	
	public static <T> TokenSpec<T> getAlnumSpec() {
		return MultipleOptionTokenSpec.of(getLetterSpec(), getDigitSpec());
	}
	
	TokenLexResult<T> parse(String text);
}
