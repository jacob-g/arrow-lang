package parser.tree;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public class ReturnParseTreeNode extends AbstractParseTreeNode {
	private final ParseTreeNode returnValue;
	
	private ReturnParseTreeNode(ParseTreeNode returnValue) {
		assert returnValue != null;
		
		this.returnValue = returnValue;
	}
	
	public static ReturnParseTreeNode of(ParseTreeNode returnValue) {
		Objects.requireNonNull(returnValue);
		
		return new ReturnParseTreeNode(returnValue);
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		return Arrays.asList(returnValue);
	}

	@Override
	public ParseTreeNodeType getType() {
		// TODO Auto-generated method stub
		return ParseTreeNodeType.RETURN;
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
		assert false;
		return null;
	}

}
