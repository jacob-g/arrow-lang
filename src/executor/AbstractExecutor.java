package executor;

import memory.RuntimeDataStack;

public abstract class AbstractExecutor implements Executor {
	protected final RuntimeDataStack runtimeData;
	
	protected AbstractExecutor(RuntimeDataStack runtimeData) {
		this.runtimeData = runtimeData;
	}
}
