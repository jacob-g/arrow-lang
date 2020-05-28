import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import arrow.lexer.ArrowLexer;
import arrow.lexer.ArrowTokenType;
import arrow.parser.ArrowProgramParser;
import executor.CompoundExecutor;
import executor.ExecutionResult;
import lexer.TokenLexResult;
import memory.RuntimeDataStack;
import parser.ParseResult;

public class Runner {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("You need to give an input file.");
			return;
		}
		
		assert args.length >= 1;
		String filename = args[0];
		
		StringBuilder fileBuilder = new StringBuilder();
		
		try(Scanner fileReader = new Scanner(new File(filename))) {
			while (fileReader.hasNextLine()) {
				fileBuilder.append(fileReader.nextLine());
				fileBuilder.append("\n");
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			return;
		}
		
		TokenLexResult<ArrowTokenType> lexResult = ArrowLexer.parse(fileBuilder.toString());		
		if (!lexResult.getSuccess()) {
			System.out.println(lexResult);
			return;
		}
		
		ParseResult<ArrowTokenType> parseResult = new ArrowProgramParser().parse(lexResult.getResults());
		if (!parseResult.getSuccess()) {
			System.out.println(parseResult);
			return;
		}
		
		ExecutionResult result = CompoundExecutor.of(new RuntimeDataStack()).execute(parseResult.getNode());
		System.out.println(result);
	}
}
