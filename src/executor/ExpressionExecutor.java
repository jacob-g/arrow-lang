package executor;

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
		default:
			assert false : "Invalid node type passed to expression executor";
			return null;
		}
	}

	private MemoryEntry executeVariable(ParseTreeNode node) {
		assert node.getType() == ParseTreeNodeType.VARIABLE;
		
		return runtimeData.lookup(node.getIdentifier());
	}
}
