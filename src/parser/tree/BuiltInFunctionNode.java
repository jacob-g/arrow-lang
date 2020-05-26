package parser.tree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.IntegerType;
import typesystem.Type;

public class BuiltInFunctionNode extends AbstractParseTreeNode {
	private final List<ParseTreeNode> values;
	private final ParseTreeNodeType type;
	private final Type returnType;
	
	private BuiltInFunctionNode(List<ParseTreeNode> values, ParseTreeNodeType type, Type returnType) {
		assert allowedTypes.contains(type);
		
		this.values = values;
		this.type = type;
		this.returnType = returnType;
	}
	
	private static final Set<ParseTreeNodeType> allowedTypes = new HashSet<>(Arrays.asList(ParseTreeNodeType.ARRAY_LENGTH, ParseTreeNodeType.PRINT, ParseTreeNodeType.INPUT));
	
	@Override
	public List<ParseTreeNode> getChildren() {
		assert values != null : "getting children where none exist";
		
		return values;
	}

	@Override
	public ParseTreeNodeType getType() {
		assert allowedTypes.contains(type);
		
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

	@Override
	public Type getDataType() {
		return returnType;
	}

	public static ParseTreeNode arrayLength(ParseTreeNode node) {
		Objects.requireNonNull(node);
		
		return new BuiltInFunctionNode(Arrays.asList(node), ParseTreeNodeType.ARRAY_LENGTH, IntegerType.getInstance());
	}

	public static ParseTreeNode print(List<ParseTreeNode> nodes) {
		Objects.requireNonNull(nodes);
		
		return new BuiltInFunctionNode(nodes, ParseTreeNodeType.PRINT, null);
	}
	
	public static ParseTreeNode input(Type returnType) {
		Objects.requireNonNull(returnType);
		
		return new BuiltInFunctionNode(null, ParseTreeNodeType.INPUT, returnType);
	}
}
