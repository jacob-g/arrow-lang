package memory;

import symboltable.SymbolTableEntry;
import symboltable.SymbolTableStack;
import typesystem.Type;

public class RuntimeDataStack extends SymbolTableStack<SymbolTableEntry, MemoryEntry> {
	public MemoryEntry add(SymbolTableEntry identifier, Type dataType) {
		return add(identifier, new MemoryEntry(dataType));
	}
}
