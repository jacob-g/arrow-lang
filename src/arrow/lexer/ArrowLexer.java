package arrow.lexer;

import java.util.Arrays;
import java.util.stream.Collectors;

import lexer.TokenLexResult;
import lexer.specs.CharPredicateSpec;
import lexer.specs.CombinerSpec;
import lexer.specs.FixedStringTokenSpec;
import lexer.specs.MultipleOptionTokenSpec;
import lexer.specs.RepeatedTokenSpec;
import lexer.specs.SequenceTokenSpec;
import lexer.specs.TokenSpec;
import lexer.specs.TransformSpec;
import lexer.specs.TyperSpec;

public class ArrowLexer {
	private static final TokenSpec<ArrowTokenType> specFromCategory(ArrowTokenCategory category) {
		return MultipleOptionTokenSpec.of(
			ArrowTokenType.tokensByCategory(category)
				.stream()
				.map(tokenType -> TyperSpec.of(FixedStringTokenSpec.<ArrowTokenType>of(tokenType.TEXT), tokenType))
				.collect(Collectors.toList())
				);
	}
	
	private static final TokenSpec<ArrowTokenType> whiteSpaceSpec = TyperSpec.of(
			CombinerSpec.of(RepeatedTokenSpec.of(MultipleOptionTokenSpec.of(
					FixedStringTokenSpec.of(" "), 
					FixedStringTokenSpec.of("\t"),
					FixedStringTokenSpec.of("\r")), false)),
			ArrowTokenType.WHITESPACE);
	
	private static final TokenSpec<ArrowTokenType> newLineSpec = TyperSpec.of(FixedStringTokenSpec.of("\n"), ArrowTokenType.NEWLINE);
	
	private static final TokenSpec<ArrowTokenType> keywordSpec = specFromCategory(ArrowTokenCategory.KEYWORD);
	
	private static final TokenSpec<ArrowTokenType> symbolSpec = specFromCategory(ArrowTokenCategory.SYMBOL);
	
	private static final TokenSpec<ArrowTokenType> identifierSpec = TyperSpec.of(
			CombinerSpec.of(SequenceTokenSpec.of(TokenSpec.getLetterSpec(), RepeatedTokenSpec.of(TokenSpec.getAlnumSpec(), false))),
			ArrowTokenType.IDENTIFIER);
	
	private static final TokenSpec<ArrowTokenType> numberSpec = TyperSpec.of(
			CombinerSpec.of(RepeatedTokenSpec.of(TokenSpec.getDigitSpec(), true)), 
			ArrowTokenType.NUMBER);
	
	private static final TokenSpec<ArrowTokenType> charSpec = TyperSpec.of(
			TransformSpec.of(
					SequenceTokenSpec.of(FixedStringTokenSpec.of("'"), CharPredicateSpec.of(c -> c != '\''), FixedStringTokenSpec.of("'")),
					tokens -> Arrays.asList(tokens.get(1))),
			ArrowTokenType.CHAR);
	
	private static final TokenSpec<ArrowTokenType> stringSpec = TyperSpec.of(
			CombinerSpec.of(
					TransformSpec.of(
							SequenceTokenSpec.of(FixedStringTokenSpec.of("\""), RepeatedTokenSpec.of(CharPredicateSpec.of(c -> c != '"'), false), FixedStringTokenSpec.of("\"")),
							tokens -> tokens.subList(1, tokens.size() - 1))
					),
			ArrowTokenType.STRING);
	
	private static final TokenSpec<ArrowTokenType> commentSpec = TyperSpec.of(
			CombinerSpec.of(SequenceTokenSpec.of(
					FixedStringTokenSpec.of("//"), 
					RepeatedTokenSpec.of(CharPredicateSpec.of(c -> c != '\n'), false),
					FixedStringTokenSpec.of("\n"))), ArrowTokenType.NEWLINE);
	
	private static final TokenSpec<ArrowTokenType> allowedWordSpec = MultipleOptionTokenSpec.of(commentSpec, keywordSpec, symbolSpec, numberSpec, charSpec, stringSpec, identifierSpec, newLineSpec, whiteSpaceSpec);
	private static final TokenSpec<ArrowTokenType> programSpec = RepeatedTokenSpec.of(allowedWordSpec, true);
	
	public static TokenLexResult<ArrowTokenType> parse(String program) {
		TokenLexResult<ArrowTokenType> result = programSpec.parse(program);
		
		return result.getSuccess() && result.getRemainder().isEmpty() 
				? TokenLexResult.of(
						result.getResults()
							.stream()
							.filter(token -> token.getType().CATEGORY != ArrowTokenCategory.IGNORE)
							.collect(Collectors.toList()), 
						"") //if the parse succeeded, remove all the ignored characters
				: TokenLexResult.failure(result.getRemainder());
	}
}
