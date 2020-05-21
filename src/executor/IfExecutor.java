package executor;

import java.util.Objects;

import memory.MemoryEntry;
import memory.RuntimeDataStack;
import parser.tree.ParseTreeAttributeType;
import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;

class IfExecutor extends AbstractExecutor {
	private IfExecutor(RuntimeDataStack runtimeData) {
		super(runtimeData);
	}
	
	public static IfExecutor of(RuntimeDataStack runtimeData) {
		Objects.requireNonNull(runtimeData);
		
		return new IfExecutor(runtimeData);
	}

	@Override
	public MemoryEntry execute(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.IF;
		
		ParseTreeNode testNode = node.getAttribute(ParseTreeAttributeType.TEST);
		MemoryEntry testValue = ExpressionExecutor.of(runtimeData).execute(testNode);
		
		if (testValue.getValue() == 0) {
			//if the condition is true, then run the body
			runtimeData.push();
			CompoundExecutor.of(runtimeData).execute(node);
			runtimeData.pop();
		}
		
		return null;
	}

}
