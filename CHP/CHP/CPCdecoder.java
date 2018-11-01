// Første linje skal opfylde:
// ^(\d+);(\d+);(\d)$

// X: Første gruppe er antal symboler i vores alfabet
// Y: Anden gruppe er antal acceptable strenge
// Z: Tredje gruppe er kortets sidelængde

// Anden linje skal opfylde:
// ^[\\u0020-\\u0022|\\u0024-\\u003A|\\u003C-\\u005E|\\u0060-\\u007E|\\u00A0-\\u00FF](;[\\u0020-\\u0022|\\u0024-\\u003A|\\u003C-\\u005E|\\u0060-\\u007E|\\u00A0-\\u00FF]){X-1}$

// Alle matrice-linjer skal opfylde:
// ^(#|_)(;(#|_)){Z-1}$

// Alle strenge skal opfylde:
// ^([\\u0020-\\u0022|\\u0024-\\u003A|\\u003C-\\u005E|\\u0060-\\u007E|\\u00A0-\\u00FF])+$

import java.util.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPCdecoder {
	public static void main(String args[]){
		String fileName = "test.cbc";

		String input = "";

		try {
			byte[] buf = Files.readAllBytes(Paths.get(fileName));
			input = new String(buf,"UTF-8");

			System.err.println("Succesfully opened the file '"+fileName+"'. Here is the map:");
			System.err.println(input);
			System.err.println();

			String[] line = input.split("\\r?\\n");

			// Matches the syntax of the variable declaration line
			try {line[0].length();} catch(ArrayIndexOutOfBoundsException exception) {
			    System.err.println("Line 0 does not exist!");
				System.out.println("NO");
				
				
			}

			if (!line[0].matches("^\\d+;\\d+;\\d$")) {
				System.err.println("Line 0 contains invalid syntax!");
				System.out.println("NO");
				System.exit(1);
				
			}

			String[] header = line[0].split(";");

			int declaredSymbolCount           = Integer.parseInt(header[0]);
			int declaredAcceptableStringCount = Integer.parseInt(header[1]);
			int declaredMatrixWidth           = Integer.parseInt(header[2]);

			System.err.println("Line 0 passed syntax check.");
			System.err.println("Parsed following information:");
			System.err.println("declaredSymbolCount: "+declaredSymbolCount);
			System.err.println("declaredAcceptableStringCount: "+declaredAcceptableStringCount);
			System.err.println("declaredMatrixWidth: "+declaredMatrixWidth);
			System.err.println();


			try {line[1].length();} catch(ArrayIndexOutOfBoundsException exception) {
			    System.err.println("Line 1 does not exist!");
				System.out.println("NO");
				System.exit(1);
				
			}

			// Matches the syntax of the alphabet declaration line
			if (!line[1].matches("^[\\u0020-\\u0022|\\u0024-\\u003A|\\u003C-\\u005E|\\u0060-\\u007E|\\u00A0-\\u00FF](;[\\u0020-\\u0022|\\u0024-\\u003A|\\u003C-\\u005E|\\u0060-\\u007E|\\u00A0-\\u00FF]){"+(declaredSymbolCount-1)+"}$")) {
				System.err.println("Line 1 contains invalid syntax!");
				System.out.println("NO");
				System.exit(1);
				
			}

			System.err.println("Line 1 passed syntax check.");

			Set<String> alphabet = new HashSet<>();
			String[] alphabetList = line[1].split(";");
			for (String letter : alphabetList) {
				alphabet.add(letter);
			}
			System.err.println("Parsed following alphabet: "+alphabet);
			System.err.println();

			// Matches the syntax of the matrix
			for (int i=0; i<declaredMatrixWidth; i++) {

				try {line[2+i].length();} catch(ArrayIndexOutOfBoundsException exception) {
				    System.err.println("Line "+(2+i)+" does not exist!");
					System.out.println("NO");
					System.exit(1);
					
				}

				// Matches the syntax of a single row in the matrix
				if(!line[2+i].matches("^(#|_)(;(#|_)){"+(declaredMatrixWidth-1)+"}$")) {
					System.err.println("Line "+(2+i)+" contains invalid syntax!");
					System.out.println("NO");
					System.exit(1);
					
				}
			}

			System.err.println("Line 2-"+(1+declaredMatrixWidth)+" conforms to the syntax of the board");
			System.err.println();

			// Count every declared variable
			int acceptableStringCounter = 0;
			
			int wordsStartLine = 2+declaredMatrixWidth;
			int wordsEndLine   = 2+declaredMatrixWidth+declaredAcceptableStringCount-1;
			for (int i=wordsStartLine; i<=wordsEndLine; i++) {

				try {line[i].length();} catch(ArrayIndexOutOfBoundsException exception) {
				    System.err.println("Line "+i+" does not exist!");
					System.out.println("NO");
					System.exit(1);
					
				}

				System.err.println("Line "+i+" is getting parsed...");
				// Should check that a word is composed of only symbols from the declared alphabet
				String[] letters = line[i].split("");
				for (String letter : letters) {
					if(!alphabet.contains(letter)) {
						System.err.println("Line "+i+" contains the illegal symbol "+letter);
						System.out.println("NO");
						System.exit(1);
						
					}
				}
				acceptableStringCounter++;
			}
			System.err.println();


			System.err.println("Line "+wordsStartLine+"-"+wordsEndLine+" are words containing only letters of the declared alphabet");
			System.err.println();

			if (acceptableStringCounter != declaredAcceptableStringCount) {
				System.err.println("Mismatch between expected amount of acceptable strings and actual strings.");
				System.err.println("Expected "+declaredAcceptableStringCount+" strings, read "+acceptableStringCounter+" strings.");
				System.out.println("NO");
				System.exit(1);
							
			}

			if (line.length-1 != wordsEndLine) {
				System.err.println("Mismatch between expected amount of acceptable strings and actual strings.");
				System.err.println("File contains more words than declared.");
				System.out.println("NO");
				System.exit(1);
						
			}

			System.err.println("The amount of words supplied match the header declaration");
			System.err.println();

			System.err.println("File completely parsed - no issues found. This file is VALID.");
			System.out.println("YES");
			System.exit(1);


		} catch (IOException e) {
			System.err.println("Error in reading file.");
			System.exit(0);
		}



	}
}
