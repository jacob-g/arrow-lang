package parser.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public class DeclarationParseTreeNode extends AbstractParseTreeNode {
	private List<ParseTreeNode> dimensions;
	
	private DeclarationParseTreeNode(ParseTreeNode identifier, List<ParseTreeNode> dimensions) {
		assert identifier != null;
		assert dimensions != null;
		
		setAttribute(ParseTreeAttributeType.IDENTIFIER, identifier);
		this.dimensions = dimensions;
	}
	
	public static DeclarationParseTreeNode of(ParseTreeNode identifier, List<ParseTreeNode> dimensions) {
		Objects.requireNonNull(identifier);
		Objects.requireNonNull(dimensions);
		
		return new DeclarationParseTreeNode(identifier, dimensions);
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		return Collections.unmodifiableList(dimensions);
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.DECLARATION;
	}

	@Override
	public SymbolTableEntry getIdentifier() {
		assert false;
		return null;
	}

	@Override
	public MemoryEntry getData() {
		assert false;
		return null;
	}

	@Override
	public Type getDataType() {
		return getAttribute(ParseTreeAttributeType.IDENTIFIER).getDataType();
	}

}
