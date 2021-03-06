package typesystem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import memory.MemoryEntry;
import memory.ScalarMemoryEntry;
import parser.tree.ParseTreeNodeType;

public final class IntegerType implements Type {
	private IntegerType() {
		
	}
	
	private static IntegerType instance = new IntegerType();
	
	private static final Set<ParseTreeNodeType> allowedBinaryOperationsSameType = new HashSet<>(Arrays.asList(ParseTreeNodeType.ADD, ParseTreeNodeType.SUBTRACT, ParseTreeNodeType.MULTIPLY, ParseTreeNodeType.DIVIDE, ParseTreeNodeType.MODULO));
	private static final Set<ParseTreeNodeType> allowedBinaryOperationsBoolean = new HashSet<>(Arrays.asList(ParseTreeNodeType.GREATER_THAN, ParseTreeNodeType.LESS_THAN, ParseTreeNodeType.EQUAL, ParseTreeNodeType.NOT_EQUAL));
	
	public static IntegerType getInstance() {
		return instance;
	}
	
	@Override
	public Optional<Type> binaryOperationResult(ParseTreeNodeType operation, Type other) {
		if (other != this) {
			return Optional.empty();
		}
		
		if (allowedBinaryOperationsSameType.contains(operation)) {
			return Optional.of(getInstance());
		}
		
		if (allowedBinaryOperationsBoolean.contains(operation)) {
			return Optional.of(BoolType.getInstance());
		}
		
		return Optional.empty();
	}

	@Override
	public Optional<Type> unaryOperationResult(ParseTreeNodeType operation) {
		return operation == ParseTreeNodeType.NEGATE ? Optional.of(getInstance()) : Optional.empty();
	}

	public String toString() {
		return "int";
	}

	@Override
	public boolean isArrayType() {
		return false;
	}

	@Override
	public Type getUnderlyingType() {
		assert false;
		return null;
	}

	@Override
	public boolean isCompatibleWith(Type other) {
		return other == IntegerType.getInstance();
	}
	
	public MemoryEntry newEntry() {
		return ScalarMemoryEntry.uninitialized(this);
	}

	@Override
	public MemoryEntry newEntry(List<Integer> sizes) {
		assert false;
		return null;
	}

	@Override
	public String toString(MemoryEntry entry) {
		assert entry.getDataType().isCompatibleWith(this);
		
		return entry.isInitialized() ? Integer.toString(entry.getScalarValue()) : "uninitialized int";
	}
}
