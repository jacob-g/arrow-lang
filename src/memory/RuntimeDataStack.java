package memory;

import symboltable.SymbolTableEntry;
import symboltable.SymbolTableStack;

public class RuntimeDataStack extends SymbolTableStack<SymbolTableEntry, MemoryEntry> {
	public MemoryEntry add(SymbolTableEntry identifier) {
		return add(identifier, new MemoryEntry());
	}
}
