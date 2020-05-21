package symboltable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SymbolTable<K, V> {
	private final Map<K, V> entries = new HashMap<>();
	
	public boolean contains(K name) {
		Objects.requireNonNull(name);
		
		return entries.containsKey(name);
	}
	
	public V add(K name, V entry) {
		if (contains(name)) {
			throw new IllegalStateException("Adding already-existing symbol table entry");
		}
		
		entries.put(name, entry);
		
		return entry;
	}
	
	public V getEntry(K name) {
		if (!contains(name)) {
			throw new IllegalArgumentException("Looking for non-existent symbol table entry");
		}
		
		return entries.get(name);
	}
}
