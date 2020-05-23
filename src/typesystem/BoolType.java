package typesystem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import parser.tree.ParseTreeNodeType;

public final class BoolType implements Type {
	private static final Set<ParseTreeNodeType> allowedBinaryOperations = new HashSet<>(Arrays.asList(ParseTreeNodeType.AND, ParseTreeNodeType.OR));
	
	private BoolType() {
		
	}
	
	private static BoolType instance = new BoolType();
	
	public static BoolType getInstance() {
		return instance;
	}
	
	@Override
	public Optional<Type> binaryOperationResult(ParseTreeNodeType operation, Type other) {
		return other == this && allowedBinaryOperations.contains(operation) ? Optional.of(getInstance()) : Optional.empty();
	}

	@Override
	public Optional<Type> unaryOperationResult(ParseTreeNodeType operation) {
		return Optional.empty();
	}
	
	public String toString() {
		return "bool";
	}
}
