package parser.tree;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;

public final class FunctionParseTreeNode extends AbstractParseTreeNode {
	private final List<ParseTreeNode> body;
	private final SymbolTableEntry identifier;
	
	private FunctionParseTreeNode(List<ParseTreeNode> body, SymbolTableEntry identifier) {
		assert body != null;
		assert identifier != null && identifier.getType() == SymbolTableEntryType.FUNCTION;
		
		this.body = body;
		this.identifier = identifier;
	}
	
	public static FunctionParseTreeNode of(List<ParseTreeNode> body, SymbolTableEntry identifier) {
		Objects.requireNonNull(body);
		Objects.requireNonNull(identifier);
		
		if (identifier.getType() != SymbolTableEntryType.FUNCTION) {
			throw new IllegalArgumentException("Function identifier must be of type function");
		}
		
		return new FunctionParseTreeNode(body, identifier);
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		return Collections.unmodifiableList(body);
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.FUNCTION;
	}

	@Override
	public SymbolTableEntry getIdentifier() {
		assert identifier != null && identifier.getType() == SymbolTableEntryType.FUNCTION;
		
		return identifier;
	}

	@Override
	public MemoryEntry getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
