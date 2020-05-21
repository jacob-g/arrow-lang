package arrow.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import arrow.ArrowTokenType;
import arrow.symboltable.SymbolTableEntry;
import arrow.symboltable.SymbolTableStack;
import lexer.Token;
import memory.MemoryEntry;
import parser.ParseResult;
import parser.tree.DataParseTreeNode;
import parser.tree.MathOperationTreeNode;
import parser.tree.ParseTreeNodeType;
import parser.tree.VariableParseTreeNode;

final class ExpressionParser extends AbstractArrowParser {

	private ExpressionParser(int indentation, SymbolTableStack symbolTable) {
		super(indentation, symbolTable);
	}
	
	public static ExpressionParser of(int indentation, SymbolTableStack symbolTable) {
		requireNonNegative(indentation);
		Objects.requireNonNull(symbolTable);
		
		return new ExpressionParser(indentation, symbolTable);
	}

	private static final Set<ArrowTokenType> ADD_OPERATORS = new HashSet<>(Arrays.asList(ArrowTokenType.PLUS, ArrowTokenType.MINUS));
	private static final Set<ArrowTokenType> MULT_OPERATORS = new HashSet<>(Arrays.asList(ArrowTokenType.TIMES, ArrowTokenType.DIVIDE));
	private static final Set<ArrowTokenType> RELATIONAL_OPERATORS = new HashSet<>(Arrays.asList(ArrowTokenType.GREATER_THAN, ArrowTokenType.LESS_THAN, ArrowTokenType.DOUBLE_EQUAL, ArrowTokenType.NOT_EQUAL));
	
	private static final Map<ArrowTokenType, ParseTreeNodeType> operations = new HashMap<>();
	static {
		operations.put(ArrowTokenType.PLUS, ParseTreeNodeType.ADD);
		operations.put(ArrowTokenType.MINUS, ParseTreeNodeType.SUBTRACT);
		operations.put(ArrowTokenType.TIMES, ParseTreeNodeType.MULTIPLY);
		operations.put(ArrowTokenType.DIVIDE, ParseTreeNodeType.DIVIDE);
		operations.put(ArrowTokenType.GREATER_THAN, ParseTreeNodeType.GREATER_THAN);
		operations.put(ArrowTokenType.LESS_THAN, ParseTreeNodeType.LESS_THAN);
		operations.put(ArrowTokenType.DOUBLE_EQUAL, ParseTreeNodeType.EQUAL);
		operations.put(ArrowTokenType.NOT_EQUAL, ParseTreeNodeType.NOT_EQUAL);
	}
	
	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		return parseTwoSided(tokens, RELATIONAL_OPERATORS, this::parseRelated);
	}
	
	private ParseResult<ArrowTokenType> parseRelated(List<Token<ArrowTokenType>> tokens) {
		return parseTwoSided(tokens, ADD_OPERATORS, this::parseAddend);
	}
	
	//TODO: eliminate repeated code
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
			
			overallResult = ParseResult.of(MathOperationTreeNode.of(operation, overallResult.getNode(), secondOperandResult.getNode()), secondOperandResult.getRemainder());
		}
		
		return overallResult;
	}
	
	private ParseResult<ArrowTokenType> parseFactor(List<Token<ArrowTokenType>> tokens) {
		if (tokens.isEmpty()) {
			return ParseResult.failure("Unexpected end of data", tokens);
		}
		
		switch (tokens.get(0).getType()) {
		case IDENTIFIER:
			return parseIdentifier(tokens);
		case NUMBER:
			return parseNumber(tokens);
		case OPEN_PAREN:
			return parseParenthesized(tokens);
		default:
			return ParseResult.failure("Unexpected symbol in expression: " + tokens.get(0).getContent(), tokens);
		}
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
		
		return ParseResult.of(DataParseTreeNode.of(new MemoryEntry(Integer.parseInt(tokens.get(0).getContent()))), tokenResult.getRemainder());
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
			throw new UnsupportedOperationException("Not yet implemented");
		case VARIABLE:
			return ParseResult.of(VariableParseTreeNode.of(variableEntry), tokens.subList(1, tokens.size()));
		default:
			return ParseResult.failure("Invalid identifier in expression", tokens);
		}
	}

}
