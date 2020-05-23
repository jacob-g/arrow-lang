package parser.tree;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import memory.MemoryEntry;
import symboltable.SymbolTableEntry;
import typesystem.Type;

public final class FunctionParseTreeNode extends AbstractParseTreeNode {
	private final List<ParseTreeNode> body;
	private final Type returnType;
	
	private FunctionParseTreeNode(List<ParseTreeNode> body, ParseTreeNode arguments, Type returnType) {
		assert body != null;
		assert arguments != null && arguments.getType() == ParseTreeNodeType.ARGUMENTS;
		assert returnType != null;
		
		this.body = body;
		setAttribute(ParseTreeAttributeType.ARGUMENTS, arguments);
		this.returnType = returnType;
	}
	
	public static FunctionParseTreeNode of(List<ParseTreeNode> body, ParseTreeNode arguments, Type returnType) {
		Objects.requireNonNull(body);

		if (arguments.getType() != ParseTreeNodeType.ARGUMENTS) {
			throw new IllegalArgumentException("Function arguments must be of arguments type");
		}
		
		return new FunctionParseTreeNode(body, arguments, returnType);
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		return Collections.unmodifiableList(body);
	}

	@Override
	public ParseTreeNodeType getType() {
		return ParseTreeNodeType.FUNCTION;
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
		return returnType;
	}

}
