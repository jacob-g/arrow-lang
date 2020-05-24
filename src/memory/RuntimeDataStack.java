package memory;

import java.util.List;

import symboltable.SymbolTableEntry;
import symboltable.SymbolTableStack;
import typesystem.Type;

public class RuntimeDataStack extends SymbolTableStack<SymbolTableEntry, MemoryEntry> {
	public MemoryEntry add(SymbolTableEntry identifier, Type dataType, List<Integer> dimensions) {
		return add(identifier, dataType.newEntry(dimensions));
	}
	
	public MemoryEntry add(SymbolTableEntry identifier, Type dataType) {
		return add(identifier, dataType.newEntry());
	}
}
