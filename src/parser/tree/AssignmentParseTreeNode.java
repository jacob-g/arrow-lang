package parser.tree;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public class AssignmentParseTreeNode extends AbstractParseTreeNode {
	private final ParseTreeNode value;
	
	private AssignmentParseTreeNode(VariableParseTreeNode variable, ParseTreeNode value) {
		assert variable != null;
		assert value != null;
		
		setAttribute(ParseTreeAttributeType.IDENTIFIER, variable);
		this.value = value;
	}
	
	//TODO: make this take a value too
	public static AssignmentParseTreeNode of(VariableParseTreeNode variable, ParseTreeNode value) {
		Objects.requireNonNull(variable);
		Objects.requireNonNull(value);
		
		return new AssignmentParseTreeNode(variable, value);
	}

	@Override
	public List<ParseTreeNode> getChildren() {
		return Arrays.asList(value);
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.ASSIGNMENT;
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
