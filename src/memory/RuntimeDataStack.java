package memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import arrow.symboltable.SymbolTableEntry;

public class RuntimeDataStack {
	private class RuntimeDataStackFrame {
		private final RuntimeData data = new RuntimeData();
		private final Optional<RuntimeDataStackFrame> predecessor;
		private final Optional<RuntimeDataStackFrame> parent;
		
		private RuntimeDataStackFrame(Optional<RuntimeDataStackFrame> predecessor, Optional<RuntimeDataStackFrame> parent) {
			assert predecessor != null;
			assert parent != null;
			
			this.predecessor = predecessor;
			this.parent = parent;
		}
		
		private RuntimeDataStackFrame() {
			this(Optional.empty(), Optional.empty());
		}
	}
	
	private RuntimeDataStackFrame top;
	private RuntimeDataStackFrame root;
	
	private final Map<RuntimeData, RuntimeDataStackFrame> entries = new HashMap<>();
	
	public RuntimeDataStack() {
		top = root = new RuntimeDataStackFrame();
		entries.put(root.data, root);
	}
	
	public void push(RuntimeData parent) {
		Objects.requireNonNull(parent);
		
		if (!entries.containsKey(parent)) {
			throw new IllegalStateException("Adding invalid parent to runtime stack");
		}
		
		top = new RuntimeDataStackFrame(Optional.of(top), Optional.of(entries.get(parent)));
		entries.put(top.data, top);
	}
	
	public void push() {
		push(top.data);
	}
	
	public void pop() {
		entries.remove(top.data);
		top = top.predecessor.orElseThrow(() -> new IllegalStateException("Popping top runtime data stack frame"));
	}
	
	public void add(SymbolTableEntry entry) {
		top.data.add(entry);
	}
	
	public boolean contains(SymbolTableEntry entry) {
		RuntimeDataStackFrame frame = top;
		while (frame != null) {
			if (frame.data.contains(entry)) {
				return true;
			}
			
			frame = frame.parent.orElse(null);
		}
		
		return false;
	}
	
	public MemoryEntry lookup(SymbolTableEntry entry) {
		if (!contains(entry)) {
			throw new IllegalStateException("Looking up non-existent symbol table entry");
		}
		
		RuntimeDataStackFrame frame = top;
		while (frame != null) {
			if (frame.data.contains(entry)) {
				return frame.data.lookup(entry);
			}
			
			frame = frame.parent.orElse(null);
		}
		
		assert false;
		return null;
	}
}
