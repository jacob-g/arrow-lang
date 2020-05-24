package parser.tree;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public class VariableParseTreeNode extends AbstractParseTreeNode {
	private final SymbolTableEntry identifier;
	private final List<ParseTreeNode> subscripts;
	
	private VariableParseTreeNode(SymbolTableEntry identifier, List<ParseTreeNode> subscripts) {
		assert identifier != null;
		
		this.identifier = identifier;
		this.subscripts = subscripts;
	}
	
	public static VariableParseTreeNode of(SymbolTableEntry identifier, List<ParseTreeNode> subscripts) {
		Objects.requireNonNull(identifier);
		Objects.requireNonNull(subscripts);
		
		return new VariableParseTreeNode(identifier, subscripts);
	}

	@Override
	public List<ParseTreeNode> getChildren() {
		return Collections.unmodifiableList(subscripts);
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

	@Override
	public Type getDataType() {
		return identifier.getDataType();
	}
}
