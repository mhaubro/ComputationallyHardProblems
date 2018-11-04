package chp;

public class WordSlot {
	public int originrow;
	public int origincol;
	public int length;
	public String axis;
	public int id;

	public WordSlot(int originrow, int origincol, int length, String axis, int id) {
		this.originrow = originrow;
		this.origincol = origincol;
		this.length = length;
		this.axis = axis;
		this.id = id;
	}

	String getCurrentWord(char[][] world){
		String word = "";
		if (axis == "horizontal"){
			for (int i = 0; i < length; i++){
				word = word + world[originrow][origincol + i];
			}			
		} else {
			for (int i = 0; i < length; i++){
				word = word + world[originrow + i][origincol];
			}						
		}
		return word;		
	}

	boolean isGoalState(char[][] world, String[] language){
		String word = "";
		if (axis == "horizontal"){
			for (int i = 0; i < length; i++){
				word = word + world[originrow][origincol + i];
			}			
		} else {
			for (int i = 0; i < length; i++){
				word = word + world[originrow + i][origincol];
			}						
		}

		for (String s : language){
			if (s.equals(word)){
				return true;
			}
		}
		return false;
	}


}