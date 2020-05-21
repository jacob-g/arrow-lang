package symboltable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class SymbolTableStack<K, V> {
	private final ChainedStackEntry root;
	private ChainedStackEntry top;
	private final Map<SymbolTable<K, V>, ChainedStackEntry> stackEntries = new HashMap<>();
	
	private class ChainedStackEntry {
		private final SymbolTable<K, V> table;
		private final Optional<ChainedStackEntry> predecessor; //the immediate entry above in the stack
		private final Optional<ChainedStackEntry> parent; //the parent where scoping looks
		
		private ChainedStackEntry(SymbolTable<K, V> table) {
			this(table, Optional.empty(), Optional.empty());
			
			assert table != null;
		}
		
		private ChainedStackEntry(SymbolTable<K, V> table, ChainedStackEntry predecessor, ChainedStackEntry parent) {
			this(table, Optional.of(predecessor), Optional.of(parent));
			
			assert table != null;
			assert predecessor != null;
			assert parent != null;
		}
		
		private ChainedStackEntry(SymbolTable<K, V> table, Optional<ChainedStackEntry> predecessor, Optional<ChainedStackEntry> parent) {
			assert table != null;
			assert predecessor != null;
			assert parent != null;
			
			this.table = table;
			this.predecessor = predecessor;
			this.parent = parent;
		}
	}
	
	protected SymbolTableStack() {
		this.root = new ChainedStackEntry(new SymbolTable<K, V>());
		this.top = root;
		
		stackEntries.put(root.table, root);
	}
		
	protected V add(K name, V entry) {
		return top.table.add(name, entry);
	}
	
	public SymbolTable<K, V> getRoot() {
		return root.table;
	}
	
	public SymbolTable<K, V> push(SymbolTable<K, V> predecessor) {
		Objects.requireNonNull(predecessor);
		
		if (!stackEntries.containsKey(predecessor)) {
			throw new IllegalStateException("Predecessor not contained in this stack");
		}
		
		SymbolTable<K, V> newTable = new SymbolTable<K, V>();
		top = new ChainedStackEntry(newTable, top, stackEntries.get(predecessor));
		stackEntries.put(newTable, top);
		
		return newTable;
	}
	
	public SymbolTable<K, V> push() {
		return push(top.table);
	}
	
	public void pop() {
		ChainedStackEntry removedEntry = top;
		if (!removedEntry.predecessor.isPresent()) {
			throw new IllegalStateException("Popping from empty stack");
		}
		
		top = removedEntry.predecessor.get();
		stackEntries.remove(removedEntry.table);
	}
	
	public boolean contains(K name) {
		ChainedStackEntry currentFrame = top;
		while (currentFrame != null) {
			if (currentFrame.table.contains(name)) {
				return true;
			}
			
			currentFrame = currentFrame.parent.orElse(null);
		}
		
		return false;
	}
	
	public V lookup(K name) {
		if (!contains(name)) {
			throw new IllegalStateException("Looking up non-existent entry");
		}
		
		ChainedStackEntry currentFrame = top;
		while (currentFrame != null) {
			if (currentFrame.table.contains(name)) {
				return currentFrame.table.getEntry(name);
			}
			
			currentFrame = currentFrame.parent.orElse(null);
		}
		
		assert false;
		return null;
	}
}
