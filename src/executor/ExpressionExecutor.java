package executor;

import java.util.Objects;
import java.util.Scanner;

import memory.MemoryEntry;
import memory.RuntimeDataStack;
import memory.ScalarMemoryEntry;
import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;
import typesystem.ArrayType;
import typesystem.CharType;
import typesystem.IntegerType;
import typesystem.Type;

class ExpressionExecutor extends AbstractExecutor {
	private ExpressionExecutor(RuntimeDataStack runtimeData) {
		super(runtimeData);
	}
	
	public static ExpressionExecutor of(RuntimeDataStack runtimeData) {
		Objects.requireNonNull(runtimeData);
		
		return new ExpressionExecutor(runtimeData);
	}
	
	@Override
	public ExecutionResult execute(ParseTreeNode node) {
		switch (node.getType()) {
		case VARIABLE:
			return executeVariable(node);
		case DATA:
			return ExecutionResult.success(node.getData());
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
			return executeArrayLength(node);
		case INPUT:
			return executeInput(node.getDataType());
		default:
			assert false : "Invalid node type passed to expression executor";
			return null;
		}
	}
	
	private ExecutionResult executeArrayLength(ParseTreeNode node) {
		ExecutionResult arrayResult = execute(node.getChildren().get(0));
		if (!arrayResult.getSuccess()) {
			return arrayResult;
		}
		
		return ExecutionResult.success(ScalarMemoryEntry.initialized(arrayResult.getValue().getSize(), IntegerType.getInstance()));
	}

	private ExecutionResult executeInput(Type dataType) {
		Scanner scan = new Scanner(System.in);
		
		MemoryEntry result = null;
		if (dataType.isCompatibleWith(IntegerType.getInstance())) {
			result = ScalarMemoryEntry.initialized(scan.nextInt(), IntegerType.getInstance());
		}
		
		if (dataType.isCompatibleWith(ArrayType.of(CharType.getInstance()))) {
			result = CharType.getInstance().fromString(scan.nextLine());
		}
		
		scan.close();
		
		assert result != null : "unhandled data type for input";
		
		return ExecutionResult.success(result);
	}

	private int boolToInt(boolean x) {
		return x ? 1 : 0;
	}
	
	private ExecutionResult executeBinaryMathOperation(ParseTreeNode node) {
		assert node.getChildren().size() == 2;
		
		ExecutionResult firstOperand = execute(node.getChildren().get(0));
		if (!firstOperand.getSuccess()) {
			return firstOperand;
		}
		
		ExecutionResult secondOperand = execute(node.getChildren().get(1));
		if (!secondOperand.getSuccess()) {
			return secondOperand;
		}
		
		assert !firstOperand.getValue().isArray() && !secondOperand.getValue().isArray() : "attempting to perform math operations on array";
		
		int op1 = firstOperand.getValue().getScalarValue();
		int op2 = secondOperand.getValue().getScalarValue();
		
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
		
		return ExecutionResult.success(ScalarMemoryEntry.initialized(out, node.getDataType()));
	}
	
	private ExecutionResult executeUnaryMathOperation(ParseTreeNode node) {
		assert node.getChildren().size() == 1;
		
		ExecutionResult operand = execute(node.getChildren().get(0));
		if (!operand.getSuccess()) {
			return operand;
		}
		
		int op1 = operand.getValue().getScalarValue();
		
		int out = 0;
				
		switch (node.getType()) {
		case NOT:
			out = op1 == 0 ? 1 : 0; break;
		case NEGATE:
			out = -op1; break;
		default:
			assert false;
		}
		
		return ExecutionResult.success(ScalarMemoryEntry.initialized(out, node.getDataType()));
	}

	private ExecutionResult executeVariable(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.VARIABLE;
				
		MemoryEntry varEntry = runtimeData.lookup(node.getIdentifier());
		
		if (varEntry.isArray()) {
			for (ParseTreeNode subscriptNode : node.getChildren()) {
				ExecutionResult subscriptEntry = execute(subscriptNode);
				if (!subscriptEntry.getSuccess()) {
					return subscriptEntry;
				}
				
				assert subscriptEntry.getValue().getDataType().isCompatibleWith(IntegerType.getInstance());
				
				int index = subscriptEntry.getValue().getScalarValue();
				
				if (!varEntry.isInBounds(index)) {
					return ExecutionResult.failure("Array index out of bounds: " + index + " given for array of size " + varEntry.getSize());
				}
				
				varEntry = varEntry.getArrayValue(index);
			}
		}
		
		if (!varEntry.isInitialized()) {
			return ExecutionResult.failure("Accessing unitialized variable: " + node.getIdentifier());
		}
		
		return ExecutionResult.success(varEntry);
	}
}
