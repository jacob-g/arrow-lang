package arrow.parser;

import java.util.List;
import java.util.Objects;

import arrow.lexer.ArrowTokenType;
import lexer.Token;
import parser.ParseResult;
import parser.tree.TypeNode;
import symboltable.StaticSymbolTableStack;
import typesystem.ArrayType;
import typesystem.Type;

final class TypeParser extends AbstractArrowParser {

	private TypeParser(int indentation, StaticSymbolTableStack symbolTable) {
		super(indentation, symbolTable);
	}
	
	public static TypeParser of(int indentation, StaticSymbolTableStack symbolTable) {
		Objects.requireNonNull(symbolTable);
		requireNonNegative(indentation);
		
		return new TypeParser(indentation, symbolTable);
	}

	@Override
	public ParseResult<ArrowTokenType> parse(List<Token<ArrowTokenType>> tokens) {
		//get the data type
		final String typeName = tokens.get(0).getContent();
		Type varType = symbolTable.lookup(typeName).getDataType();
		
		List<Token<ArrowTokenType>> remainder = tokens.subList(1, tokens.size());
		
		//parse array subscripts
		//TODO: move this out so it can be used for function parameters/return types too
		if (remainder.get(0).getType() == ArrowTokenType.OPEN_SUBSCRIPT) {
			boolean moreSubscripts = true;
			while (moreSubscripts) {
				if (remainder.size() < 2) {
					return ParseResult.failure("Unexpected end of data", remainder);
				}

				if (remainder.get(0).getType() == ArrowTokenType.OPEN_SUBSCRIPT) {
					//we found a [] in the text, so nest the current type inside another array
					if (remainder.get(1).getType() == ArrowTokenType.CLOSE_SUBSCRIPT) {
						remainder = remainder.subList(2, remainder.size());
						varType = ArrayType.of(varType);
					} else {
						return ParseResult.failure("Improperly formatted array declaration", remainder);
					}
				} else {
					moreSubscripts = false;
				}
			}
		}
		
		return ParseResult.of(TypeNode.of(varType), remainder);
	}

}
