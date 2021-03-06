package arrow.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import arrow.lexer.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;
import parser.tree.ArgumentParseTreeNode;
import parser.tree.FunctionParseTreeNode;
import parser.tree.ParseTreeNode;
import parser.tree.ReturnParseTreeNode;
import parser.tree.VariableParseTreeNode;
import symboltable.StaticSymbolTableStack;
import symboltable.SymbolTableEntry;
import symboltable.SymbolTableEntryType;
import typesystem.Type;

final class FunctionDefinitionParser extends AbstractArrowParser {

	private FunctionDefinitionParser(int indentation, StaticSymbolTableStack symbolTable) {
		super(indentation, symbolTable);
	}
	
	public static FunctionDefinitionParser of(int indentation, StaticSymbolTableStack symbolTable) {
		Objects.requireNonNull(symbolTable);
		requireNonNegative(indentation);
		
		return new FunctionDefinitionParser(indentation, symbolTable);
	}

	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty() && tokens.get(0).getType() == ArrowTokenType.FUNCTION;
		
		if (tokens.size() < 4) {
			return ParseResult.failure("Invalid function declaration syntax", tokens);
		}
		
		ParseResult<ArrowTokenType> newLineCheckResult = requireType(tokens.subList(1, tokens.size()), ArrowTokenType.NEWLINE, 1);
		if (!newLineCheckResult.getSuccess()) {
			return newLineCheckResult;
		}
		
		List<Token<ArrowTokenType>> remainder = newLineCheckResult.getRemainder();
		
		ParseResult<ArrowTokenType> beginCheckResult = requireType(remainder, ArrowTokenType.BEGIN_DO_WHILE, 1);
		if (!beginCheckResult.getSuccess()) {
			return beginCheckResult;
		}
		remainder = beginCheckResult.getRemainder();
		
		//parse the return type
		ParseResult<ArrowTokenType> typeResult = TypeParser.of(indentation, symbolTable).parse(remainder);
		if (!typeResult.getSuccess()) {
			return typeResult;
		}
		
		remainder = typeResult.getRemainder();
		Type returnType = typeResult.getNode().getDataType();
		
		//parse the name
		Token<ArrowTokenType> nameToken = remainder.get(0);
		if (nameToken.getType() != ArrowTokenType.IDENTIFIER) {
			return ParseResult.failure("Illegal function name", remainder);
		}
		if (symbolTable.contains(nameToken.getContent())) { //make sure we haven't already defined something under this name
			return ParseResult.failure("Function name already defined: " + nameToken.getContent(), remainder);
		}
		SymbolTableEntry functionIdentifier = symbolTable.add(nameToken.getContent(), SymbolTableEntryType.FUNCTION, returnType);
		
		//now read the arguments
		symbolTable.push();
		
		remainder = remainder.subList(1, remainder.size());
		ParseResult<ArrowTokenType> openParenResult = requireType(remainder, ArrowTokenType.OPEN_PAREN, 1);
		if (!openParenResult.getSuccess()) {
			return ParseResult.failure("Missing open parenthesis for function arguments", remainder);
		}
		remainder = openParenResult.getRemainder();
		
		List<ParseTreeNode> argNodes = new LinkedList<>();
		
		boolean moreArguments = true;
		while (moreArguments) {
			if (remainder.isEmpty()) {
				return ParseResult.failure("Unexpected end-of-data", remainder);
			}
			
			Token<ArrowTokenType> firstToken = remainder.get(0);
			switch (firstToken.getType()) {
			case IDENTIFIER:
				//we're parsing an argument
				ParseResult<ArrowTokenType> argResult = parseArgument(remainder);
				if (!argResult.getSuccess()) {
					return argResult;
				}
				argNodes.add(argResult.getNode());
				remainder = argResult.getRemainder();
				break;
			case CLOSE_PAREN:
				//we hit the end of the argument list
				moreArguments = false;
				remainder = remainder.subList(1, remainder.size());
				break;
			default:
				return ParseResult.failure("Unexpected argument type", remainder);
			}
		}
		
		if (remainder.get(0).getType() != ArrowTokenType.NEWLINE) {
			return ParseResult.failure("Expected newline at end of function header", remainder);
		}
		remainder = remainder.subList(1, remainder.size());
		
		//now parse the body
		List<ParseTreeNode> children = new LinkedList<>();
		
		boolean moreBody = true;
		while (moreBody) {
			if (tokens.isEmpty()) {
				return ParseResult.failure("Unexpected end of data", remainder);
			}
			
			Token<ArrowTokenType> firstToken = remainder.get(0);
			
			switch (firstToken.getType()) {
			case PIPE:
				//this is just a line in the function
				ParseResult<ArrowTokenType> lineResult = LineParser.of(1, symbolTable).parse(remainder.subList(1, remainder.size()));
				if (!lineResult.getSuccess()) {
					return lineResult;
				}
				remainder = lineResult.getRemainder();
				children.add(lineResult.getNode());
				break;
			case RETURN:
				//we hit a return so bail out
				ParseResult<ArrowTokenType> returnResult = ExpressionParser.of(1, symbolTable).parse(remainder.subList(1, remainder.size()));
				if (!returnResult.getSuccess()) {
					return returnResult;
				}
				if (!returnResult.getNode().getDataType().isCompatibleWith(returnType)) {
					return ParseResult.failure("Incompatible return type to function", remainder);
				}
				remainder = returnResult.getRemainder();
				children.add(ReturnParseTreeNode.of(returnResult.getNode()));
				moreBody = false;
				break;
			default:
				return ParseResult.failure("Unexpected token type at start of line in function", remainder);
			}
		}
		
		symbolTable.pop();
		
		ParseTreeNode functionNode = FunctionParseTreeNode.of(children, ArgumentParseTreeNode.of(argNodes), returnType);
		functionIdentifier.setPayload(functionNode);
		return ParseResult.of(functionNode, remainder);
	}

	private ParseResult<ArrowTokenType> parseArgument(List<Token<ArrowTokenType>> tokens) {
		List<Token<ArrowTokenType>> remainder = tokens;
		
		if (remainder.size() < 3) {
			return ParseResult.failure("Not enough data given to function argument", remainder);
		}
				
		//make sure the type given is actually a type
		ParseResult<ArrowTokenType> typeResult = TypeParser.of(indentation, symbolTable).parse(tokens);
		if (!typeResult.getSuccess()) {
			return typeResult;
		}
		Type argType = typeResult.getNode().getDataType();
		remainder = typeResult.getRemainder();
		
		//make sure a non-previously-defined variable name is given as an argument
		Token<ArrowTokenType> argNameToken = remainder.get(0);
		if (argNameToken.getType() != ArrowTokenType.IDENTIFIER) {
			return ParseResult.failure("Name not given for function argument", remainder);
		}
		if (symbolTable.contains(argNameToken.getContent())) {
			return ParseResult.failure("Function argument name already defined: " + argNameToken.getContent(), remainder);
		}
		SymbolTableEntry argEntry = symbolTable.add(argNameToken.getContent(), SymbolTableEntryType.VARIABLE, argType);
		
		//if it ends with a comma, consume it
		if (remainder.get(1).getType() == ArrowTokenType.COMMA) {
			remainder = remainder.subList(2, remainder.size());
		} else {
			remainder = remainder.subList(1, remainder.size());
		}
		
		return ParseResult.of(VariableParseTreeNode.of(argEntry, new ArrayList<>()), remainder);
	}
}
