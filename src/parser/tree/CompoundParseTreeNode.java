package parser.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import arrow.symboltable.SymbolTableEntry;

public class CompoundParseTreeNode extends AbstractParseTreeNode {

	private final List<ParseTreeNode> children;
	
	private CompoundParseTreeNode(List<ParseTreeNode> children) {
		this.children = children;
	}
	
	public static CompoundParseTreeNode of(List<ParseTreeNode> children) {
		Objects.requireNonNull(children);
		
		return new CompoundParseTreeNode(new ArrayList<>(children));
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.COMPOUND;
	}

	@Override
	public SymbolTableEntry getIdentifier() {
		assert false;
		return null;
	}

}
