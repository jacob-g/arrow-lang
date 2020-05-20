package lexer;

import java.util.Objects;

public class TypedToken<T> extends AbstractToken<T> {
	private final Token<T> underlyingToken;
	private final T type;
	
	private TypedToken(Token<T> underlyingToken, T type) {
		assert underlyingToken != null;
		assert type != null;
		
		this.underlyingToken = underlyingToken;
		this.type = type;
	}
	
	public static <T> Token<T> of(Token<T> underlyingToken, T type) {
		Objects.requireNonNull(underlyingToken);
		Objects.requireNonNull(type);
		
		return new TypedToken<T>(underlyingToken, type);
	}
	
	public T getType() {
		assert type != null;
		
		return type;
	}
	
	public String toString() {
		return "[" + getType() + ": " + getContent() + "]";
	}

	@Override
	public String getContent()	{
		assert underlyingToken != null;
		
		return underlyingToken.getContent();
	}
}
