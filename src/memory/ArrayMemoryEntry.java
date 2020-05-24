package memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import typesystem.Type;

public final class ArrayMemoryEntry implements MemoryEntry {
	private final int size;
	private final Type dataType;
	private final List<MemoryEntry> subMemoryEntries;
	
	private ArrayMemoryEntry(Type dataType, List<Integer> sizes) {
		assert dataType != null && dataType.isArrayType();
		assert !sizes.isEmpty();
		assert sizes.get(0) >= 0;
		
		this.dataType = dataType;
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
	}
	
	public static ArrayMemoryEntry of(Type dataType, List<Integer> sizes) {
		Objects.requireNonNull(dataType);
		Objects.requireNonNull(sizes);
		
		if (!dataType.isArrayType()) {
			throw new IllegalArgumentException("Scalar type passed to array");
		}
		
		assert !sizes.isEmpty();
		
		if (sizes.get(0) < 0) {
			throw new IllegalArgumentException("Array size must be non-negative");
		}
		
		return new ArrayMemoryEntry(dataType, sizes);
	}
	
	@Override
	public Type getDataType() {
		return dataType;
	}

	@Override
	public void copy(MemoryEntry other) {
		assert false;
	}

	@Override
	public int getScalarValue() {
		assert false : "getting scalar value of array";
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
	
	private void requireInBounds(int index) {
		if (index < 0 || index >= size) {
			//TODO: have a way to handle runtime errors
			throw new IndexOutOfBoundsException("Invalid index for array: " + index);
		}
	}

	@Override
	public void copy(List<Integer> indices, MemoryEntry other) {
		assert !indices.isEmpty() : "number of subscripts should match array dimension";
		
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

	@Override
	public MemoryEntry getArrayValue(int index) {			
		requireInBounds(index);
			
		return subMemoryEntries.get(index);
	}

}
