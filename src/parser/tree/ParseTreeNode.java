package parser.tree;

import java.util.List;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public interface ParseTreeNode {
	ParseTreeNode getAttribute(ParseTreeAttributeType type);
	List<ParseTreeNode> getChildren();
	ParseTreeNodeType getType();
	SymbolTableEntry getIdentifier();
	MemoryEntry getData();
	Type getDataType();
}
