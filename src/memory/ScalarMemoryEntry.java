package memory;

import java.util.Objects;

import typesystem.Type;

public class ScalarMemoryEntry implements MemoryEntry {
	private int value;
	private boolean initialized;
	private final Type dataType;
	
	private ScalarMemoryEntry(int value, Type dataType) {
		this.value = value;
		initialized = true;
		this.dataType = dataType;
	}
	
	private static void requireScalarType(Type dataType) {
		if (dataType.isArrayType()) {
			throw new IllegalArgumentException("Can't create scalar memory entry of array type");
		}
	}
	
	public static MemoryEntry initialized(int value, Type dataType) {
		Objects.requireNonNull(dataType);
		requireScalarType(dataType);
		
		return new ScalarMemoryEntry(value, dataType);
	}
	
	public static MemoryEntry uninitialized(Type dataType) {
		Objects.requireNonNull(dataType);
		requireScalarType(dataType);
		
		return new ScalarMemoryEntry(dataType);
	}
	
	private ScalarMemoryEntry(Type dataType) {
		initialized = false;
		this.dataType = dataType;
	}
	
	public Type getDataType() {
		return dataType;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public int getScalarValue() {
		return value;
	}

	@Override
	public void copy(MemoryEntry other) {
		this.initialized = true;
		this.value = other.getScalarValue();
	}

	@Override
	public boolean isArray() {
		return false;
	}
}
