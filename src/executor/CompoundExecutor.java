package executor;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import memory.RuntimeDataStack;
import parser.tree.ParseTreeAttributeType;
import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;
import typesystem.IntegerType;

public class CompoundExecutor extends AbstractExecutor {
	private CompoundExecutor(RuntimeDataStack runtimeData) {
		super(runtimeData);
	}
	
	public static CompoundExecutor of(RuntimeDataStack runtimeData) {
		Objects.requireNonNull(runtimeData);
		
		return new CompoundExecutor(runtimeData);
	}
	
	@Override
	public ExecutionResult execute(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.FUNCTION || node.getType() == ParseTreeNodeType.COMPOUND || node.getType() == ParseTreeNodeType.IF || node.getType() == ParseTreeNodeType.LOOP;

		for (ParseTreeNode child : node.getChildren()) {
			ExecutionResult result;
			
			switch (child.getType()) {
			case DECLARATION:
				result = executeDeclaration(child);
				break;
			case ASSIGNMENT:
				result = executeAssignment(child);
				break;
			case IF:
				result = executeIf(child);
				break;
			case LOOP:
				result = executeLoop(child);
				break;
			case RETURN:
				return ExpressionExecutor.of(runtimeData).execute(child.getChildren().get(0));
			case EMPTY:
				result = ExecutionResult.voidResult();
				break;
			case FUNCTION_CALL:
				result = ExpressionExecutor.of(runtimeData).execute(child);
				break;
			case PRINT:
				for (ParseTreeNode printNode : child.getChildren()) {
					ExecutionResult valueResult = ExpressionExecutor.of(runtimeData).execute(printNode);
					if (!valueResult.getSuccess()) {
						return valueResult;
					}
					
					System.out.print(valueResult.getValue());
				}
				System.out.println();
				result = ExecutionResult.voidResult();
				break;
			default:
				assert false;
				result = null;
			}
			
			if (result == null) {
				System.out.println();
			}
			
			//if this step failed, then bail out right here
			if (!result.getSuccess()) {
				return result;
			}
		}
		
		return ExecutionResult.voidResult();
	}
	
	private ExecutionResult executeIf(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.IF;
		
		return IfExecutor.of(runtimeData).execute(node);
	}
	
	private ExecutionResult executeLoop(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.LOOP;
		
		return LoopExecutor.of(runtimeData).execute(node);
	}
	
	private ExecutionResult executeDeclaration(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.DECLARATION;
		
		SymbolTableEntry identifier = node.getAttribute(ParseTreeAttributeType.IDENTIFIER).getIdentifier();
		assert identifier.getType() == SymbolTableEntryType.VARIABLE;
		
		if (identifier.getDataType().isArrayType()) {
			List<Integer> subscripts = new LinkedList<>();
			for (ParseTreeNode subscriptNode : node.getChildren()) {
				ExecutionResult subscriptResult = ExpressionExecutor.of(runtimeData).execute(subscriptNode);
				if (!subscriptResult.getSuccess()) {
					return subscriptResult;
				}
				
				assert subscriptResult.getValue().getDataType().isCompatibleWith(IntegerType.getInstance());
				
				subscripts.add(subscriptResult.getValue().getScalarValue());
			}
			
			runtimeData.add(identifier, identifier.getDataType(), subscripts);
		} else {
			runtimeData.add(identifier, node.getDataType());
		}
		
		return ExecutionResult.voidResult();
	}
	
	private ExecutionResult executeAssignment(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.ASSIGNMENT;
		assert node.getChildren().size() == 1;
		
		ParseTreeNode varNode = node.getAttribute(ParseTreeAttributeType.IDENTIFIER);
		SymbolTableEntry identifier = varNode.getIdentifier();
		assert identifier.getType() == SymbolTableEntryType.VARIABLE;
		
		ExecutionResult result = ExpressionExecutor.of(runtimeData).execute(node.getChildren().get(0));
		if (!result.getSuccess()) {
			return result;
		}
		
		if (identifier.getDataType().isArrayType()) {
			List<Integer> subscripts = new LinkedList<>();
			for (ParseTreeNode subscriptNode : varNode.getChildren()) {
				ExecutionResult subscriptResult = ExpressionExecutor.of(runtimeData).execute(subscriptNode);
				if (!subscriptResult.getSuccess()) {
					return subscriptResult;
				}
				
				subscripts.add(subscriptResult.getValue().getScalarValue());
			}
						
			runtimeData.lookup(identifier).copy(subscripts, result.getValue());
		} else {
			runtimeData.lookup(identifier).copy(result.getValue());
		}
		
		return result;
	}
}
