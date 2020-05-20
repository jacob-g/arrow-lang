package parser.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import arrow.symboltable.SymbolTableEntry;

public class DeclarationParseTreeNode extends AbstractParseTreeNode {

	private DeclarationParseTreeNode(ParseTreeNode identifier) {
		assert identifier != null;
		
		setAttribute(ParseTreeAttributeType.IDENTIFIER, identifier);
	}
	
	public static DeclarationParseTreeNode of(ParseTreeNode identifier) {
		Objects.requireNonNull(identifier);
		
		return new DeclarationParseTreeNode(identifier);
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		return new ArrayList<>();
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.DECLARATION;
	}

	@Override
	public SymbolTableEntry getIdentifier() {
		assert false;
		return null;
	}

}
