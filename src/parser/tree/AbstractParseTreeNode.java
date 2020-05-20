package parser.tree;

public abstract class AbstractParseTreeNode implements ParseTreeNode {

	protected final void validateAttributeType(ParseTreeAttributeType attrType) {
		if (!getType().VISIBLE_ATTRIBUTES.contains(attrType)) {
			throw new IllegalArgumentException("Checking for non-existent attribute (" + attrType + ") for this type of node (" + getType() + ")");
		}
	}
}
