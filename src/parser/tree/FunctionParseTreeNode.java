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
	
	private FunctionParseTreeNode(List<ParseTreeNode> body, SymbolTableEntry identifier, ParseTreeNode arguments) {
		assert body != null;
		assert identifier != null && identifier.getType() == SymbolTableEntryType.FUNCTION;
		assert arguments != null && arguments.getType() == ParseTreeNodeType.ARGUMENTS;
		
		this.body = body;
		this.identifier = identifier;
		setAttribute(ParseTreeAttributeType.ARGUMENTS, arguments);
	}
	
	public static FunctionParseTreeNode of(List<ParseTreeNode> body, SymbolTableEntry identifier, ParseTreeNode arguments) {
		Objects.requireNonNull(body);
		Objects.requireNonNull(identifier);
		
		if (identifier.getType() != SymbolTableEntryType.FUNCTION) {
			throw new IllegalArgumentException("Function identifier must be of type function");
		}
		
		if (arguments.getType() != ParseTreeNodeType.ARGUMENTS) {
			throw new IllegalArgumentException("Function arguments must be of arguments type");
		}
		
		return new FunctionParseTreeNode(body, identifier, arguments);
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
