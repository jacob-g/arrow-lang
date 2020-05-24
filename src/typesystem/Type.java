package typesystem;

import java.util.List;
import java.util.Optional;

import memory.MemoryEntry;
import parser.tree.ParseTreeNodeType;

public interface Type {
	Optional<Type> binaryOperationResult(ParseTreeNodeType operation, Type other);
	Optional<Type> unaryOperationResult(ParseTreeNodeType operation);
	boolean canBeAssignedTo(Type other);
	boolean isArrayType();
	Type getUnderlyingType();
	MemoryEntry newEntry();
	MemoryEntry newEntry(List<Integer> sizes);
}
