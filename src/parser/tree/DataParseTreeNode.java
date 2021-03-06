package parser.tree;

import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public class DataParseTreeNode extends AbstractParseTreeNode {
	private final MemoryEntry data;
	
	private DataParseTreeNode(MemoryEntry data) {
		this.data = data;
	}
	
	public static DataParseTreeNode of(MemoryEntry data) {
		Objects.requireNonNull(data);
		
		return new DataParseTreeNode(data);
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		assert false;
		return null;
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.DATA;
	}

	@Override
	public SymbolTableEntry getIdentifier() {
		assert false;
		return null;
	}

	@Override
	public MemoryEntry getData() {
		return data;
	}

	@Override
	public Type getDataType() {
		return data.getDataType();
	}

}
