package arrow.symboltable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SymbolTableStack {
	private final ChainedStackEntry root;
	private ChainedStackEntry top;
	private final Map<SymbolTable, ChainedStackEntry> stackEntries = new HashMap<>();
	
	private class ChainedStackEntry {
		private final SymbolTable table;
		private final Optional<ChainedStackEntry> predecessor; //the immediate entry above in the stack
		private final Optional<ChainedStackEntry> parent; //the parent where scoping looks
		
		private ChainedStackEntry(SymbolTable table) {
			this(table, Optional.empty(), Optional.empty());
			
			assert table != null;
		}
		
		private ChainedStackEntry(SymbolTable table, ChainedStackEntry predecessor, ChainedStackEntry parent) {
			this(table, Optional.of(predecessor), Optional.of(parent));
			
			assert table != null;
			assert predecessor != null;
			assert parent != null;
		}
		
		private ChainedStackEntry(SymbolTable table, Optional<ChainedStackEntry> predecessor, Optional<ChainedStackEntry> parent) {
			assert table != null;
			assert predecessor != null;
			assert parent != null;
			
			this.table = table;
			this.predecessor = predecessor;
			this.parent = parent;
		}
	}
	
	private SymbolTableStack() {
		this.root = new ChainedStackEntry(new SymbolTable());
		this.top = root;
		
		stackEntries.put(root.table, root);
	}
	
	public static SymbolTableStack build() {
		return new SymbolTableStack();
	}
	
	public SymbolTable getRoot() {
		return root.table;
	}
	
	public SymbolTableEntry add(String name, SymbolTableEntryType type) {
		return top.table.add(name, type);
	}
	
	public SymbolTable push(SymbolTable predecessor) {
		Objects.requireNonNull(predecessor);
		
		if (!stackEntries.containsKey(predecessor)) {
			throw new IllegalStateException("Predecessor not contained in this stack");
		}
		
		SymbolTable newTable = new SymbolTable();
		top = new ChainedStackEntry(newTable, top, stackEntries.get(predecessor));
		stackEntries.put(newTable, top);
		
		return newTable;
	}
	
	public SymbolTable push() {
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
	
	public boolean contains(String name) {
		ChainedStackEntry currentFrame = top;
		while (currentFrame != null) {
			if (currentFrame.table.contains(name)) {
				return true;
			}
			
			currentFrame = currentFrame.parent.orElse(null);
		}
		
		return false;
	}
	
	public SymbolTableEntry lookup(String name) {
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
