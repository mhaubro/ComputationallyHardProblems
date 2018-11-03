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
}