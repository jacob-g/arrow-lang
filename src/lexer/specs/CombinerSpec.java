package lexer.specs;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import lexer.StringToken;
import lexer.TokenLexResult;

public final class CombinerSpec<T> implements TokenSpec<T> {
	private final TokenSpec<T> innerSpec;
	
	private CombinerSpec(TokenSpec<T> innerSpec) {
		assert innerSpec != null;
		
		this.innerSpec = innerSpec;
	}
	
	public static <T> TokenSpec<T> of(TokenSpec<T> innerSpec) {
		Objects.requireNonNull(innerSpec);
		
		return new CombinerSpec<T>(innerSpec);
	}
	
	@Override
	public TokenLexResult<T> parse(String text) {
		Objects.requireNonNull(text);
		
		assert innerSpec != null;
		
		TokenLexResult<T> result = innerSpec.parse(text);
		
		return result.getSuccess() ? 
				TokenLexResult.of(
					Arrays.asList(
						StringToken.of(
							result.getResults()
								.stream()
								.map(token -> token.getContent())
								.collect(Collectors.joining())
						)
					),
					result.getRemainder())
				: TokenLexResult.failure();
	}
}
