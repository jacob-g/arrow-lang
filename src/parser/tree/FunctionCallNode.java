package parser.tree;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import arrow.lexer.ArrowTokenType;
import memory.MemoryEntry;
import parser.ParseResult;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;

public class FunctionCallNode extends AbstractParseTreeNode {
	private final SymbolTableEntry function;
	private final List<ParseTreeNode> arguments;
	
	private FunctionCallNode(SymbolTableEntry function, List<ParseTreeNode> arguments) {
		assert function != null && function.getType() == SymbolTableEntryType.FUNCTION;
		assert arguments != null;
		
		this.function = function;
		this.arguments = arguments;
	}
	
	public static FunctionCallNode of(SymbolTableEntry function, List<ParseTreeNode> arguments) {
		Objects.requireNonNull(function);
		Objects.requireNonNull(arguments);
		
		if (function.getType() != SymbolTableEntryType.FUNCTION) {
			throw new IllegalArgumentException("Function call must be to function");
		}
		
		return new FunctionCallNode(function, arguments);
	}
	
	@Override
	public List<ParseTreeNode> getChildren() {
		return Collections.unmodifiableList(arguments);
	}

	@Override
	public ParseTreeNodeType getType() {
		// TODO Auto-generated method stub
		return ParseTreeNodeType.FUNCTION_CALL;
	}

	@Override
	public SymbolTableEntry getIdentifier() {
		return function;
	}

	@Override
	public MemoryEntry getData() {
		assert false;
		return null;
	}
}
