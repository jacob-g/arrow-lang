package memory;

public class MemoryEntry {
	//TODO: make this support types
	private int value;
	private boolean initialized;
	
	public MemoryEntry(int value) {
		this.value = value;
		initialized = true;
	}
	
	public MemoryEntry() {
		initialized = false;
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
