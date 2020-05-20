package lexer.specs;

import java.util.stream.Collectors;

import lexer.TokenLexResult;

public interface TokenSpec<T> {
	static <T> TokenSpec<T> stringToTokenFromChars(String str) {
		return MultipleOptionTokenSpec.of(
				str
				.chars()
				.mapToObj(c -> FixedStringTokenSpec.<T>of(Character.toString((char)c)))
				.collect(Collectors.toList()));
	}
	
	public static <T> TokenSpec<T> getLetterSpec() {
		return stringToTokenFromChars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
	}
	
	public static <T> TokenSpec<T> getDigitSpec() {
		return stringToTokenFromChars("0123456789");
	}
	
	public static <T> TokenSpec<T> getAlnumSpec() {
		return MultipleOptionTokenSpec.of(getLetterSpec(), getDigitSpec());
	}
	
	TokenLexResult<T> parse(String text);
}
