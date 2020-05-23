package symboltable;

import java.util.Objects;

import parser.tree.ParseTreeNode;
import typesystem.Type;

public class SymbolTableEntry {
	private final String identifier;
	private final SymbolTableEntryType type;
	private ParseTreeNode payload = null;
	private final Type dataType;

	private SymbolTableEntry(String identifier, SymbolTableEntryType type, Type dataType) {
		assert identifier != null;
		assert type != null;
		assert dataType != null;
		
		this.identifier = identifier;
		this.type = type;
		this.dataType = dataType;
	}
	
	static SymbolTableEntry of(String identifier, SymbolTableEntryType type, Type dataType) {
		Objects.requireNonNull(identifier);
		Objects.requireNonNull(type);
		Objects.requireNonNull(dataType);
		
		return new SymbolTableEntry(identifier, type, dataType);
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
	
	public Type getDataType() {
		return dataType;
	}
	
	public void setPayload(ParseTreeNode payload) {
		Objects.requireNonNull(payload);
		
		this.payload = payload;
	}
	
	public ParseTreeNode getPayload() {
		return payload;
	}
}
