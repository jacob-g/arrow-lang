package parser.tree;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public enum ParseTreeNodeType {
	COMPOUND(new HashSet<>()),
	EMPTY(new HashSet<>()),
	STATEMENT(new HashSet<>()),
	DECLARATION(new HashSet<>(Arrays.asList(ParseTreeAttributeType.TYPE, ParseTreeAttributeType.IDENTIFIER))),
	ASSIGNMENT(new HashSet<>(Arrays.asList(ParseTreeAttributeType.IDENTIFIER, ParseTreeAttributeType.VALUE))),
	IF(new HashSet<>(Arrays.asList(ParseTreeAttributeType.TEST))),
	LOOP(new HashSet<>(Arrays.asList(ParseTreeAttributeType.TEST))),
	WHILE(new HashSet<>(Arrays.asList(ParseTreeAttributeType.TEST))),
	FUNCTION(new HashSet<>(Arrays.asList(ParseTreeAttributeType.ARGUMENTS, ParseTreeAttributeType.IDENTIFIER, ParseTreeAttributeType.TYPE))),
	DATA(new HashSet<>(Arrays.asList(ParseTreeAttributeType.TYPE))),
	ADD(new HashSet<>()),
	SUBTRACT(new HashSet<>()),
	MULTIPLY(new HashSet<>()),
	DIVIDE(new HashSet<>()),
	MODULO(new HashSet<>()),
	GREATER_THAN(new HashSet<>()),
	LESS_THAN(new HashSet<>()),
	EQUAL(new HashSet<>()),
	NOT_EQUAL(new HashSet<>()),
	AND(new HashSet<>()),
	OR(new HashSet<>()),
	VARIABLE(new HashSet<>(Arrays.asList(ParseTreeAttributeType.TYPE)));
	
	public final Set<ParseTreeAttributeType> VISIBLE_ATTRIBUTES;
	private ParseTreeNodeType(Set<ParseTreeAttributeType> attributes) {
		this.VISIBLE_ATTRIBUTES = Collections.unmodifiableSet(attributes);
	}
}
