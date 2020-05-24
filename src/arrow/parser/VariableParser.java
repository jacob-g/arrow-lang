package arrow.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import arrow.lexer.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;
import parser.tree.VariableParseTreeNode;
import symboltable.StaticSymbolTableStack;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;

final class VariableParser extends AbstractArrowParser {

	private VariableParser(int indentation, StaticSymbolTableStack symbolTable) {
		super(indentation, symbolTable);
	}
	
	public static VariableParser of(int indentation, StaticSymbolTableStack symbolTable) {
		requireNonNegative(indentation);
		Objects.requireNonNull(symbolTable);
		
		return new VariableParser(indentation, symbolTable);
	}

	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		if (tokens.isEmpty()) {
			return ParseResult.failure("Unexpected end-of-data", tokens);
		}
		
		if (tokens.get(0).getType() != ArrowTokenType.IDENTIFIER) {
			return ParseResult.failure("Expected variable, found " + tokens.get(0).getType(), tokens);
		}
		
		String varName = tokens.get(0).getContent();
		
		if (!symbolTable.contains(varName)) {
			return ParseResult.failure("Undefined variable", tokens);
		}
		
		SymbolTableEntry varEntry = symbolTable.lookup(varName);
		if (varEntry.getType() != SymbolTableEntryType.VARIABLE) {
			return ParseResult.failure("Expected variable, found " + varEntry.getType(), tokens);
		}
		
		return ParseResult.of(VariableParseTreeNode.of(varEntry, new ArrayList<>()), tokens.subList(1, tokens.size()));
	}

}
