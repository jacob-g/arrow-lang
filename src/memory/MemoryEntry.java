package memory;

import java.util.Objects;

import typesystem.Type;

public class MemoryEntry {
	private int value;
	private boolean initialized;
	private final Type dataType;
	
	private MemoryEntry(int value, Type dataType) {
		this.value = value;
		initialized = true;
		this.dataType = dataType;
	}
	
	public static MemoryEntry initialized(int value, Type dataType) {
		Objects.requireNonNull(dataType);
		
		return new MemoryEntry(value, dataType);
	}
	
	public static MemoryEntry uninitialized(Type dataType) {
		Objects.requireNonNull(dataType);
		
		return new MemoryEntry(dataType);
	}
	
	public MemoryEntry(Type dataType) {
		initialized = false;
		this.dataType = dataType;
	}
	
	public Type getDataType() {
		return dataType;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.initialized = true;
		this.value = value;
	}
}
