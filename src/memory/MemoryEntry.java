package memory;

import java.util.List;

import typesystem.Type;

public interface MemoryEntry {
	Type getDataType();
	void copy(MemoryEntry other);
	void copy(List<Integer> indices, MemoryEntry other);
	int getScalarValue();
	MemoryEntry getArrayValue(int index);
	boolean isInitialized();
	boolean isArray();
}
