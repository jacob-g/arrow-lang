package parser.tree;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import arrow.symboltable.SymbolTableEntry;
import memory.MemoryEntry;

public class MathOperationTreeNode extends AbstractParseTreeNode {
	public static enum Operation {
		ADD,
		SUBTRACT,
		MULTIPLY,
		DIVIDE
	}
	
	private final Operation operation;
	private final ParseTreeNode firstOperand;
	private final ParseTreeNode secondOperand;
	
	private MathOperationTreeNode(Operation operation, ParseTreeNode firstOperand, ParseTreeNode secondOperand) {
		assert firstOperand != null;
		assert secondOperand != null;
		
		this.operation = operation;
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}
	
	public static MathOperationTreeNode of(Operation operation, ParseTreeNode firstOperand, ParseTreeNode secondOperand) {
		Objects.requireNonNull(operation);
		Objects.requireNonNull(firstOperand);
		Objects.requireNonNull(secondOperand);
		
		return new MathOperationTreeNode(operation, firstOperand, secondOperand);
	}

	@Override
	public List<ParseTreeNode> getChildren() {
		return Arrays.asList(firstOperand, secondOperand);
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.MATH;
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
