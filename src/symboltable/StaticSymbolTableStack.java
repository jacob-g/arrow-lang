package symboltable;

import java.util.Objects;

import parser.tree.ParseTreeNode;
import parser.tree.ParseTreeNodeType;

public class StaticSymbolTableStack extends SymbolTableStack<String, SymbolTableEntry> {
	public SymbolTableEntry add(String identifier, SymbolTableEntryType type) {
		Objects.requireNonNull(identifier);
		Objects.requireNonNull(type);
		
		return add(identifier, SymbolTableEntry.of(identifier, type));
	}
}
