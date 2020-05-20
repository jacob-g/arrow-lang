package arrow.parser;

import java.util.List;

import arrow.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;
import parser.tree.VariableParseTreeNode;

public class AssignmentDeclarationParser extends AbstractArrowParser {

	protected AssignmentDeclarationParser(int indentation) {
		super(indentation);
	}
	
	//TODO: link these with the symbol table
	private boolean representsType(Token<ArrowTokenType> token) {
		assert token.getType() == ArrowTokenType.IDENTIFIER;
		
		return token.getContent().equals("bool");
	}
	
	private boolean representsVariable(Token<ArrowTokenType> token) {
		assert token.getType() == ArrowTokenType.IDENTIFIER;
		
		return true;
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
			return ParseResult.failure("Unexpected symbol for assignment", tokens);
		}
	}
	
	//TODO: make this look in the symbol table
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
		
		//if this is followed by an equal sign, give a value
		if (tokens.size() >= 4 && tokens.get(2).getType() == ArrowTokenType.SINGLE_EQUAL) {
			ParseResult<ArrowTokenType> assignmentParseResult = parseAssignment(tokens.subList(1, tokens.size()));
			
			if (!assignmentParseResult.getSuccess()) {
				return assignmentParseResult;
			}
		}
		
		//TODO: insert into the symbol table
		
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
		
		ParseResult<ArrowTokenType> valueResult = ExpressionParser.of(indentation).parse(equalParseResult.getRemainder());
		if (!valueResult.getSuccess()) {
			return valueResult;
		}
		
		//TODO: put together the assignment node with what we got out of this
		
		return null;
	}
}
