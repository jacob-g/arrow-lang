package memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import typesystem.Type;

public final class ArrayMemoryEntry implements MemoryEntry {
	private final int size;
	private final Type dataType;
	private final List<MemoryEntry> subMemoryEntries;
	
	private ArrayMemoryEntry(Type dataType, int size) {
		assert dataType != null && dataType.isArrayType();
		assert size >= 0;
		
		this.dataType = dataType;
		this.size = size;
		
		subMemoryEntries = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			subMemoryEntries.add(dataType.newEntry());
		}
	}
	
	public static ArrayMemoryEntry of(Type dataType, int size) {
		Objects.requireNonNull(dataType);
		
		if (!dataType.isArrayType()) {
			throw new IllegalArgumentException("Scalar type passed to array");
		}
		
		if (size < 0) {
			throw new IllegalArgumentException("Array size must be non-negative");
		}
		
		return new ArrayMemoryEntry(dataType, size);
	}
	
	@Override
	public Type getDataType() {
		return dataType;
	}

	@Override
	public void copy(MemoryEntry other) {
		assert other.isArray();
	}

	@Override
	public int getScalarValue() {
		assert false;
		return 0;
	}

	//the array itself is always initialized
	@Override
	public boolean isInitialized() {
		return true;
	}

	@Override
	public boolean isArray() {
		return true;
	}

}
