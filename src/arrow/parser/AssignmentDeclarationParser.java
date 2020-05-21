package arrow.parser;

import java.util.List;

import arrow.ArrowTokenType;
import arrow.symboltable.SymbolTableEntry;
import arrow.symboltable.SymbolTableEntryType;
import arrow.symboltable.SymbolTableStack;
import lexer.Token;
import parser.ParseResult;
import parser.tree.AssignmentParseTreeNode;
import parser.tree.DeclarationParseTreeNode;
import parser.tree.VariableParseTreeNode;

public class AssignmentDeclarationParser extends AbstractArrowParser {

	protected AssignmentDeclarationParser(int indentation, SymbolTableStack symbolTable) {
		super(indentation, symbolTable);
	}
	
	private boolean representsType(Token<ArrowTokenType> token) {
		assert token.getType() == ArrowTokenType.IDENTIFIER;
		
		return symbolTable.contains(token.getContent()) && symbolTable.lookup(token.getContent()).getType() == SymbolTableEntryType.TYPE;
	}
	
	private boolean representsVariable(Token<ArrowTokenType> token) {
		assert token.getType() == ArrowTokenType.IDENTIFIER;
		
		return symbolTable.contains(token.getContent()) && symbolTable.lookup(token.getContent()).getType() == SymbolTableEntryType.VARIABLE;
	}

	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		if (tokens.isEmpty()) {
			return ParseResult.failure("Empty list provided to assignment/declaration parser", tokens);
		}
		
		if (representsType(tokens.get(0))) {
			return parseDeclaration(tokens);
		} else if (representsVariable(tokens.get(0))) {
			return parseAssignment(tokens);
		} else {
			return ParseResult.failure("Undeclared symbol for assignment: " + tokens.get(0).getContent(), tokens);
		}
	}
	
	private ParseResult<ArrowTokenType> parseDeclaration(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty() && representsType(tokens.get(0));
				
		if (tokens.size() < 2) {
			return ParseResult.failure("Improperly formatted declaration", tokens);
		}
		
		if (tokens.get(1).getType() != ArrowTokenType.IDENTIFIER) {
			return ParseResult.failure("Missing variable name in declaration", tokens);
		}
		
		final String identifier = tokens.get(1).getContent();
		
		if (symbolTable.contains(identifier)) {
			return ParseResult.failure("Redefining previously defined identifier", tokens);
		}
		
		VariableParseTreeNode varNode = VariableParseTreeNode.of(symbolTable.add(identifier, SymbolTableEntryType.VARIABLE));
		
		return ParseResult.of(DeclarationParseTreeNode.of(varNode), tokens.subList(2, tokens.size()));
	}

	private ParseResult<ArrowTokenType> parseAssignment(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty() && representsVariable(tokens.get(0));
						
		if (tokens.size() < 3) {
			return ParseResult.failure("Improperly formatted assignment", tokens);
		}
		
		if (tokens.get(0).getType() != ArrowTokenType.IDENTIFIER) {
			return ParseResult.failure("Missing variable name in assignment", tokens);
		}
		
		final String identifier = tokens.get(0).getContent();
		if (!symbolTable.contains(identifier)) {
			return ParseResult.failure("Assignment undeclared variable: " + identifier, tokens);
		}
		
		SymbolTableEntry varEntry = symbolTable.lookup(identifier);
		if (varEntry.getType() != SymbolTableEntryType.VARIABLE) {
			return ParseResult.failure("Incorrect symbol type in assignment: " + varEntry.getType() + " for " + identifier, tokens);
		}
				
		ParseResult<ArrowTokenType> equalParseResult = requireType(tokens.subList(1, tokens.size()), ArrowTokenType.SINGLE_EQUAL, 1);
		if (!equalParseResult.getSuccess()){
			return equalParseResult;
		}
		
		ParseResult<ArrowTokenType> valueResult = ExpressionParser.of(indentation, symbolTable).parse(equalParseResult.getRemainder());
		if (!valueResult.getSuccess()) {
			return valueResult;
		}
		
		VariableParseTreeNode varNode = VariableParseTreeNode.of(varEntry);
		AssignmentParseTreeNode assignNode = AssignmentParseTreeNode.of(varNode, valueResult.getNode());
		
		return ParseResult.of(assignNode, valueResult.getRemainder());
	}
}
