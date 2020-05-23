package parser.tree;

import java.util.ArrayList;
import java.util.List;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public class EmptyParseTreeNode extends AbstractParseTreeNode {
	@Override
	public List<ParseTreeNode> getChildren() {
		return new ArrayList<>();
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.EMPTY;
	}

	@Override
	public SymbolTableEntry getIdentifier() {
		assert false;
		return null;
	}

	@Override
	public MemoryEntry getData() {
		assert false;
		return null;
	}

	@Override
	public Type getDataType() {
		assert false;
		return null;
	}
}
