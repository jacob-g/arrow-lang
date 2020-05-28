package executor;

import java.util.Objects;

import memory.RuntimeDataStack;
import parser.tree.ParseTreeAttributeType;
import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;
import typesystem.BoolType;

class LoopExecutor extends AbstractExecutor {
	private LoopExecutor(RuntimeDataStack runtimeData) {
		super(runtimeData);
	}
	
	public static LoopExecutor of(RuntimeDataStack runtimeData) {
		Objects.requireNonNull(runtimeData);
		
		return new LoopExecutor(runtimeData);
	}

	@Override
	public ExecutionResult execute(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.LOOP;
		
		ExecutionResult testResult;
		do {
			//run the body
			runtimeData.push();
			
			ExecutionResult result = CompoundExecutor.of(runtimeData).execute(node);
			if (!result.getSuccess()) {
				return result;
			}
			
			runtimeData.pop();
			
			//test whether or not to continue
			ParseTreeNode testNode = node.getAttribute(ParseTreeAttributeType.TEST);
			
			testResult = ExpressionExecutor.of(runtimeData).execute(testNode);
			if (!testResult.getSuccess()) {
				return testResult;
			}
			
			assert BoolType.getInstance().isCompatibleWith(testResult.getValue().getDataType());
		} while (testResult.getValue().getScalarValue() != 0);
		
		return ExecutionResult.voidResult();
	}

}
