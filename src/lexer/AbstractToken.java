package lexer;

public abstract class AbstractToken<T> implements Token<T> {
	public String toString() {
		return "[" + getType() + ": " + getContent() + "]";
	}
}
