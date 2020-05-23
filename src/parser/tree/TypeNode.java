package parser.tree;

import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public final class TypeNode extends AbstractParseTreeNode {
	private final Type dataType;
	
	public TypeNode(Type dataType) {
		assert dataType != null;
		
		this.dataType = dataType;
	}

	public static TypeNode of(Type dataType) {
		Objects.requireNonNull(dataType);
		
		return new TypeNode(dataType);
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		assert false;
		return null;
	}

	@Override
	public ParseTreeNodeType getType() {
		assert false;
		return null;
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
		return dataType;
	}

}
