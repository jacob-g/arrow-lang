package executor;

import java.util.Arrays;
import java.util.Objects;

import memory.MemoryEntry;
import memory.RuntimeDataStack;
import memory.ScalarMemoryEntry;
import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;
import typesystem.IntegerType;

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
		case MODULO:
		case NOT_EQUAL:
		case GREATER_THAN:
		case LESS_THAN:
		case AND:
		case OR:
			return executeBinaryMathOperation(node);
		case NOT:
		case NEGATE:
			return executeUnaryMathOperation(node);
		case FUNCTION_CALL:
			return FunctionCallExecutor.of(runtimeData).execute(node);
		case ARRAY_LENGTH:
			return ScalarMemoryEntry.initialized(execute(node.getChildren().get(0)).getSize(), IntegerType.getInstance());
		default:
			assert false : "Invalid node type passed to expression executor";
			return null;
		}
	}
	
	private int boolToInt(boolean x) {
		return x ? 1 : 0;
	}
	
	private MemoryEntry executeBinaryMathOperation(ParseTreeNode node) {
		assert node.getChildren().size() == 2;
		
		MemoryEntry firstOperand = execute(node.getChildren().get(0));
		MemoryEntry secondOperand = execute(node.getChildren().get(1));
		
		if (Arrays.asList(firstOperand, secondOperand).stream().anyMatch(operand -> !operand.isInitialized())) {
			//TODO: establish architecture for runtime errors
			throw new IllegalStateException("Working with undefined variables");
		}
		
		assert !firstOperand.isArray() && !secondOperand.isArray() : "attempting to perform math operations on array";
		
		int op1 = firstOperand.getScalarValue();
		int op2 = secondOperand.getScalarValue();
		
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
		case MODULO:
			out = op1 % op2; break;
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
		
		return ScalarMemoryEntry.initialized(out, node.getDataType());
	}
	
	private MemoryEntry executeUnaryMathOperation(ParseTreeNode node) {
		assert node.getChildren().size() == 1;
		
		MemoryEntry operand = execute(node.getChildren().get(0));
		
		if (!operand.isInitialized()) {
			//TODO: establish architecture for runtime errors
			throw new IllegalStateException("Working with undefined variables");
		}
		
		int op1 = operand.getScalarValue();
		
		int out = 0;
				
		switch (node.getType()) {
		case NOT:
			out = op1 == 0 ? 1 : 0; break;
		case NEGATE:
			out = -op1; break;
		default:
			assert false;
		}
		
		return ScalarMemoryEntry.initialized(out, node.getDataType());
	}

	private MemoryEntry executeVariable(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.VARIABLE;
				
		MemoryEntry varEntry = runtimeData.lookup(node.getIdentifier());
		
		if (varEntry.isArray()) {
			for (ParseTreeNode subscriptNode : node.getChildren()) {
				MemoryEntry subscriptEntry = execute(subscriptNode);
				
				assert subscriptEntry.getDataType().isCompatibleWith(IntegerType.getInstance());
				
				varEntry = varEntry.getArrayValue(subscriptEntry.getScalarValue());
			}
		}
		
		if (!varEntry.isInitialized()) {
			throw new IllegalStateException("Accessing uninitialized variable " + node.getIdentifier());
		}
		
		return varEntry;
	}
}
