package arrow.symboltable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SymbolTable {
	private final Map<String, SymbolTableEntry> entries = new HashMap<>();
	
	public boolean contains(String name) {
		Objects.requireNonNull(name);
		
		return entries.containsKey(name);
	}
	
	public SymbolTableEntry add(String name, SymbolTableEntryType type) {
		if (contains(name)) {
			throw new IllegalStateException("Adding already-existing symbol table entry");
		}
		
		SymbolTableEntry entry = new SymbolTableEntry(name, type);
		entries.put(name, entry);
		
		return entry;
	}
	
	public SymbolTableEntry getEntry(String name) {
		if (!contains(name)) {
			throw new IllegalArgumentException("Looking for non-existent symbol table entry");
		}
		
		return entries.get(name);
	}
}
