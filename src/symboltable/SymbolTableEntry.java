package symboltable;

import java.util.Objects;

public class SymbolTableEntry {
	private final String identifier;
	private final SymbolTableEntryType type;

	private SymbolTableEntry(String identifier, SymbolTableEntryType type) {
		assert identifier != null;
		assert type != null;
		
		this.identifier = identifier;
		this.type = type;
	}
	
	static SymbolTableEntry of(String identifier, SymbolTableEntryType type) {
		Objects.requireNonNull(identifier);
		Objects.requireNonNull(type);
		
		return new SymbolTableEntry(identifier, type);
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public SymbolTableEntryType getType() {
		return type;
	}
	
	public String toString() {
		return getIdentifier();
	}
}
