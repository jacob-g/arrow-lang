package memory;

import typesystem.Type;

public interface MemoryEntry {
	Type getDataType();
	void copy(MemoryEntry other);
	int getScalarValue();
	boolean isInitialized();
	boolean isArray();
}
