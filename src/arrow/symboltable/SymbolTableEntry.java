package arrow.symboltable;

public class SymbolTableEntry {
	private final String identifier;
	private final SymbolTableEntryType type;

	SymbolTableEntry(String identifier, SymbolTableEntryType type) {
		assert identifier != null;
		assert type != null;
		
		this.identifier = identifier;
		this.type = type;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public SymbolTableEntryType getType() {
		return type;
	}
}
