package arrow.parser;

import java.util.LinkedList;
import java.util.List;

import arrow.lexer.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;
import parser.tree.AssignmentParseTreeNode;
import parser.tree.DeclarationParseTreeNode;
import parser.tree.ParseTreeNode;
import parser.tree.VariableParseTreeNode;
import symboltable.StaticSymbolTableStack;
import symboltable.SymbolTableEntryType;
import typesystem.IntegerType;
import typesystem.Type;

final class AssignmentDeclarationParser extends AbstractArrowParser {

	protected AssignmentDeclarationParser(int indentation, StaticSymbolTableStack symbolTable) {
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
		
		//get the data type
		ParseResult<ArrowTokenType> typeResult = TypeParser.of(indentation, symbolTable).parse(tokens);
		if (!typeResult.getSuccess()) {
			return typeResult;
		}
		
		Type varType = typeResult.getNode().getDataType();
		
		List<Token<ArrowTokenType>> remainder = typeResult.getRemainder();
		
		//make sure we actually named the variable
		if (remainder.get(0).getType() != ArrowTokenType.IDENTIFIER) {
			return ParseResult.failure("Missing variable name in declaration", tokens);
		}
		
		//now actually enter the variable into the symbol table
		final String varName = remainder.get(0).getContent();
		
		if (symbolTable.contains(varName)) {
			return ParseResult.failure("Redefining previously defined identifier: " + varName, tokens);
		}
		
		remainder = remainder.subList(1, remainder.size());
		
		//TODO: this is repeated with the VariableParser
		Type dimType = varType;
		List<ParseTreeNode> dimensions = new LinkedList<>();
		boolean moreSubscripts = true;
		while (moreSubscripts) {
			if (remainder.isEmpty()) {
				return ParseResult.failure("Unexpected end-of-data", remainder);
			}
			
			switch (remainder.get(0).getType()) {
			case OPEN_SUBSCRIPT:
				if (!dimType.isArrayType()) {
					return ParseResult.failure("Too many subscripts", remainder);
				}
				
				dimType = dimType.getUnderlyingType();
				
				remainder = remainder.subList(1, remainder.size()); //consume the open bracket
				
				//read the actual subscript
				ParseResult<ArrowTokenType> subscriptResult = ExpressionParser.of(indentation, symbolTable).parse(remainder);
				if (!subscriptResult.getSuccess()) {
					return subscriptResult;
				}
				
				//make sure the subscript type is compatible with integer
				if (!subscriptResult.getNode().getDataType().isCompatibleWith(IntegerType.getInstance())) {
					return ParseResult.failure("Array subscripts must be integers, found " + subscriptResult.getNode().getDataType(), remainder);
				}
				
				dimensions.add(subscriptResult.getNode());
				
				remainder = subscriptResult.getRemainder();
				
				ParseResult<ArrowTokenType> closingSubscriptResult = requireType(remainder, ArrowTokenType.CLOSE_SUBSCRIPT, 1);
				if (!closingSubscriptResult.getSuccess()) {
					return closingSubscriptResult;
				}
				remainder = closingSubscriptResult.getRemainder();
				break;
				
			default:
				moreSubscripts = false;
			}
		}
		
		ParseTreeNode varNode = VariableParseTreeNode.of(symbolTable.add(varName, SymbolTableEntryType.VARIABLE, varType), dimensions);
		
		return ParseResult.of(DeclarationParseTreeNode.of(varNode, dimensions), remainder);
	}

	private ParseResult<ArrowTokenType> parseAssignment(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty() && representsVariable(tokens.get(0));
						
		if (tokens.size() < 3) {
			return ParseResult.failure("Improperly formatted assignment", tokens);
		}
		
		List<Token<ArrowTokenType>> remainder = tokens;
		ParseResult<ArrowTokenType> varResult = VariableParser.of(indentation, symbolTable).parse(remainder);
		if (!varResult.getSuccess()) {
			return varResult;
		}
		remainder = varResult.getRemainder();
				
		//consume the equal sign
		ParseResult<ArrowTokenType> equalParseResult = requireType(remainder, ArrowTokenType.SINGLE_EQUAL, 1);
		if (!equalParseResult.getSuccess()){
			return equalParseResult;
		}
		remainder = equalParseResult.getRemainder();
		
		//parse the value
		ParseResult<ArrowTokenType> valueResult = ExpressionParser.of(indentation, symbolTable).parse(equalParseResult.getRemainder());
		if (!valueResult.getSuccess()) {
			return valueResult;
		}
		
		//make sure the types are compatible
		Type valueType = valueResult.getNode().getDataType();
		if (!varResult.getNode().getDataType().isCompatibleWith(valueType)) {
			return ParseResult.failure("Incompatible types for assignment: assigning " + valueType + " to variable of type " + varResult.getNode().getDataType(), tokens);
		}
		
		//actually put the thing together
		AssignmentParseTreeNode assignNode = AssignmentParseTreeNode.of(varResult.getNode(), valueResult.getNode());
		
		return ParseResult.of(assignNode, valueResult.getRemainder());
	}
}
