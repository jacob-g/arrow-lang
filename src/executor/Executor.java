package executor;

import memory.MemoryEntry;
import parser.tree.ParseTreeNode;

public interface Executor {
	public MemoryEntry execute(ParseTreeNode node);
}
