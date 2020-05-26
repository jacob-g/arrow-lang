package typesystem;

import java.util.List;
import java.util.Optional;

import memory.MemoryEntry;
import parser.tree.ParseTreeNodeType;

public class GenericArrayType implements Type {
	private GenericArrayType() {
		
	}
	
	private static GenericArrayType instance = new GenericArrayType();
	
	public static GenericArrayType getInstance() {
		return instance;
	}
	
	@Override
	public Optional<Type> binaryOperationResult(ParseTreeNodeType operation, Type other) {
		return Optional.empty();
	}

	@Override
	public Optional<Type> unaryOperationResult(ParseTreeNodeType operation) {
		return Optional.empty();
	}

	@Override
	public boolean isCompatibleWith(Type other) {
		return other.isArrayType();
	}

	@Override
	public boolean isArrayType() {
		return true;
	}

	@Override
	public Type getUnderlyingType() {
		assert false;
		return null;
	}

	@Override
	public MemoryEntry newEntry() {
		assert false;
		return null;
	}

	@Override
	public MemoryEntry newEntry(List<Integer> sizes) {
		assert false;
		return null;
	}

	@Override
	public String toString(MemoryEntry entry) {
		return "array";
	}

}
