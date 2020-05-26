package arrow.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import arrow.lexer.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;
import parser.tree.BuiltInFunctionNode;
import parser.tree.EmptyParseTreeNode;
import parser.tree.ParseTreeNode;
import symboltable.StaticSymbolTableStack;
import symboltable.SymbolTableEntry;

final class LineParser extends AbstractArrowParser {
	private LineParser(int indentation, StaticSymbolTableStack symbolTable) {
		super(indentation, symbolTable);
	}
	
	public static LineParser of(int indentation, StaticSymbolTableStack symbolTable) {
		requireNonNegative(indentation);
		Objects.requireNonNull(symbolTable);
		
		return new LineParser(indentation, symbolTable);
	}

	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {		
		assert !tokens.isEmpty();
		
		ParseResult<ArrowTokenType> lineParseResult;
		switch (tokens.get(0).getType().CATEGORY) {
		case IDENTIFIER:
			lineParseResult = parseIdentifier(tokens);
			break;
		case KEYWORD:
			lineParseResult = parseKeyword(tokens);
			break;
		case NUMBER:
			lineParseResult = ParseResult.failure("Unexpected number at the start of a line", tokens);
			break;
		case SYMBOL:
			lineParseResult = parseSymbol(tokens);
			break;
		case NEWLINE:
			//do nothing since we've reached the end of the line
			lineParseResult = ParseResult.of(new EmptyParseTreeNode(), tokens);
			break;
		case IGNORE:
			assert false : "ignored tokens not filtered out";
			lineParseResult = null;
			break;
		default:
			assert false : "keyword type not covered";
			lineParseResult = null;
			break;
		}
		
		if (!lineParseResult.getSuccess()) {
			return lineParseResult;
		}
		
		assert lineParseResult.getSuccess();
		
		//make sure that the line ends with a newline
		ParseResult<ArrowTokenType> endOfLineResult = requireType(lineParseResult.getRemainder(), ArrowTokenType.NEWLINE, 1);
		
		return endOfLineResult.getSuccess() ? ParseResult.of(lineParseResult.getNode(), endOfLineResult.getRemainder()) : endOfLineResult;
	}
	
	private ParseResult<ArrowTokenType> parseIdentifier(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty();
		
		String identifier = tokens.get(0).getContent();
		
		if (!symbolTable.contains(identifier)) {
			return ParseResult.failure("Undefined identifier: " + identifier, tokens);
		}
		
		SymbolTableEntry idEntry = symbolTable.lookup(identifier);
		switch (idEntry.getType()) {
		case VARIABLE:
		case TYPE:
			return new AssignmentDeclarationParser(indentation, symbolTable).parse(tokens);
		case FUNCTION:
			return ExpressionParser.of(indentation, symbolTable).parse(tokens);
		default:
			assert false;
			return null;
		}
	}

	private ParseResult<ArrowTokenType> parseSymbol(List<Token<ArrowTokenType>> tokens) {
		assert tokens != null && !tokens.isEmpty();
		
		switch (tokens.get(0).getType()) {
		case START_IF:
			return IfParser.of(indentation, symbolTable).parse(tokens);
		case BEGIN_DO_WHILE:
			return LoopParser.of(indentation, symbolTable).parse(tokens);
		default:
			return ParseResult.failure("Unexpected token to start line: " + tokens.get(0).getType(), tokens);
		}
	}
	
	private ParseResult<ArrowTokenType> parseKeyword(List<Token<ArrowTokenType>> tokens) {
		assert !tokens.isEmpty();
		
		switch (tokens.get(0).getType()) {
		case PRINT:
			return parsePrint(tokens);
		default:
			assert false;
			return null;
		}
	}
	
	private ParseResult<ArrowTokenType> parsePrint(List<Token<ArrowTokenType>> tokens) {
		assert tokens != null && !tokens.isEmpty();
		
		List<Token<ArrowTokenType>> remainder = tokens.subList(1, tokens.size());
		
		List<ParseTreeNode> values = new LinkedList<>();
		
		boolean moreValues = true;
		while (moreValues) {
			ParseResult<ArrowTokenType> exprResult = ExpressionParser.of(indentation, symbolTable).parse(remainder);
			if (!exprResult.getSuccess()) {
				return exprResult;
			}
			
			values.add(exprResult.getNode());
			
			remainder = exprResult.getRemainder();
			
			moreValues = exprResult.getRemainder().size() > 2 && remainder.get(0).getType() == ArrowTokenType.COMMA;
			if (moreValues) {
				remainder = remainder.subList(1, remainder.size());
			}
		}
		
		return ParseResult.of(BuiltInFunctionNode.print(values), remainder);
	}
}
