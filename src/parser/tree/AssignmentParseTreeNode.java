package parser.tree;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public class AssignmentParseTreeNode extends AbstractParseTreeNode {
	private final ParseTreeNode value;
	
	private AssignmentParseTreeNode(ParseTreeNode variable, ParseTreeNode value) {
		assert variable != null;
		assert value != null;
		
		setAttribute(ParseTreeAttributeType.IDENTIFIER, variable);
		this.value = value;
	}
	
	public static AssignmentParseTreeNode of(ParseTreeNode variable, ParseTreeNode value) {
		Objects.requireNonNull(variable);
		Objects.requireNonNull(value);
		
		if (variable.getType() != ParseTreeNodeType.VARIABLE) {
			throw new IllegalArgumentException("Variable passed to assignment must be of type variable");
		}
		
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
