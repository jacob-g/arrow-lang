package symboltable;

import java.util.Objects;

import typesystem.Type;

public class StaticSymbolTableStack extends SymbolTableStack<String, SymbolTableEntry> {
	public SymbolTableEntry add(String identifier, SymbolTableEntryType type, Type dataType) {
		Objects.requireNonNull(identifier);
		Objects.requireNonNull(type);
		Objects.requireNonNull(dataType);
		
		return add(identifier, SymbolTableEntry.of(identifier, type, dataType));
	}
}
