package executor;

import java.util.Objects;

import memory.RuntimeDataStack;
import parser.tree.ParseTreeAttributeType;
import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;
import typesystem.BoolType;

class IfExecutor extends AbstractExecutor {
	private IfExecutor(RuntimeDataStack runtimeData) {
		super(runtimeData);
	}
	
	public static IfExecutor of(RuntimeDataStack runtimeData) {
		Objects.requireNonNull(runtimeData);
		
		return new IfExecutor(runtimeData);
	}

	@Override
	public ExecutionResult execute(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.IF;
		
		ParseTreeNode testNode = node.getAttribute(ParseTreeAttributeType.TEST);
		ExecutionResult testResult = ExpressionExecutor.of(runtimeData).execute(testNode);
		if (!testResult.getSuccess()) {
			return testResult;
		}
		
		assert BoolType.getInstance().isCompatibleWith(testResult.getValue().getDataType());
		
		if (testResult.getValue().getScalarValue() == 0) {
			//if the condition is true, then run the body
			runtimeData.push();
			ExecutionResult bodyResult = CompoundExecutor.of(runtimeData).execute(node);
			if (!bodyResult.getSuccess()) {
				return bodyResult;
			}
			runtimeData.pop();
		}
		
		return ExecutionResult.voidResult();
	}

}
