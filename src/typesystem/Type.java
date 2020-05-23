package typesystem;

import java.util.Optional;

import parser.tree.ParseTreeNodeType;

public interface Type {
	Optional<Type> binaryOperationResult(ParseTreeNodeType operation, Type other);
	Optional<Type> unaryOperationResult(ParseTreeNodeType operation);
	boolean canBeAssignedTo(Type other);
	boolean isArrayType();
	Type getUnderlyingType();
}
