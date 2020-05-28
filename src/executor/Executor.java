package executor;

import parser.tree.ParseTreeNode;

public interface Executor {
	public ExecutionResult execute(ParseTreeNode node);
}
