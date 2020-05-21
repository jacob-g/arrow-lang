package executor;

import memory.RuntimeDataStack;

abstract class AbstractExecutor implements Executor {
	protected final RuntimeDataStack runtimeData;
	
	protected AbstractExecutor(RuntimeDataStack runtimeData) {
		this.runtimeData = runtimeData;
	}
}
