package parser.tree;

import java.util.List;

public interface ParseTreeNode {
	ParseTreeNode getAttribute();
	List<ParseTreeNode> getChildren();
	ParseTreeNodeType getType();
}
