package lexer;

import java.util.Objects;

public final class StringToken<T> extends AbstractToken<T> {
	private final String value;
	
	private StringToken(String value) {
		assert value != null;
		
		this.value = value;
	}
	
	public static <T> Token<T> of(String value) {
		Objects.requireNonNull(value);
		
		return new StringToken<T>(value);
	}
	
	public String getContent() {
		assert value != null;
		
		return value;
	}

	@Override
	public T getType() {
		throw new UnsupportedOperationException();
	}
}
