package lexer.specs;

import java.util.Objects;
import java.util.stream.Collectors;

import lexer.TokenLexResult;
import lexer.TypedToken;

public class TyperSpec<T> implements TokenSpec<T> {
	private final TokenSpec<T> innerSpec;
	private final T label;
	
	private TyperSpec(TokenSpec<T> innerSpec, T label) {
		assert innerSpec != null;
		
		this.innerSpec = innerSpec;
		this.label = label;
	}
	
	public static <T> TyperSpec<T> of(TokenSpec<T> innerSpec, T type) {
		Objects.requireNonNull(innerSpec);
		
		return new TyperSpec<T>(innerSpec, type);
	}

	@Override
	public TokenLexResult<T> parse(String text) {
		Objects.requireNonNull(text);
		
		TokenLexResult<T> result = innerSpec.parse(text);
		
		return result.getSuccess() ? TokenLexResult.of(
				result.getResults()
					.stream()
					.map(token -> TypedToken.of(token, label))
					.collect(Collectors.toList()),
				result.getRemainder()) 
				: result;
	}
	
	
}
