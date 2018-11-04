package chp;

import java.util.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import chp.Node.*;
import chp.Strategy.*;
import chp.Heuristic.*;
import chp.WordSlot.*;

public class CPC {

	public static Set<String> alphabet = new HashSet<>();
	public static String[] language;
	public static String languageAsOne;
	public static boolean walls[][];
	public static char[][] initialMap;
	public static int maxTileCount;


	public static void main(String args[]){
		if (!decoder()) {
			System.err.println("System input is invalid.");
			System.out.println("NO");
			System.exit(0);
		} else {
			Node initial = new Node(null);
			initial.walls = walls;
			initial.language = language;
			initial.languageAsOne = languageAsOne;
			initial.initValues(walls.length, walls.length);
			initial.currentFilledTileCount = 0;
			initial.maxTileCount = maxTileCount;
			initial.guess = initialMap;


			Strategy strategy = new StrategyBestFirst(new AStar(initial));
			Node solution = Search(strategy, initial);

			if (solution == null) {
				System.err.println("Impossible to find solution.");
				System.out.println("NO");
				System.exit(0);
			} else {
				System.err.println("Found this solution:");
				//System.out.println("YES");
				System.out.print(solution);
				System.exit(0);
			}
		}
	}

	public static Boolean decoder() {
		InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(in);
//      String fileName = "test.cbc";
//		String input = "";

		List<String> all_lines = new ArrayList<String>();

		try {
			String currentLine = br.readLine();

			while (currentLine != null || currentLine == "") {
				all_lines.add(currentLine);	            
				currentLine = br.readLine();
			}

			String[] line = all_lines.toArray(new String[0]);

			// byte[] buf = Files.readAllBytes(Paths.get(fileName));
			// input = new String(buf,"UTF-8");

			// System.err.println("Succesfully opened the file '"+fileName+"'. Here is the map:");
			// System.err.println(input);
			// System.err.println();

			// String[] line = input.split("\\r?\\n");


			// Matches the syntax of the variable declaration line
			try {line[0].length();} catch(ArrayIndexOutOfBoundsException exception) {
			    System.err.println("Line 0 does not exist!");
				return false;
			}

			if (!line[0].matches("^\\d+;\\d+;\\d$")) {
				System.err.println("Line 0 contains invalid syntax!");
				return false;
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

			if (declaredSymbolCount < 1 || declaredAcceptableStringCount < 1 || declaredMatrixWidth < 1) {
				System.err.println("Values declared in line 0 do not make sense");
				System.out.println("NO");
				System.exit(0);
			}


			try {line[1].length();} catch(ArrayIndexOutOfBoundsException exception) {
			    System.err.println("Line 1 does not exist!");
				return false;
			}

			// Matches the syntax of the alphabet declaration line
			if (!line[1].matches("^[\\u0020-\\u0022|\\u0024-\\u003A|\\u003C-\\u005E|\\u0060-\\u007E|\\u00A0-\\u00FF](;[\\u0020-\\u0022|\\u0024-\\u003A|\\u003C-\\u005E|\\u0060-\\u007E|\\u00A0-\\u00FF]){"+(declaredSymbolCount-1)+"}$")) {
				System.err.println("Line 1 contains invalid syntax!");
				return false;
			}

			System.err.println("Line 1 passed syntax check.");

			String[] declaredAlphabetList = line[1].split(";");
			for (String letter : declaredAlphabetList) {
				alphabet.add(letter);
			}
			System.err.println("Parsed following alphabet: "+alphabet);
			System.err.println();

			walls = new boolean[declaredMatrixWidth][declaredMatrixWidth];
			initialMap = new char[declaredMatrixWidth][declaredMatrixWidth];
			// Matches the syntax of the matrix
			for (int i=0; i<declaredMatrixWidth; i++) {

				try {line[2+i].length();} catch(ArrayIndexOutOfBoundsException exception) {
				    System.err.println("Line "+(2+i)+" does not exist!");
					return false;
				}

				// Matches the syntax of a single row in the matrix
				if(!line[2+i].matches("^(#|_)(;(#|_)){"+(declaredMatrixWidth-1)+"}$")) {
					System.err.println("Line "+(2+i)+" contains invalid syntax!");
					return false;
				}

				// Save map into data structure
				String[] lineContent = line[2+i].split(";");
				for (int j=0; j<lineContent.length; j++) {
					if (lineContent[j].charAt(0) == '#') {
						// Check if row/col is correct
						walls[i][j] = true;
						initialMap[i][j] = '#';
					} else if (lineContent[j].equals("_")) {
						maxTileCount++;
						initialMap[i][j] = '_';
						walls[i][j] = false;//Might be unnecessary
					}
				}
			}

			System.err.println("Line 2-"+(1+declaredMatrixWidth)+" conforms to the syntax of the board: ");
			System.err.println();

			// Count every declared variable
			ArrayList<String> tempLanguage = new ArrayList<String>();
			
			int wordsStartLine = 2+declaredMatrixWidth;
			int wordsEndLine   = 2+declaredMatrixWidth+declaredAcceptableStringCount-1;
			for (int i=wordsStartLine; i<=wordsEndLine; i++) {

				try {line[i].length();} catch(ArrayIndexOutOfBoundsException exception) {
				    System.err.println("Line "+i+" does not exist!");
					return false;
				}

				System.err.println("Line "+i+" is getting parsed...");
				// Should check that a word is composed of only symbols from the declared alphabet
				String[] letters = line[i].split("");
				for (String letter : letters) {
					if(!alphabet.contains(letter)) {
						System.err.println("Line "+i+" contains the illegal symbol '"+letter+"'");
						return false;
					}
				}
				tempLanguage.add(line[i]);
			}
			System.err.println();


			System.err.println("Line "+wordsStartLine+"-"+wordsEndLine+" are words containing only letters of the declared alphabet");
			System.err.println();

			language = new String[tempLanguage.size()];
			language = tempLanguage.toArray(language);
			languageAsOne = String.join("\n", language);

			if (language.length != declaredAcceptableStringCount) {
				System.err.println("Mismatch between expected amount of acceptable strings and actual strings.");
				System.err.println("Expected "+declaredAcceptableStringCount+" strings, read "+language.length+" strings.");
				return false;
							
			}

			if (line.length-1 != wordsEndLine) {
				System.err.println("Mismatch between expected amount of acceptable strings and actual strings.");
				System.err.println("File contains more words than declared.");
				return false;
						
			}

			System.err.println("The amount of words supplied match the header declaration");
			System.err.println();

			System.err.println("File completely parsed - no issues found. This file is VALID.");
			return true;


		} catch (IOException e) {
			System.err.println("Error in reading file.");
			return false;
		}
	}

	// The search method takes a start node and expands it by applying a search strategy
	// It returns a total, valid, boardconfiguration (guess)
	public static Node Search(Strategy strategy, Node startNode) {
		System.err.println("Search starting with strategy "+strategy.toString()+".");
		strategy.addToFrontier(startNode);

		int iterations = 0;
		while (true) {
            if ((iterations % 100) == 0) {
				System.err.println(strategy.searchStatus());
			}

			if (strategy.frontierIsEmpty()) {
				System.err.println("Explored every combination of words for every word slot without finding a valid combination");
				return null;
			}

			Node leafNode = strategy.getAndRemoveLeaf();

			//Running this before checking legality will make sure we will only check legality of a node once.
			strategy.addToExplored(leafNode);

			if (!leafNode.isLegalState()){
				continue;
			}

			// This state has at least one word slot that can never be satisfied, and can therefor never be part of the real solution
			if (leafNode.isGoalState()) {
				return leafNode;
			}

			if (leafNode == null){
				System.err.println("This is bad");
			}
			// Somehow deal with early termination here
			for (Node n : leafNode.getExpandedNodes()) { // The list of expanded nodes is shuffled randomly; see Node.java.
				if (!strategy.isExplored(n) && !strategy.inFrontier(n)) {
					strategy.addToFrontier(n);
				}
			}
			iterations++;
		}
	}
}
