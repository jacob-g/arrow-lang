package memory;

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
	
	public RuntimeDataStack() {
		top = root = new RuntimeDataStackFrame();
	}
	
	public void push(RuntimeData predecessor) {
		
	}
	
	public void push() {
		push(top.data);
	}
	
	public void pop() {
		top = top.predecessor.orElseThrow(() -> new IllegalStateException("Popping top runtime data stack frame"));
	}
	
	public void add(SymbolTableEntry entry) {
		top.data.add(entry);
	}
	
	public boolean contains(SymbolTableEntry entry) {
		RuntimeDataStackFrame frame = top;
		while (frame != root) {
			if (frame.data.contains(entry)) {
				return true;
			}
			
			frame = frame.predecessor.get();
		}
		
		return false;
	}
	
	public MemoryEntry lookup(SymbolTableEntry entry) {
		if (!contains(entry)) {
			throw new IllegalStateException("Looking up non-existent symbol table entry");
		}
		
		RuntimeDataStackFrame frame = top;
		while (frame != root) {
			if (frame.data.contains(entry)) {
				return frame.data.lookup(entry);
			}
			
			frame = frame.predecessor.get();
		}
		
		assert false;
		return null;
	}
}
