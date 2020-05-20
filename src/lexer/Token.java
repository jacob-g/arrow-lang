package lexer;

public interface Token<T> {	
	String getContent();
	T getType();
}
