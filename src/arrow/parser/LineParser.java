package arrow.parser;

import java.util.List;

import arrow.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;
import parser.tree.EmptyParseTreeNode;

public class LineParser extends AbstractArrowParser {
	private LineParser(int indentation) {
		super(indentation);
	}
	
	public static LineParser of(int indentation) {
		requireNonNegative(indentation);
		
		return new LineParser(indentation);
	}

	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		List<Token<ArrowTokenType>> remainder = tokens;
		
		//first check indentation
		ParseResult<ArrowTokenType> indentationResult = parseIndentation(tokens);
		if (!indentationResult.getSuccess()) {
			return indentationResult;
		}
		
		assert !tokens.isEmpty();
		
		ParseResult<ArrowTokenType> lineParseResult;
		switch (tokens.get(0).getType().CATEGORY) {
		case IDENTIFIER:
			lineParseResult = new AssignmentDeclarationParser(indentation).parse(tokens);
			break;
		case KEYWORD:
			lineParseResult = parseKeyword(tokens);
			break;
		case NUMBER:
			lineParseResult = ParseResult.failure("Unexpected number at the start of a line", tokens);
			break;
		case SYMBOL:
			lineParseResult = ParseResult.failure("Unexpected symbol at the start of a line", tokens);
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
		ParseResult<ArrowTokenType> endOfLineResult = requireType(remainder, ArrowTokenType.NEWLINE, 1);
		
		return endOfLineResult.getSuccess() ? ParseResult.of(lineParseResult.getNode(), endOfLineResult.getRemainder()) : endOfLineResult;
	}
	
	private ParseResult<ArrowTokenType> parseKeyword(List<Token<ArrowTokenType>> tokens) {
		return null;
	}
}
