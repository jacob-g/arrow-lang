package typesystem;

import java.util.Optional;

import parser.tree.ParseTreeNodeType;

public final class ArrayType implements Type {
	private final Type underlyingType;
	
	private ArrayType(Type underlyingType) {
		this.underlyingType = underlyingType;
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
		// TODO Auto-generated method stub
		return underlyingType;
	}

}
