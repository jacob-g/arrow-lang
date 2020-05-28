package executor;

import java.util.Objects;
import java.util.Optional;

import memory.MemoryEntry;

public final class ExecutionResult {
	private Optional<MemoryEntry> value;
	private boolean isSuccessful;
	private Optional<String> errorMessage;
	
	private ExecutionResult(boolean isSuccessful, Optional<MemoryEntry> value, Optional<String> errorMessage) {
		assert value != null;
		assert errorMessage != null;
		assert isSuccessful == !errorMessage.isPresent();
		
		this.isSuccessful = isSuccessful;
		this.value = value;
		this.errorMessage = errorMessage;
	}
	
	public static ExecutionResult voidResult() {
		return new ExecutionResult(true, Optional.empty(), Optional.empty());
	}
	
	public static ExecutionResult success(MemoryEntry value) {
		Objects.requireNonNull(value);
		
		return new ExecutionResult(true, Optional.of(value), Optional.empty());
	}
	
	public static ExecutionResult failure(String errorMessage) {
		Objects.requireNonNull(errorMessage);
		
		return new ExecutionResult(false, Optional.empty(), Optional.of(errorMessage));
	}
	
	public MemoryEntry getValue() {
		return value.orElseThrow(() -> new IllegalStateException("Retrieving a non-existent value from an execution result"));
	}
	
	public boolean getSuccess() {
		return isSuccessful;
	}
	
	private void requireSuccess(boolean desiredState) {
		if (getSuccess() != desiredState) {
			throw new IllegalArgumentException("Invalid success state");
		}
	}
	
	public String getErrorMessage() {
		requireSuccess(false);
		
		return errorMessage.get();
	}
	
	public boolean hasValue() {
		requireSuccess(true);
		
		return value.isPresent();
	}
	
	@Override
	public String toString() {
		if (getSuccess()) {
			return hasValue() ? getValue().toString() : "[success]";
		} else {
			return "Execution failure:\n" + getErrorMessage();
		}
	}
}
