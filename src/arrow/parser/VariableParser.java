package arrow.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import arrow.lexer.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;
import parser.tree.ParseTreeNode;
import parser.tree.VariableParseTreeNode;
import symboltable.StaticSymbolTableStack;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;
import typesystem.IntegerType;
import typesystem.Type;

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
		
		List<Token<ArrowTokenType>> remainder = tokens;
		
		if (remainder.get(0).getType() != ArrowTokenType.IDENTIFIER) {
			return ParseResult.failure("Expected variable, found " + remainder.get(0).getType(), remainder);
		}
		
		String varName = remainder.get(0).getContent();
		
		if (!symbolTable.contains(varName)) {
			return ParseResult.failure("Undefined variable", remainder);
		}
		
		SymbolTableEntry varEntry = symbolTable.lookup(varName);
		if (varEntry.getType() != SymbolTableEntryType.VARIABLE) {
			return ParseResult.failure("Expected variable, found " + varEntry.getType(), remainder);
		}
		
		Type type = varEntry.getDataType();
		
		remainder = remainder.subList(1, remainder.size());
		boolean moreSubscripts = true;
		List<ParseTreeNode> subscripts = new LinkedList<>();
		while (moreSubscripts) {
			if (remainder.isEmpty()) {
				return ParseResult.failure("Unexpected end-of-data", remainder);
			}
			
			switch (remainder.get(0).getType()) {
			case OPEN_SUBSCRIPT:
				if (!type.isArrayType()) {
					return ParseResult.failure("Too many subscripts", remainder);
				}
				
				remainder = remainder.subList(1, remainder.size()); //consume the open bracket
				
				//read the actual subscript
				ParseResult<ArrowTokenType> subscriptResult = ExpressionParser.of(indentation, symbolTable).parse(remainder);
				if (!subscriptResult.getSuccess()) {
					return subscriptResult;
				}
				
				//make sure the subscript type is compatible with integer
				if (!subscriptResult.getNode().getDataType().canBeAssignedTo(IntegerType.getInstance())) {
					return ParseResult.failure("Array subscripts must be integers, found " + subscriptResult.getNode().getDataType(), remainder);
				}
				
				subscripts.add(subscriptResult.getNode());
				
				remainder = subscriptResult.getRemainder();
				
				ParseResult<ArrowTokenType> closingSubscriptResult = requireType(remainder, ArrowTokenType.CLOSE_SUBSCRIPT, 1);
				if (!closingSubscriptResult.getSuccess()) {
					return closingSubscriptResult;
				}
				remainder = closingSubscriptResult.getRemainder();
				
			default:
				moreSubscripts = false;
			}
		}
		
		return ParseResult.of(VariableParseTreeNode.of(varEntry, subscripts), remainder);
	}

}
