package memory;

import java.util.List;

import typesystem.Type;

public interface MemoryEntry {
	Type getDataType();
	void copy(MemoryEntry other);
	void copy(List<Integer> indices, MemoryEntry other);
	int getScalarValue();
	boolean isInitialized();
	boolean isArray();
}
