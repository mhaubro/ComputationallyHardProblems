package chp;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chp.WordSlot.*;

public class Node {
	// These values are the same for every instance of Node

	private static final Random RND = new Random(1);
	//private static final Random RND = new Random(2);
	//private static final Random RND = new Random(3);

	// Every node has the same max dimensions, goal and wall locations
	public static int max_row;
	public static int max_col;
	public static boolean[][] walls;
	public static int maxTileCount;

	// Two-dimensional arrays containing a value for every tile. The value represents how long a word must be to fit into this tile
	public static int[][] horizontalWordLength;
	public static int[][] verticalWordLength;

	public static WordSlot[] wordSlots;

	public static int finalWordCount;

	// A set containing the accepted symbols
	public static Set<String> alphabet = new HashSet<>();

	// A list of every accepted word
	public static String[] language; 
	public static String languageAsOne;
	// Could saved in a better (sorted) datastructure with faster searches

	// These values are specific to each instance of Node

	// Contains "#" for walls, "_" for yet-to-be-guessed titles, and letters from the alphabet in all other cases
	public char[][] guess;
	public int currentWordCount;
	public int currentFilledTileCount;

	public Node parent;
	private int g;
	private int _hash = 0;

	// Constructor - you can only construct a node after you have defined the initial values
	public Node(Node parent) {
		this.parent = parent;

		if (parent == null) {
			this.g = 0;
		} else {
			this.g = parent.g() + 1;
		}
	}
	
	// Defines some static initial values
	public static void initValues(int row, int col) {
        max_row = row;
        max_col = col;
        //walls = new boolean[max_row][max_col];
        horizontalWordLength = new int[max_row][max_col];
        verticalWordLength   = new int[max_row][max_col];

        preprocess();
	}

	// Defines values for finalWordCount, horizontalWordLength[][] and verticalWordLength[][]
	public static void preprocess() {
		ArrayList<WordSlot> tempWordSlots = new ArrayList<WordSlot>();

		// Iterate through every tile
		for (int row = 0; row < max_row; row++) {
			for (int col = 0; col < max_col; col++) {
				// If you find a tile that doesn't contain a wall, and that has not already been assigned a value by this method
				if (!walls[row][col] && horizontalWordLength[row][col] == 0) {
					System.err.println("Horizontal word:");

					int currentWordLength = 1;
					while (col+currentWordLength < max_col && !walls[row][col+currentWordLength]) {
						currentWordLength++;
					}

					tempWordSlots.add(new WordSlot(row, col, currentWordLength, "horizontal", finalWordCount));
					finalWordCount++;

					System.err.println(currentWordLength);

					// At this point the end of the word has been found, and currentWordLength has the correct value
					// Now we need to assign this value to every corresponding tile
					for (int i = 0; i<currentWordLength; i++) {
						horizontalWordLength[row][col+i] = currentWordLength;
					}
				}
				// If you find a tile that doesn't contain a wall, and that has not already been assigned a value by this method
				if (!walls[row][col] && verticalWordLength[row][col] == 0) {
					System.err.println("vertical word:");


					int currentWordLength = 1;
					while (row+currentWordLength < max_row && !walls[row+currentWordLength][col]) {
						currentWordLength++;
					}

					tempWordSlots.add(new WordSlot(row, col, currentWordLength, "vertical", finalWordCount));
					finalWordCount++;

					// At this point the end of the word has been found, and currentWordLength has the correct value
					// Now we need to assign this value to every corresponding tile
					for (int i = 0; i<currentWordLength; i++) {
						verticalWordLength[row+i][col] = currentWordLength;
					}
				}
			}
		}

		wordSlots = new WordSlot[tempWordSlots.size()];
		wordSlots = tempWordSlots.toArray(wordSlots);
	}

	public int g() {
		return this.g;
	}

	public boolean isInitialState() {
		return this.parent == null;
	}

	public boolean isGoalState() {
		// for (WordSlot w : wordSlots){
		// 	if (!w.isGoalState(guess, language))
		// 		return false;
		// }
		// return true;
		return (currentFilledTileCount == maxTileCount);
//		return (currentWordCount == finalWordCount);
	}

//Checks if this state can ever become legal
	boolean isLegalState(){
		String s;
		for (WordSlot w : wordSlots){
			s = w.getCurrentWord(guess);
			s = s.replaceAll("_", ".");
			//System.out.println(s);
			boolean foundMatch = false;
			for (String word : language){
				if (word.matches("^"+s+"$")){
					foundMatch = true;
					break;
				}
			}
			if (!foundMatch){
				return false;
			}
		}
		return true;
	}

