package arrow.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import arrow.lexer.ArrowTokenType;
import lexer.Token;
import memory.ScalarMemoryEntry;
import parser.ParseResult;
import parser.tree.BuiltInFunctionNode;
import parser.tree.DataParseTreeNode;
import parser.tree.MathOperationTreeNode;
import parser.tree.ParseTreeNodeType;
import symboltable.StaticSymbolTableStack;
import symboltable.SymbolTableEntry;
import typesystem.ArrayType;
import typesystem.BoolType;
import typesystem.CharType;
import typesystem.GenericArrayType;
import typesystem.IntegerType;
import typesystem.Type;

final class ExpressionParser extends AbstractArrowParser {

	private ExpressionParser(int indentation, StaticSymbolTableStack symbolTable) {
		super(indentation, symbolTable);
	}
	
	public static ExpressionParser of(int indentation, StaticSymbolTableStack symbolTable) {
		requireNonNegative(indentation);
		Objects.requireNonNull(symbolTable);
		
		return new ExpressionParser(indentation, symbolTable);
	}

	private static final Set<ArrowTokenType> ADD_OPERATORS = new HashSet<>(Arrays.asList(ArrowTokenType.PLUS, ArrowTokenType.MINUS));
	private static final Set<ArrowTokenType> MULT_OPERATORS = new HashSet<>(Arrays.asList(ArrowTokenType.TIMES, ArrowTokenType.DIVIDE, ArrowTokenType.MODULO));
	private static final Set<ArrowTokenType> RELATIONAL_OPERATORS = new HashSet<>(Arrays.asList(ArrowTokenType.GREATER_THAN, ArrowTokenType.LESS_THAN, ArrowTokenType.DOUBLE_EQUAL, ArrowTokenType.NOT_EQUAL));
	private static final Set<ArrowTokenType> BOOLEAN_BINARY_OPERATORS = new HashSet<>(Arrays.asList(ArrowTokenType.AND, ArrowTokenType.OR));
	private static final Set<ArrowTokenType> UNARY_OPERATORS = new HashSet<>(Arrays.asList(ArrowTokenType.NOT, ArrowTokenType.MINUS));
	
	private static final Map<ArrowTokenType, ParseTreeNodeType> operations = new HashMap<>();
	static {
		operations.put(ArrowTokenType.PLUS, ParseTreeNodeType.ADD);
		operations.put(ArrowTokenType.MINUS, ParseTreeNodeType.SUBTRACT);
		operations.put(ArrowTokenType.TIMES, ParseTreeNodeType.MULTIPLY);
		operations.put(ArrowTokenType.DIVIDE, ParseTreeNodeType.DIVIDE);
		operations.put(ArrowTokenType.MODULO, ParseTreeNodeType.MODULO);
		operations.put(ArrowTokenType.GREATER_THAN, ParseTreeNodeType.GREATER_THAN);
		operations.put(ArrowTokenType.LESS_THAN, ParseTreeNodeType.LESS_THAN);
		operations.put(ArrowTokenType.DOUBLE_EQUAL, ParseTreeNodeType.EQUAL);
		operations.put(ArrowTokenType.NOT_EQUAL, ParseTreeNodeType.NOT_EQUAL);
		operations.put(ArrowTokenType.AND, ParseTreeNodeType.AND);
		operations.put(ArrowTokenType.OR, ParseTreeNodeType.OR);
	}
	
