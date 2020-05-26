package typesystem;

import java.util.List;
import java.util.Optional;

import memory.MemoryEntry;
import parser.tree.ParseTreeNodeType;

public final class AnyType implements Type {
	private AnyType() {
		
	}
	
	private static final AnyType instance = new AnyType();
	
	public static AnyType getInstance() {
		return instance;
	}
	
	@Override
	public Optional<Type> binaryOperationResult(ParseTreeNodeType operation, Type other) {
		assert false;
		return null;
	}

	@Override
	public Optional<Type> unaryOperationResult(ParseTreeNodeType operation) {
		assert false;
		return null;
	}

	@Override
	public boolean isCompatibleWith(Type other) {
		return true;
	}

	@Override
	public boolean isArrayType() {
		assert false;
		return false;
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
		assert false;
		return null;
	}

}
