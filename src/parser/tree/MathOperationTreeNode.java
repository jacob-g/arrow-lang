package parser.tree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import arrow.symboltable.SymbolTableEntry;
import memory.MemoryEntry;

public class MathOperationTreeNode extends AbstractParseTreeNode {
	private final ParseTreeNodeType operation;
	private final ParseTreeNode firstOperand;
	private final ParseTreeNode secondOperand;
	
	private MathOperationTreeNode(ParseTreeNodeType operation, ParseTreeNode firstOperand, ParseTreeNode secondOperand) {
		assert firstOperand != null;
		assert secondOperand != null;
		
		this.operation = operation;
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}
	
	private static final Set<ParseTreeNodeType> allowedOperations = new HashSet<>(Arrays.asList(
			ParseTreeNodeType.ADD, 
			ParseTreeNodeType.SUBTRACT, 
			ParseTreeNodeType.MULTIPLY, 
			ParseTreeNodeType.DIVIDE,
			ParseTreeNodeType.EQUAL,
			ParseTreeNodeType.NOT_EQUAL, 
			ParseTreeNodeType.GREATER_THAN, 
			ParseTreeNodeType.LESS_THAN,
			ParseTreeNodeType.AND,
			ParseTreeNodeType.OR));
	
	public static MathOperationTreeNode of(ParseTreeNodeType operation, ParseTreeNode firstOperand, ParseTreeNode secondOperand) {
		Objects.requireNonNull(operation);
		Objects.requireNonNull(firstOperand);
		Objects.requireNonNull(secondOperand);
		
		if (!allowedOperations.contains(operation)) {
			throw new IllegalArgumentException("Illegal operation given to math node");
		}
		
		return new MathOperationTreeNode(operation, firstOperand, secondOperand);
	}

	@Override
	public List<ParseTreeNode> getChildren() {
		return Arrays.asList(firstOperand, secondOperand);
	}

	@Override
	public ParseTreeNodeType getType() {
		assert operation != null && allowedOperations.contains(operation);
		
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
}
