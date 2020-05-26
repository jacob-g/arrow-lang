package parser.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

abstract class AbstractParseTreeNode implements ParseTreeNode {
	private Map<ParseTreeAttributeType, ParseTreeNode> attributes = new HashMap<>();
	
	protected final void setAttribute(ParseTreeAttributeType attrType, ParseTreeNode node) {
		validateAttributeType(attrType);
		
		attributes.put(attrType, node);
	}
	
	@Override
	public final ParseTreeNode getAttribute(ParseTreeAttributeType attrType) {
		validateAttributeType(attrType);
		
		return attributes.get(attrType);
	}

	protected final void validateAttributeType(ParseTreeAttributeType attrType) {
		Objects.requireNonNull(attrType);
		if (!getType().VISIBLE_ATTRIBUTES.contains(attrType)) {
			throw new IllegalArgumentException("Checking for non-existent attribute (" + attrType + ") for this type of node (" + getType() + ")");
		}
	}
}
