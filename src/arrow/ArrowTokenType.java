package arrow;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum ArrowTokenType {
	IDENTIFIER(ArrowTokenCategory.IDENTIFIER),
	
	FUNCTION(ArrowTokenCategory.KEYWORD),
	MAIN(ArrowTokenCategory.KEYWORD),
	NUMBER(ArrowTokenCategory.NUMBER),
	
	BOOL(ArrowTokenCategory.TYPE),
	INT(ArrowTokenCategory.TYPE),
	
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
	COMMA(ArrowTokenCategory.SYMBOL, ","),
	AND(ArrowTokenCategory.SYMBOL, "&&"),
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
			tokensByCategory.putIfAbsent(type.CATEGORY, new HashSet<>());
			tokensByCategory.get(type.CATEGORY).add(type);
		}
	}
	
	public static Set<ArrowTokenType> tokensByCategory(ArrowTokenCategory category) {
		assert tokensByCategory.containsKey(category);
		
		return Collections.unmodifiableSet(tokensByCategory.get(category));
	}
}
