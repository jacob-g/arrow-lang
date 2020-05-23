package executor;

import java.util.Objects;

import memory.MemoryEntry;
import memory.RuntimeDataStack;
import parser.tree.ParseTreeAttributeType;
import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;

class LoopExecutor extends AbstractExecutor {
	private LoopExecutor(RuntimeDataStack runtimeData) {
		super(runtimeData);
	}
	
	public static LoopExecutor of(RuntimeDataStack runtimeData) {
		Objects.requireNonNull(runtimeData);
		
		return new LoopExecutor(runtimeData);
	}

	@Override
	public MemoryEntry execute(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.LOOP;
		
		MemoryEntry testValue;
		do {
			runtimeData.push();
			CompoundExecutor.of(runtimeData).execute(node);
			runtimeData.pop();
			ParseTreeNode testNode = node.getAttribute(ParseTreeAttributeType.TEST);
			testValue = ExpressionExecutor.of(runtimeData).execute(testNode);
		} while (testValue.getValue() != 0); //TODO: have a better way of handling loops
		
		return null;
	}

}
