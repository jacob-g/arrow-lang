package typesystem;

import java.util.Objects;
import java.util.Optional;

import memory.MemoryEntry;
import parser.tree.ParseTreeNodeType;

public final class ArrayType implements Type {
	private final Type underlyingType;
	
	private ArrayType(Type underlyingType) {
		this.underlyingType = underlyingType;
	}
	
	public static ArrayType of(Type underlyingType) {
		Objects.requireNonNull(underlyingType);
		return new ArrayType(underlyingType);
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
	public boolean isArrayType() {
		return true;
	}

	@Override
	public Type getUnderlyingType() {
		return underlyingType;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other.isArrayType() && getUnderlyingType().canBeAssignedTo(other.getUnderlyingType());
	}

	public MemoryEntry newEntry() {
		//TODO: implement this
		throw new UnsupportedOperationException();
	}
}
