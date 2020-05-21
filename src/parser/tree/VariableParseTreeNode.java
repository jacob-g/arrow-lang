package parser.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import arrow.symboltable.SymbolTableEntry;
import memory.MemoryEntry;

public class VariableParseTreeNode extends AbstractParseTreeNode {
	private final SymbolTableEntry identifier;
	
	private VariableParseTreeNode(SymbolTableEntry identifier) {
		assert identifier != null;
		
		this.identifier = identifier;
	}
	
	public static VariableParseTreeNode of(SymbolTableEntry identifier) {
		Objects.requireNonNull(identifier);
		
		return new VariableParseTreeNode(identifier);
	}

	@Override
	public List<ParseTreeNode> getChildren() {
		return new ArrayList<>();
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.VARIABLE;
	}

	@Override
	public SymbolTableEntry getIdentifier() {
		return identifier;
	}

	@Override
	public MemoryEntry getData() {
		assert false;
		return null;
	}
}
