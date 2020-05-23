package parser.tree;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public final class ArgumentParseTreeNode extends AbstractParseTreeNode {
	private final List<ParseTreeNode> arguments;
	
	private ArgumentParseTreeNode(List<ParseTreeNode> arguments) {
		assert arguments != null;
		assert arguments.stream().allMatch(node -> node.getType() == ParseTreeNodeType.VARIABLE);
		
		this.arguments = arguments;
	}
	
	public static ArgumentParseTreeNode of(List<ParseTreeNode> arguments) {
		Objects.requireNonNull(arguments);
		
		if (!arguments.stream().allMatch(node -> node.getType() == ParseTreeNodeType.VARIABLE)) {
			throw new IllegalArgumentException("Non-variable arguments passed to ArgumentParseTreeNpde");
		}
		
		return new ArgumentParseTreeNode(arguments);
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		return Collections.unmodifiableList(arguments);
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.ARGUMENTS;
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
