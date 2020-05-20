package arrow.parser;

import java.util.List;

import arrow.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;

public class ExpressionParser extends AbstractArrowParser {

	private ExpressionParser(int indentation) {
		super(indentation);
	}
	
	public static ExpressionParser of(int indentation) {
		requireNonNegative(indentation);
		
		return new ExpressionParser(indentation);
	}

	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		// TODO Auto-generated method stub
		return null;
	}

}
