package parser.tree;

import java.util.ArrayList;
import java.util.List;

import arrow.symboltable.SymbolTableEntry;

public class EmptyParseTreeNode extends AbstractParseTreeNode {
	@Override
	public List<ParseTreeNode> getChildren() {
		return new ArrayList<>();
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.EMPTY;
	}

	@Override
	public SymbolTableEntry getIdentifier() {
		assert false;
		return null;
	}
}
