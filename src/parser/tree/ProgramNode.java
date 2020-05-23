package parser.tree;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public final class ProgramNode extends AbstractParseTreeNode {
	private final List<ParseTreeNode> children;
	
	private ProgramNode(List<ParseTreeNode> children) {
		assert children != null;
		
		this.children = children;
	}
	
	public static ProgramNode of(List<ParseTreeNode> children) {
		Objects.requireNonNull(children);
		
		return new ProgramNode(children);
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		assert children != null;
		return Collections.unmodifiableList(children);
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.COMPOUND;
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
