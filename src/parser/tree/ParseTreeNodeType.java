package parser.tree;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public enum ParseTreeNodeType {
	STATEMENT(new HashSet<>()),
	DECLARATION(new HashSet<>(Arrays.asList(ParseTreeAttributeType.TYPE, ParseTreeAttributeType.IDENTIFIER))),
	ASSIGNMENT(new HashSet<>(Arrays.asList(ParseTreeAttributeType.TYPE, ParseTreeAttributeType.VALUE))),
	IF(new HashSet<>(Arrays.asList(ParseTreeAttributeType.TEST))),
	WHILE(new HashSet<>(Arrays.asList(ParseTreeAttributeType.TEST)));
	
	public final Set<ParseTreeAttributeType> VISIBLE_ATTRIBUTES;
	private ParseTreeNodeType(Set<ParseTreeAttributeType> attributes) {
		this.VISIBLE_ATTRIBUTES = Collections.unmodifiableSet(attributes);
	}
}