	// The expanded nodes are generated by generating a leaf node for every state of the board that can be creating by filling in one more word
	public ArrayList<Node> getExpandedNodes() {

		//System.err.println("Current Node:");
        //System.err.println(this);

		int remainingWordCount = (finalWordCount-currentWordCount);
		ArrayList<Node> expandedNodes = new ArrayList<Node>();

		for (WordSlot wordSlot : wordSlots) {
			String regexString = "";
			//System.err.println("Checking wordslot length: " + wordSlot.length);

			if (wordSlot.axis == "horizontal") {
				for (int col = wordSlot.origincol; col<wordSlot.origincol+wordSlot.length; col++) {
					if (guess[wordSlot.originrow][col] == '_') {
						regexString = regexString + '.';
					} else {
						regexString = regexString + guess[wordSlot.originrow][col];
					}
				}
			}

			else {//Vertical
				for (int row = wordSlot.originrow; row<wordSlot.originrow+wordSlot.length; row++) {
					if (guess[row][wordSlot.origincol] == '_') {
						regexString = regexString + '.';
					} else {
						regexString = regexString + guess[row][wordSlot.origincol];
					}
				}
			}
			
			// count the number of unfilled letters this word would fill
			int unfilledLetters = regexString.length() - regexString.replace(".", "").length();
			if (unfilledLetters == 0) {
				// The wordSlot has already been fully filled by a word
				// No need to do any expansions
			} else {
				Pattern p = Pattern.compile("^("+regexString+")$", Pattern.MULTILINE);
				Matcher m = p.matcher(languageAsOne);

				while (m.find()){
					// Just search once
					for (int matchIndex = 0; matchIndex < m.groupCount(); matchIndex++) {
						String word = m.group(matchIndex);
						// System.err.println("'"+word+"' is match for wordSlot "+wordSlot.id+"'s' '"+regexString+"'");
						// word is a valid match for wordSlot and should be considered target for expansion
						Node newNode = this.ChildNode();
						newNode.currentFilledTileCount = currentFilledTileCount + unfilledLetters;
						if (wordSlot.axis == "horizontal") {
							for (int i = 0; i<wordSlot.length; i++) {
								newNode.guess[wordSlot.originrow][wordSlot.origincol+i] = (word.charAt(i));
							}
						}
						else {
							for (int i = 0; i<wordSlot.length; i++) {
								newNode.guess[wordSlot.originrow+i][wordSlot.origincol] = (word.charAt(i));
							}
						}
									//It will never be possible to make it on from this state
						if (newNode.isLegalState()){
							expandedNodes.add(newNode);
						}
					}
				}
			}
		}

		// for (int row = 0; row < max_row; row++) {
		// 	for (int col = 0; col < max_col; col++) {
		// 		if (guess[row][col] == "_".charAt(0)) {
		// 			//System.err.println("Tile at "+row+","+col+" is _");
		// 			//System.err.println(alphabet);
		// 			for (String letter : alphabet) {
		// 				//System.err.println("Assigned "+letter+" to tile");
		// 				Node newNode = this.ChildNode();
		// 				newNode.guess[row][col] = letter.charAt(0);
		// 				newNode.currentFilledTileCount = currentFilledTileCount+1;
		// 				//System.err.println(newNode);
		// 				expandedNodes.add(newNode);
		// 			}
		// 		}
		// 	}
		// }




				// boolean foundMatchForSlot = false;
				// for (String word : language) {
				// 	if (word.matches("^"+regexString+"$")) {
				// 		foundMatchForSlot = true;
				// 		//System.err.println("Matching word!");
				// 		// word is a valid match for wordSlot and should be considered target for expansion
				// 		Node newNode = this.ChildNode();
				// 		newNode.currentFilledTileCount = currentFilledTileCount + unfilledLetters;
				// 		if (wordSlot.axis == "horizontal") {
				// 			for (int i = 0; i<wordSlot.length; i++) {
				// 				newNode.guess[wordSlot.originrow][wordSlot.origincol+i] = (word.charAt(i));
				// 			}
				// 		}
				// 		if (wordSlot.axis == "vertical") {
				// 			for (int i = 0; i<wordSlot.length; i++) {
				// 				newNode.guess[wordSlot.originrow+i][wordSlot.origincol] = (word.charAt(i));
				// 			}
				// 		}
				// 		expandedNodes.add(newNode);
				// 	}
				// }

				// if (!foundMatchForSlot) {
				// 	// Tried every word for current wordslot with no matches - current guess can never be part of the solution
				// 	//return null;
				// }
			
			// check whether wordSlot has been filled
			// yes -> skip it
			// no  -> compose regular expression based on the wordSlot and current guess values
			//        find matches in language based on regular expression
			//        add every possible match to expandedNodes

		Collections.shuffle(expandedNodes, RND);
		return expandedNodes;
	}


	// A child node contains some of the information from the current node. We copy the information efficiently here
	private Node ChildNode() {
		Node copy = new Node(this);
		// Efficient copying of 2d-matrix
		// for (int row = 0; row < max_row; row++) {
		// 	System.arraycopy(this.guess[row], '\u0000', copy.guess[row], '\u0000', max_col);
		// }
		copy.guess = new char[guess[0].length][guess[0].length];
		for (int i = 0; i < guess[0].length; i++){
			for (int j = 0; j < guess[0].length; j++){
				copy.guess[i][j] = guess[i][j];
			}
		}
//		copy.guess = guess;
		copy.currentWordCount = currentWordCount + 1;
		return copy;
	}

	@Override
	public int hashCode() {
		if (this._hash == 0) {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.deepHashCode(guess);
			result = prime * result + Arrays.deepHashCode(walls);
			this._hash = result;
		}
		return this._hash;
	}

	// @Override
	// public boolean equals(Object obj) {
	// 	if (this == obj)
	// 		return true;
	// 	if (obj == null)
	// 		return false;
	// 	if (this.getClass() != obj.getClass())
	// 		return false;
	// 	Node other = (Node) obj;
	// 	if (this.agent.locRow != other.agent.locRow || this.agent.locCol != other.agent.locCol)
	// 		return false;
	// 	if (!Arrays.deepEquals(this.boxesOnMap, other.boxesOnMap))
	// 		return false;
	// 	return true;
	// }

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int row = 0; row < max_row; row++) {
			for (int col = 0; col < max_col-1; col++) {
				s.append(guess[row][col]);
				s.append(';');
			}
			s.append(guess[row][max_col-1]);
			s.append("\n");
		}
		return s.toString();
	}

}