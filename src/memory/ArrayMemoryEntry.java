package memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import typesystem.Type;

public final class ArrayMemoryEntry implements MemoryEntry {
	private int size;
	private final Type dataType;
	private List<MemoryEntry> subMemoryEntries;
	private boolean initialized;
	
	private ArrayMemoryEntry(Type dataType, List<Integer> sizes, boolean initialized) {
		assert dataType != null && dataType.isArrayType();
		assert !initialized || (sizes.get(0) >= 0 && !sizes.isEmpty());
		
		this.dataType = dataType;
		this.initialized = initialized;
		if (initialized) {
			this.size = sizes.get(0);
			
			List<Integer> remainingSizes = sizes.subList(1, sizes.size());
			
			subMemoryEntries = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				if (remainingSizes.isEmpty()) {
					subMemoryEntries.add(dataType.getUnderlyingType().newEntry());
				} else {
					subMemoryEntries.add(dataType.getUnderlyingType().newEntry(remainingSizes));
				}
				
			}
		} else {
			this.size = -1;
			subMemoryEntries = null;
		}
	}
	
	public static ArrayMemoryEntry of(Type dataType, List<Integer> sizes) {
		Objects.requireNonNull(dataType);
		Objects.requireNonNull(sizes);
		
		if (!dataType.isArrayType()) {
			throw new IllegalArgumentException("Scalar type passed to array");
		}
		
		if (sizes.isEmpty()) {
			return uninitialized(dataType);
		} else {
			if (sizes.get(0) < 0) {
				throw new IllegalArgumentException("Array size must be non-negative");
			}
			
			return new ArrayMemoryEntry(dataType, sizes, true);
		}
	}
	
	public static ArrayMemoryEntry uninitialized(Type dataType) {
		Objects.requireNonNull(dataType);
		
		return new ArrayMemoryEntry(dataType, null, false);
	}
	
	@Override
	public Type getDataType() {
		return dataType;
	}

	@Override
	public void copy(MemoryEntry other) {
		this.size = other.getSize();
		
		this.subMemoryEntries = new ArrayList<>(other.getSize());
		
		for (int i = 0; i < other.getSize(); i++) {
			subMemoryEntries.add(other.getArrayValue(i).getDataType().newEntry());
			subMemoryEntries.get(i).copy(other.getArrayValue(i));
		}
	}

	@Override
	public int getScalarValue() {
		assert false : "getting scalar value of array";
		return 0;
	}

	//the array itself is always initialized
	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public boolean isArray() {
		return true;
	}
	
	private void requireInBounds(int index) {
		if (index < 0 || index >= size) {
			//TODO: have a way to handle runtime errors
			throw new IndexOutOfBoundsException("Invalid index for array: " + index);
		}
	}

	@Override
	public void copy(List<Integer> indices, MemoryEntry other) {
		Objects.requireNonNull(indices);
		Objects.requireNonNull(other);
		
		if (indices.isEmpty()) {
			copy(other);
		} else {
			int index = indices.get(0);
			
			requireInBounds(index);
				
			if (subMemoryEntries.get(index).isArray()) {
				assert indices.size() >= 1;
				
				subMemoryEntries.get(index).copy(indices.subList(1, indices.size()), other);
			} else {
				assert indices.size() == 1;
				
				subMemoryEntries.get(index).copy(other);
			}
		}
	}

	@Override
	public MemoryEntry getArrayValue(int index) {			
		requireInBounds(index);
			
		return subMemoryEntries.get(index);
	}

	@Override
	public int getSize() {
		assert initialized;
		
		return size;
	}

	public String toString() {
		return initialized ? subMemoryEntries.stream().map(entry -> entry.toString()).collect(Collectors.joining(",", "[", "]"))  : "uninitialized array";
	}
}
