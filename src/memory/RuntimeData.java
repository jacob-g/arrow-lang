package memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import arrow.symboltable.SymbolTableEntry;

public class RuntimeData {
	private final Map<SymbolTableEntry, MemoryEntry> entries = new HashMap<>();
	
	RuntimeData() {
	}
	
	public boolean contains(SymbolTableEntry entry) {
		Objects.requireNonNull(entry);
		
		return entries.containsKey(entry);
	}
	
	public void add(SymbolTableEntry entry) {
		if (contains(entry)) {
			throw new IllegalStateException("Adding duplicate entry onto stack frame");
		}
		
		entries.put(entry, new MemoryEntry());
	}
	
	public MemoryEntry lookup(SymbolTableEntry entry) {
		if (!contains(entry)) {
			throw new IllegalStateException("Looking up non-existent entry in stack frame");
		}
		
		return entries.get(entry);
	}
}
