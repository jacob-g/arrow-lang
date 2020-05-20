package arrow;

import lexer.TokenLexResult;
import lexer.specs.CombinerSpec;
import lexer.specs.FixedStringTokenSpec;
import lexer.specs.MultipleOptionTokenSpec;
import lexer.specs.RepeatedTokenSpec;
import lexer.specs.SequenceTokenSpec;
import lexer.specs.TokenSpec;
import lexer.specs.TyperSpec;

public class ArrowLexer {
	private static final TokenSpec<ArrowTokenType> whiteSpaceSpec = TyperSpec.of(
			CombinerSpec.of(RepeatedTokenSpec.of(MultipleOptionTokenSpec.of(
					FixedStringTokenSpec.of(" "), 
					FixedStringTokenSpec.of("\t")), false)),
			ArrowTokenType.IGNORE);
	
	private static final TokenSpec<ArrowTokenType> newLineSpec = TyperSpec.of(FixedStringTokenSpec.of("\n"), ArrowTokenType.NEWLINE);
	
	private static final TokenSpec<ArrowTokenType> keywordSpec = TyperSpec.of(
		MultipleOptionTokenSpec.of(
			FixedStringTokenSpec.of("if"), 
			FixedStringTokenSpec.of("not")),
		ArrowTokenType.KEYWORD);
	
	private static final TokenSpec<ArrowTokenType> symbolSpec = TyperSpec.of(
		MultipleOptionTokenSpec.of(
			FixedStringTokenSpec.of("|-->"),
			FixedStringTokenSpec.of("|--"),
			FixedStringTokenSpec.of("|"),
			FixedStringTokenSpec.of("("),
			FixedStringTokenSpec.of(")"),
			FixedStringTokenSpec.of("=="),
			FixedStringTokenSpec.of("="),
			FixedStringTokenSpec.of("+"),
			FixedStringTokenSpec.of("-"),
			FixedStringTokenSpec.of("*"),
			FixedStringTokenSpec.of("/"),
			FixedStringTokenSpec.of("-->"),
			FixedStringTokenSpec.of("<"),
			FixedStringTokenSpec.of(">")),
		ArrowTokenType.SYMBOL);
	
	private static final TokenSpec<ArrowTokenType> identifierSpec = TyperSpec.of(
			CombinerSpec.of(SequenceTokenSpec.of(TokenSpec.getLetterSpec(), RepeatedTokenSpec.of(TokenSpec.getAlnumSpec(), false))),
			ArrowTokenType.IDENTIFIER);
	
	private static final TokenSpec<ArrowTokenType> numberSpec = TyperSpec.of(
			CombinerSpec.of(RepeatedTokenSpec.of(TokenSpec.getDigitSpec(), true)), 
			ArrowTokenType.NUMBER);
	
	private static final TokenSpec<ArrowTokenType> allowedWordSpec = MultipleOptionTokenSpec.of(keywordSpec, symbolSpec, numberSpec, identifierSpec, newLineSpec, whiteSpaceSpec);
	private static final TokenSpec<ArrowTokenType> programSpec = RepeatedTokenSpec.of(allowedWordSpec, true);
	
	public static TokenLexResult<ArrowTokenType> parse(String program) {
		TokenLexResult<ArrowTokenType> result = programSpec.parse(program);
		
		return result.getSuccess() && result.getRemainder().isEmpty() ? result : TokenLexResult.failure(result.getRemainder());
	}
}
