package memory;

import java.util.List;
import java.util.Objects;

import typesystem.Type;

public final class ScalarMemoryEntry implements MemoryEntry {
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
		assert getDataType().canBeAssignedTo(other.getDataType());
		
		this.initialized = true;
		this.value = other.getScalarValue();
	}

	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public void copy(List<Integer> indices, MemoryEntry other) {
		assert false : "array-copying a scalar";
	}

	@Override
	public MemoryEntry getArrayValue(int index) {
		assert false : "getting array index of a scalar";
		return null;
	}

	@Override
	public int getSize() {
		assert false : "getting size of a scalar";
		return 0;
	}
	
	public String toString() {
		return initialized ? Integer.toString(value) : "uninitialized scalar";
	}
}
