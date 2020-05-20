package parser.tree;

import java.util.List;

public interface ParseTreeNode {
	ParseTreeNode getAttribute(ParseTreeAttributeType type);
	List<ParseTreeNode> getChildren();
	ParseTreeNodeType getType();
	String getIdentifier();
}
