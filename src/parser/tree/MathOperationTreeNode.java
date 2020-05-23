package parser.tree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public class MathOperationTreeNode extends AbstractParseTreeNode {
	private final ParseTreeNodeType operation;
	private final ParseTreeNode firstOperand;
	private final Optional<ParseTreeNode> secondOperand;
	
	private MathOperationTreeNode(ParseTreeNodeType operation, ParseTreeNode firstOperand, Optional<ParseTreeNode> secondOperand) {
		assert firstOperand != null;
		assert secondOperand != null;
		
		this.operation = operation;
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}
	
	private static final Set<ParseTreeNodeType> allowedBinaryOperations = new HashSet<>(Arrays.asList(
			ParseTreeNodeType.ADD, 
			ParseTreeNodeType.SUBTRACT, 
			ParseTreeNodeType.MULTIPLY, 
			ParseTreeNodeType.DIVIDE,
			ParseTreeNodeType.MODULO,
			ParseTreeNodeType.EQUAL,
			ParseTreeNodeType.NOT_EQUAL, 
			ParseTreeNodeType.GREATER_THAN,
			ParseTreeNodeType.LESS_THAN,
			ParseTreeNodeType.AND,
			ParseTreeNodeType.OR,
			ParseTreeNodeType.NOT));
	
	private static final Set<ParseTreeNodeType> allowedUnaryOperations = new HashSet<>(Arrays.asList(
			ParseTreeNodeType.NEGATE, 
			ParseTreeNodeType.NOT));
	
	public static MathOperationTreeNode of(ParseTreeNodeType operation, ParseTreeNode firstOperand, ParseTreeNode secondOperand) {
		Objects.requireNonNull(operation);
		Objects.requireNonNull(firstOperand);
		Objects.requireNonNull(secondOperand);
		
		if (!allowedBinaryOperations.contains(operation)) {
			throw new IllegalArgumentException("Illegal operation given binary to math node");
		}
		
		return new MathOperationTreeNode(operation, firstOperand, Optional.of(secondOperand));
	}
	
	public static MathOperationTreeNode of(ParseTreeNodeType operation, ParseTreeNode operand) {
		Objects.requireNonNull(operation);
		Objects.requireNonNull(operand);
		
		if (!allowedUnaryOperations.contains(operation)) {
			throw new IllegalArgumentException("Illegal operation given to unary math node");
		}
		
		return new MathOperationTreeNode(operation, operand, Optional.empty());
	}

	@Override
	public List<ParseTreeNode> getChildren() {
		return secondOperand.map(op2 -> Arrays.asList(firstOperand, op2)).orElse(Arrays.asList(firstOperand));
	}

	@Override
	public ParseTreeNodeType getType() {
		assert operation != null && 
				((getChildren().size() == 2 && allowedBinaryOperations.contains(operation))
						|| (getChildren().size() == 1 && allowedUnaryOperations.contains(operation)));
		
		return operation;
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
		return secondOperand.map(op2 -> firstOperand.getDataType().binaryOperationResult(operation, op2.getDataType()).get())
				.orElseGet(() -> firstOperand.getDataType().unaryOperationResult(operation).get());
	}
}
