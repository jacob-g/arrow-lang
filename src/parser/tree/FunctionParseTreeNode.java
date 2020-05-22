package parser.tree;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;

public final class FunctionParseTreeNode extends AbstractParseTreeNode {
	private final List<ParseTreeNode> body;
	
	private FunctionParseTreeNode(List<ParseTreeNode> body, ParseTreeNode arguments) {
		assert body != null;
		assert arguments != null && arguments.getType() == ParseTreeNodeType.ARGUMENTS;
		
		this.body = body;
		setAttribute(ParseTreeAttributeType.ARGUMENTS, arguments);
	}
	
	public static FunctionParseTreeNode of(List<ParseTreeNode> body, ParseTreeNode arguments) {
		Objects.requireNonNull(body);

		if (arguments.getType() != ParseTreeNodeType.ARGUMENTS) {
			throw new IllegalArgumentException("Function arguments must be of arguments type");
		}
		
		return new FunctionParseTreeNode(body, arguments);
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
		assert false;
		return null;
	}

	@Override
	public MemoryEntry getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
