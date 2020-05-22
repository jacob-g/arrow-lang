package symboltable;

import java.util.Objects;

import parser.tree.ParseTreeNode;

public class SymbolTableEntry {
	private final String identifier;
	private final SymbolTableEntryType type;
	private ParseTreeNode payload = null;

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
	
	public void setPayload(ParseTreeNode payload) {
		Objects.requireNonNull(payload);
		
		this.payload = payload;
	}
	
	public ParseTreeNode getPayload() {
		return payload;
	}
}
