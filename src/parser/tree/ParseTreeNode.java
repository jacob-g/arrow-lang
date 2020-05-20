package parser.tree;

import java.util.List;

import arrow.symboltable.SymbolTableEntry;

public interface ParseTreeNode {
	ParseTreeNode getAttribute(ParseTreeAttributeType type);
	List<ParseTreeNode> getChildren();
	ParseTreeNodeType getType();
	SymbolTableEntry getIdentifier();
}