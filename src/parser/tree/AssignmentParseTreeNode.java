package parser.tree;

import java.util.List;
import java.util.Objects;

import arrow.symboltable.SymbolTableEntry;

public class AssignmentParseTreeNode extends AbstractParseTreeNode {
	private AssignmentParseTreeNode(VariableParseTreeNode variable) {
		assert variable != null;
		
		setAttribute(ParseTreeAttributeType.IDENTIFIER, variable);
	}
	
	//TODO: make this take a value too
	public static AssignmentParseTreeNode of(VariableParseTreeNode variable) {
		Objects.requireNonNull(variable);
		
		return new AssignmentParseTreeNode(variable);
	}

	@Override
	public List<ParseTreeNode> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.ASSIGNMENT;
	}

	@Override
	public SymbolTableEntry getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}
}
