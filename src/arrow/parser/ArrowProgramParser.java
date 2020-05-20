package arrow.parser;

import java.util.List;

import arrow.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;

public class ArrowProgramParser extends AbstractArrowParser {

	public ArrowProgramParser() {
		super(0);
	}

	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		ParseResult<ArrowTokenType> mainResult = parseMain(tokens);
		return mainResult;
	}

	public ParseResult<ArrowTokenType> parseMain(List<Token<ArrowTokenType>> tokens) {
		ParseResult<ArrowTokenType> starterResult = requireType(tokens, ArrowTokenType.MAIN, 1);
		if (!starterResult.getSuccess()) {
			return starterResult;
		}
		
		ParseResult<ArrowTokenType> newLineResult = requireType(starterResult.getRemainder(), ArrowTokenType.NEWLINE, 1);
		if (!newLineResult.getSuccess()) {
			return newLineResult;
		}
		
		List<Token<ArrowTokenType>> remainder = newLineResult.getRemainder();
		while (!remainder.isEmpty()) {
			ParseResult<ArrowTokenType> lineResult = LineParser.of(0).parse(remainder);
			
			if (!lineResult.getSuccess()) {
				return lineResult;
			}
			
			remainder = lineResult.getRemainder();
		}
		
		return null;
	}
}
