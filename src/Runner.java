import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import arrow.ArrowLexer;
import arrow.ArrowTokenType;
import arrow.parser.ArrowProgramParser;
import executor.CompoundExecutor;
import lexer.TokenLexResult;
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
		System.out.println("Lexer result:");
		System.out.println(lexResult);
		if (!lexResult.getSuccess()) {
			return;
		}
		
		System.out.println("Parser result:");
		ParseResult<ArrowTokenType> parseResult = new ArrowProgramParser().parse(lexResult.getResults());
		System.out.println(parseResult);
		if (!parseResult.getSuccess()) {
			return;
		}
		
		System.out.println("Executor result:");
		new CompoundExecutor().execute(parseResult.getNode());
	}
}
