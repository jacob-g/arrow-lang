package parser.tree;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;

public class ControlParseTreeNode extends AbstractParseTreeNode {

	private final List<ParseTreeNode> children;
	private final ParseTreeNodeType type;
	
	private ControlParseTreeNode(ParseTreeNodeType type, ParseTreeNode condition, List<ParseTreeNode> children) {
		assert type != null;
		assert children != null;
		
		this.type = type;
		setAttribute(ParseTreeAttributeType.TEST, condition);
		this.children = children;
	}
	
	private static final Set<ParseTreeNodeType> controlTypes = new HashSet<>(Arrays.asList(ParseTreeNodeType.IF, ParseTreeNodeType.LOOP));
	public static ControlParseTreeNode of(ParseTreeNodeType type, ParseTreeNode condition, List<ParseTreeNode> children) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(condition);
		Objects.requireNonNull(children);
		
		if (!controlTypes.contains(type)) {
			throw new IllegalArgumentException("Illegal type provided to control node");
		}
		
		return new ControlParseTreeNode(type, condition, children);
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public ParseTreeNodeType getType() {
		assert type != null && controlTypes.contains(type);
		
		return type;
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