	private static final Map<ArrowTokenType, ParseTreeNodeType> unaryOperations = new HashMap<>();
	static {
		unaryOperations.put(ArrowTokenType.MINUS, ParseTreeNodeType.NEGATE);
		unaryOperations.put(ArrowTokenType.NOT, ParseTreeNodeType.NOT);
	}
	
	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		return parseTwoSided(tokens, BOOLEAN_BINARY_OPERATORS, this::parseBooleanTerm);
	}
	
	private ParseResult<ArrowTokenType> parseBooleanTerm(List<Token<ArrowTokenType>> tokens) {
		return parseTwoSided(tokens, RELATIONAL_OPERATORS, this::parseRelated);
	}
	
	private ParseResult<ArrowTokenType> parseRelated(List<Token<ArrowTokenType>> tokens) {
		return parseTwoSided(tokens, ADD_OPERATORS, this::parseAddend);
	}
	
	private ParseResult<ArrowTokenType> parseAddend(List<Token<ArrowTokenType>> tokens) {
		return parseTwoSided(tokens, MULT_OPERATORS, this::parseFactor);
	}
	
	private ParseResult<ArrowTokenType> parseTwoSided(List<Token<ArrowTokenType>> tokens, Set<ArrowTokenType> operators, Function<List<Token<ArrowTokenType>>, ParseResult<ArrowTokenType>> innerParser) {
		if (tokens.size() < 1) {
			return ParseResult.failure("Unexpected end of data", tokens);
		}
		
		ParseResult<ArrowTokenType> overallResult = innerParser.apply(tokens);
		if (!overallResult.getSuccess()) {
			return overallResult;
		}
				
		if (overallResult.getRemainder().size() >= 2 && operators.contains(overallResult.getRemainder().get(0).getType())) {
			ParseTreeNodeType operation = operations.get(overallResult.getRemainder().get(0).getType());
			ParseResult<ArrowTokenType> secondOperandResult = innerParser.apply(overallResult.getRemainder().subList(1, overallResult.getRemainder().size()));
			if (!secondOperandResult.getSuccess()) {
				return secondOperandResult;
			}
			
			//type checking
			if (!overallResult.getNode().getDataType().binaryOperationResult(operation, secondOperandResult.getNode().getDataType()).isPresent()) {
				return ParseResult.failure("Incompatible types: " + overallResult.getNode().getDataType() + " " + operation + " " + secondOperandResult.getNode().getDataType(), tokens);
			}
			
			overallResult = ParseResult.of(MathOperationTreeNode.of(operation, overallResult.getNode(), secondOperandResult.getNode()), secondOperandResult.getRemainder());
		}
		
		return overallResult;
	}
	
	private ParseResult<ArrowTokenType> parseFactor(List<Token<ArrowTokenType>> tokens) {
		if (tokens.isEmpty()) {
			return ParseResult.failure("Unexpected end of data", tokens);
		}
		
		if (UNARY_OPERATORS.contains(tokens.get(0).getType())) {
			return parseUnary(tokens);
		}
		
		switch (tokens.get(0).getType()) {
		case IDENTIFIER:
			return parseIdentifier(tokens);
		case CHAR:
			return parseChar(tokens);
		case STRING:
			return parseString(tokens);
		case NUMBER:
			return parseNumber(tokens);
		case OPEN_PAREN:
			return parseParenthesized(tokens);
		case TRUE:
			return ParseResult.of(DataParseTreeNode.of(BoolType.getTrue()), tokens.subList(1, tokens.size()));
		case FALSE:
			return ParseResult.of(DataParseTreeNode.of(BoolType.getFalse()), tokens.subList(1, tokens.size()));
		case LENGTH:
			return parseLength(tokens);
		case INPUT:
			return parseInput(tokens);
		default:
			return ParseResult.failure("Unexpected symbol in expression: " + tokens.get(0).getContent(), tokens);
		}
	}
	
	private ParseResult<ArrowTokenType> parseInput(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty() && tokens.get(0).getType() == ArrowTokenType.INPUT;
		
		ParseResult<ArrowTokenType> typeResult = TypeParser.of(indentation, symbolTable).parse(tokens.subList(1, tokens.size()));
		if (!typeResult.getSuccess()) {
			return typeResult;
		}
		
		Type inputType = typeResult.getNode().getDataType();
		
		if (!Arrays.asList(IntegerType.getInstance(), ArrayType.of(CharType.getInstance())).stream().anyMatch(type -> type.isCompatibleWith(inputType))) {
			return ParseResult.failure("Unsupported type for input: " + inputType, tokens);
		}
		
		return ParseResult.of(BuiltInFunctionNode.input(inputType), typeResult.getRemainder());
	}

	private ParseResult<ArrowTokenType> parseLength(List<Token<ArrowTokenType>> tokens) {
		if (tokens.size() < 2) {
			return ParseResult.failure("Unexpected end-of-data", tokens);
		}
		
		ParseResult<ArrowTokenType> exprParseResult = parseFactor(tokens.subList(1, tokens.size()));
		if (!exprParseResult.getSuccess()) {
			return exprParseResult;
		}
		
		if (!GenericArrayType.getInstance().isCompatibleWith(exprParseResult.getNode().getDataType())) {
			return ParseResult.failure("Argument to length must be array, found " + exprParseResult.getNode().getType(), tokens);
		}
		
		return ParseResult.of(BuiltInFunctionNode.arrayLength(exprParseResult.getNode()), exprParseResult.getRemainder());
	}
	
	private ParseResult<ArrowTokenType> parseString(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty() && tokens.get(0).getType() == ArrowTokenType.STRING;
		
		String payload = tokens.get(0).getContent();
				
		return ParseResult.of(DataParseTreeNode.of(CharType.getInstance().fromString(payload)), tokens.subList(1, tokens.size()));
	}

	private ParseResult<ArrowTokenType> parseChar(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty() && tokens.get(0).getType() == ArrowTokenType.CHAR;
		
		return ParseResult.of(DataParseTreeNode.of(ScalarMemoryEntry.initialized((int)tokens.get(0).getContent().charAt(0), CharType.getInstance())), tokens.subList(1, tokens.size()));
	}

	private ParseResult<ArrowTokenType> parseUnary(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty() && UNARY_OPERATORS.contains(tokens.get(0).getType());
		
		if (tokens.size() < 2) {
			return ParseResult.failure("Unexpected end of data", tokens);
		}
		
		ParseResult<ArrowTokenType> operandResult = parseAddend(tokens.subList(1, tokens.size()));
		if (!operandResult.getSuccess()) {
			return operandResult;
		}
		
		ParseTreeNodeType operation = unaryOperations.get(tokens.get(0).getType());
		
		if (!operandResult.getNode().getDataType().unaryOperationResult(operation).isPresent()) {
			return ParseResult.failure("Invalid unary operation " + operation + " on " + operandResult.getNode().getDataType(), tokens);
		}
		
		return ParseResult.of(MathOperationTreeNode.of(operation, operandResult.getNode()), operandResult.getRemainder());
	}

	private ParseResult<ArrowTokenType> parseParenthesized(List<Token<ArrowTokenType>> tokens) {
		ParseResult<ArrowTokenType> openParenResult = requireType(tokens, ArrowTokenType.OPEN_PAREN, 1);
		if (!openParenResult.getSuccess()) {
			return openParenResult;
		}
		
		ParseResult<ArrowTokenType> bodyResult = parse(openParenResult.getRemainder());
		if (!bodyResult.getSuccess()) {
			return bodyResult;
		}
		
		ParseResult<ArrowTokenType> closeParenResult = requireType(bodyResult.getRemainder(), ArrowTokenType.CLOSE_PAREN, 1);
		if (!closeParenResult.getSuccess()) {
			return closeParenResult;
		}
		
		return ParseResult.of(bodyResult.getNode(), closeParenResult.getRemainder());
	}
	
	private ParseResult<ArrowTokenType> parseNumber(List<Token<ArrowTokenType>> tokens) {
		ParseResult<ArrowTokenType> tokenResult = requireType(tokens, ArrowTokenType.NUMBER, 1);
		if (!tokenResult.getSuccess()) {
			return tokenResult;
		}
		
		assert !tokens.isEmpty();
		
		return ParseResult.of(DataParseTreeNode.of(ScalarMemoryEntry.initialized(Integer.parseInt(tokens.get(0).getContent()), IntegerType.getInstance())), tokenResult.getRemainder());
	}
	
	private ParseResult<ArrowTokenType> parseIdentifier(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty() && tokens.get(0).getType() == ArrowTokenType.IDENTIFIER;
		
		final String identifier = tokens.get(0).getContent();
		
		if (!symbolTable.contains(identifier)) {
			return ParseResult.failure("Use of undeclared variable in expression: " + identifier, tokens);
		}
		
		SymbolTableEntry variableEntry = symbolTable.lookup(identifier);
		switch (variableEntry.getType()) {
		case FUNCTION:
			return FunctionCallParser.of(indentation, symbolTable).parse(tokens);
		case VARIABLE:
			ParseResult<ArrowTokenType> varResult = VariableParser.of(indentation, symbolTable).parse(tokens);
			if (!varResult.getSuccess()) {
				return varResult;
			}
			return ParseResult.of(varResult.getNode(), varResult.getRemainder());
		default:
			return ParseResult.failure("Invalid identifier in expression", tokens);
		}
	}

}
