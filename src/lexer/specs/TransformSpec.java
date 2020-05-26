package lexer.specs;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lexer.Token;

import lexer.TokenLexResult;

public final class TransformSpec<T> implements TokenSpec<T> {

	private final Function<List<Token<T>>, List<Token<T>>> transformation;
	private final TokenSpec<T> innerSpec;
	
	private TransformSpec(TokenSpec<T> innerSpec, Function<List<Token<T>>, List<Token<T>>> transformation) {
		assert innerSpec != null;
		assert transformation != null;
		
		this.innerSpec = innerSpec;
		this.transformation = transformation;
	}
	
	public static <T> TransformSpec<T> of(TokenSpec<T> innerSpec, Function<List<Token<T>>, List<Token<T>>> transformation) {
		Objects.requireNonNull(innerSpec);
		Objects.requireNonNull(transformation);
		
		return new TransformSpec<T>(innerSpec, transformation);
	}

	@Override
	public TokenLexResult<T> parse(String text) {
		TokenLexResult<T> underlyingResult = innerSpec.parse(text);
		
		return underlyingResult.getSuccess()
				? TokenLexResult.of(transformation.apply(underlyingResult.getResults()), underlyingResult.getRemainder())
				: underlyingResult;
	}

}
