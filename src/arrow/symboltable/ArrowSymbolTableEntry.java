package arrow.symboltable;

public class ArrowSymbolTableEntry {
	private final String identifier;
	private final SymbolTableEntryType type;

	ArrowSymbolTableEntry(String identifier, SymbolTableEntryType type) {
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
