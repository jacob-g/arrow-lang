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
	private final ParseTreeNode value;
	private final ParseTreeNodeType type;
	private final Type returnType;
	
	private BuiltInFunctionNode(ParseTreeNode value, ParseTreeNodeType type, Type returnType) {
		assert value != null;
		assert allowedTypes.contains(type);
		
		this.value = value;
		this.type = type;
		this.returnType = returnType;
	}
	
	private static final Set<ParseTreeNodeType> allowedTypes = new HashSet<>(Arrays.asList(ParseTreeNodeType.ARRAY_LENGTH, ParseTreeNodeType.PRINT));
	
	@Override
	public List<ParseTreeNode> getChildren() {
		assert value != null;
		
		return Arrays.asList(value);
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
		
		return new BuiltInFunctionNode(node, ParseTreeNodeType.ARRAY_LENGTH, IntegerType.getInstance());
	}

	public static ParseTreeNode print(ParseTreeNode node) {
		Objects.requireNonNull(node);
		
		return new BuiltInFunctionNode(node, ParseTreeNodeType.PRINT, null);
	}
}
