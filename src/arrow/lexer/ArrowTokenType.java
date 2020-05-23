package arrow.lexer;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public enum ArrowTokenType {
	IDENTIFIER(ArrowTokenCategory.IDENTIFIER),
	
	FUNCTION(ArrowTokenCategory.KEYWORD),
	MAIN(ArrowTokenCategory.KEYWORD),
	TRUE(ArrowTokenCategory.KEYWORD),
	FALSE(ArrowTokenCategory.KEYWORD),
	NUMBER(ArrowTokenCategory.NUMBER),
	
	//control flow symbols
	START_IF(ArrowTokenCategory.SYMBOL, "/--<"),
	END_IF(ArrowTokenCategory.SYMBOL, "\\-->"),
	BEGIN_DO_WHILE(ArrowTokenCategory.SYMBOL, "/-->"),
	END_DO_WHILE(ArrowTokenCategory.SYMBOL, "\\--<"),
	PIPE(ArrowTokenCategory.SYMBOL, "|"),
	
	//standard assignment, relational, math, etc. stuff
	RETURN(ArrowTokenCategory.SYMBOL, "^"),
	DOUBLE_EQUAL(ArrowTokenCategory.SYMBOL, "=="),
	NOT_EQUAL(ArrowTokenCategory.SYMBOL, "!="),
	LESS_THAN(ArrowTokenCategory.SYMBOL, "<"),
	GREATER_THAN(ArrowTokenCategory.SYMBOL, ">"),
	PLUS(ArrowTokenCategory.SYMBOL, "+"),
	MINUS(ArrowTokenCategory.SYMBOL, "-"),
	TIMES(ArrowTokenCategory.SYMBOL, "*"),
	DIVIDE(ArrowTokenCategory.SYMBOL, "/"),
	MODULO(ArrowTokenCategory.SYMBOL, "%"),
	COMMA(ArrowTokenCategory.SYMBOL, ","),
	AND(ArrowTokenCategory.SYMBOL, "and"),
	OR(ArrowTokenCategory.SYMBOL, "or"),
	NOT(ArrowTokenCategory.SYMBOL, "not"),
	SINGLE_EQUAL(ArrowTokenCategory.SYMBOL, "="),
	OPEN_PAREN(ArrowTokenCategory.SYMBOL, "("),
	CLOSE_PAREN(ArrowTokenCategory.SYMBOL, ")"),
	
	NEWLINE(ArrowTokenCategory.NEWLINE),
	WHITESPACE(ArrowTokenCategory.IGNORE);
	
	public final ArrowTokenCategory CATEGORY;
	public final String TEXT;
	private ArrowTokenType(ArrowTokenCategory category) {
		this.CATEGORY = category;
		this.TEXT = toString().toLowerCase();
	}
	
	private ArrowTokenType(ArrowTokenCategory category, String text) {
		this.CATEGORY = category;
		this.TEXT = text;
	}
	
	private static final Map<ArrowTokenCategory, Set<ArrowTokenType>> tokensByCategory = new HashMap<>();
	static {
		for (ArrowTokenType type : values()) {
			tokensByCategory.putIfAbsent(type.CATEGORY, new LinkedHashSet<>()); //we need the symbols to be added in the order that they're listed, which is why we're using a LinkedHashSet
			tokensByCategory.get(type.CATEGORY).add(type);
		}
	}
	
	public static Set<ArrowTokenType> tokensByCategory(ArrowTokenCategory category) {
		assert tokensByCategory.containsKey(category);
		
		return Collections.unmodifiableSet(tokensByCategory.get(category));
	}
}
