package parser.tree;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import arrow.symboltable.SymbolTableEntry;
import memory.MemoryEntry;

public class IfParseTreeNode extends AbstractParseTreeNode {

	private final List<ParseTreeNode> children;
	
	private IfParseTreeNode(ParseTreeNode condition, List<ParseTreeNode> children) {
		setAttribute(ParseTreeAttributeType.TEST, condition);
		this.children = children;
	}
	
	public static IfParseTreeNode of(ParseTreeNode condition, List<ParseTreeNode> children) {
		Objects.requireNonNull(condition);
		Objects.requireNonNull(children);
		
		return new IfParseTreeNode(condition, children);
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.IF;
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

}
