package arrow.parser;

import java.util.List;

import arrow.ArrowTokenType;
import arrow.symboltable.SymbolTableEntryType;
import arrow.symboltable.SymbolTableStack;
import lexer.Token;
import parser.ParseResult;
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
			return ParseResult.failure("Undeclared symbol for assignment", tokens);
		}
	}
	
	private ParseResult<ArrowTokenType> parseIdentifier(List<Token<ArrowTokenType>> tokens) {
		final String identifier = tokens.get(0).getContent();
		ParseResult<ArrowTokenType> identifierParseResult = requireType(tokens, ArrowTokenType.IDENTIFIER, 1);
		if (!identifierParseResult.getSuccess()) {
			return identifierParseResult;
		}
		
		return ParseResult.of(VariableParseTreeNode.of(identifier), identifierParseResult.getRemainder());
	}
	
	private ParseResult<ArrowTokenType> parseDeclaration(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty() && representsType(tokens.get(0));
				
		if (tokens.size() < 2) {
			return ParseResult.failure("Improperly formatted declaration", tokens);
		}
		
		ParseResult<ArrowTokenType> identifierParseResult = parseIdentifier(tokens.subList(1, tokens.size()));
		if (!identifierParseResult.getSuccess()) {
			return identifierParseResult;
		}
		
		final String identifier = tokens.get(1).getContent();
		
		//if this is followed by an equal sign, give a value
		if (tokens.size() >= 4 && tokens.get(2).getType() == ArrowTokenType.SINGLE_EQUAL) {
			ParseResult<ArrowTokenType> assignmentParseResult = parseAssignment(tokens.subList(1, tokens.size()));
			
			if (!assignmentParseResult.getSuccess()) {
				return assignmentParseResult;
			}
		}
		
		//TODO: insert into the symbol table
		if (symbolTable.contains(identifier)) {
			return ParseResult.failure("Redefining previously defined identifier", tokens);
		}
		
		symbolTable.add(identifier, SymbolTableEntryType.VARIABLE);
		
		return null;
	}

	private ParseResult<ArrowTokenType> parseAssignment(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty() && representsVariable(tokens.get(0));
						
		if (tokens.size() < 3) {
			return ParseResult.failure("Improperly formatted assignment", tokens);
		}
		
		ParseResult<ArrowTokenType> identifierParseResult = parseIdentifier(tokens);
		if (!identifierParseResult.getSuccess()) {
			return identifierParseResult;
		}
				
		ParseResult<ArrowTokenType> equalParseResult = requireType(identifierParseResult.getRemainder(), ArrowTokenType.SINGLE_EQUAL, 1);
		if (!equalParseResult.getSuccess()){
			return equalParseResult;
		}
		
		ParseResult<ArrowTokenType> valueResult = ExpressionParser.of(indentation, symbolTable).parse(equalParseResult.getRemainder());
		if (!valueResult.getSuccess()) {
			return valueResult;
		}
		
		//TODO: put together the assignment node with what we got out of this
		
		return null;
	}
}
