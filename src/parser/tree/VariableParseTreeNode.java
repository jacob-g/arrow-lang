package parser.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VariableParseTreeNode extends AbstractParseTreeNode {
	private final String identifier;
	
	private VariableParseTreeNode(String identifier) {
		assert identifier != null;
		
		this.identifier = identifier;
	}
	
	public static VariableParseTreeNode of(String identifier) {
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
	public String getIdentifier() {
		return identifier;
	}
}
