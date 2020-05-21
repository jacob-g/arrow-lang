package symboltable;

import java.util.Objects;

public class StaticSymbolTableStack extends SymbolTableStack<String, SymbolTableEntry> {
	public SymbolTableEntry add(String identifier, SymbolTableEntryType type) {
		Objects.requireNonNull(identifier);
		Objects.requireNonNull(type);
		
		return add(identifier, SymbolTableEntry.of(identifier, type));
	}
}
