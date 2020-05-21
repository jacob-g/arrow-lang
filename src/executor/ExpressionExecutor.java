package executor;

import java.util.Arrays;
import java.util.Objects;

import memory.MemoryEntry;
import memory.RuntimeDataStack;
import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;

class ExpressionExecutor extends AbstractExecutor {
	private ExpressionExecutor(RuntimeDataStack runtimeData) {
		super(runtimeData);
	}
	
	public static ExpressionExecutor of(RuntimeDataStack runtimeData) {
		Objects.requireNonNull(runtimeData);
		
		return new ExpressionExecutor(runtimeData);
	}
	
	@Override
	public MemoryEntry execute(ParseTreeNode node) {
		switch (node.getType()) {
		case VARIABLE:
			return executeVariable(node);
		case DATA:
			return node.getData();
		case ADD:
		case SUBTRACT:
		case MULTIPLY:
		case DIVIDE:
		case EQUAL:
		case NOT_EQUAL:
		case GREATER_THAN:
		case LESS_THAN:
		case AND:
		case OR:
			return executeMathOperation(node);
		default:
			assert false : "Invalid node type passed to expression executor";
			return null;
		}
	}
	
	private int boolToInt(boolean x) {
		return x ? 1 : 0;
	}
	
	private MemoryEntry executeMathOperation(ParseTreeNode node) {
		assert node.getChildren().size() == 2;
		
		MemoryEntry firstOperand = execute(node.getChildren().get(0));
		MemoryEntry secondOperand = execute(node.getChildren().get(1));
		
		if (Arrays.asList(firstOperand, secondOperand).stream().anyMatch(operand -> !operand.isInitialized())) {
			//TODO: establish architecture for runtime errors
			throw new IllegalStateException("Working with undefined variables");
		}
		
		int op1 = firstOperand.getValue();
		int op2 = secondOperand.getValue();
		
		int out = 0;
				
		switch (node.getType()) {
		case ADD:
			out = op1 + op2; break;
		case SUBTRACT:
			out = op1 - op2; break;
		case MULTIPLY:
			out = op1 * op2; break;
		case DIVIDE:
			out = op1 / op2; break;
		case EQUAL:
			out = boolToInt(op1 == op2); break;
		case NOT_EQUAL:
			out = boolToInt(op1 != op2); break;
		case GREATER_THAN:
			out = boolToInt(op1 > op2); break;
		case LESS_THAN:
			out = boolToInt(op1 < op2); break;
		case AND:
			out = boolToInt(op1 != 0 && op2 != 0); break;
		case OR:
			out = boolToInt(op1 != 0 || op2 != 0); break;
		default:
			assert false;
		}
		
		return new MemoryEntry(out);
	}

	private MemoryEntry executeVariable(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.VARIABLE;
		
		//TODO: if there's an undefined variable cause problems
		
		return runtimeData.lookup(node.getIdentifier());
	}
}
