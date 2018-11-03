package chp;

import java.util.Comparator;

public abstract class Heuristic implements Comparator<Node> {
	public Heuristic(Node initialState) {
		// Heuristic preprocessing?

	}

	// The heuristic should be quick to calculate, as it is computed a lot of times
	public int h(Node n) {
		// unsolved goal count
		//int missingWords = n.finalWordCount - n.currentWordCount;
		//int goalBias = missingWords * 100;

		// int penalty = 0;
		// // punish long unsolved words heavily
		// for (WordSlot w : n.wordSlots) {
		// 	if (!w.isGoalState(n.guess, n.language)) {
		// 		penalty = penalty * w.length;
		// 	}
		// }

		// return (penalty);

		return (n.maxTileCount - n.currentFilledTileCount) * n.g();
	}
    
	public abstract int f(Node n);

	@Override
	public int compare(Node n1, Node n2) {
		return this.f(n1) - this.f(n2);
	}

	public static class AStar extends Heuristic {
		public AStar(Node initialState) {
			super(initialState);
		}

		@Override
		public int f(Node n) {
			return n.g() + this.h(n);
		}

		@Override
		public String toString() {
			return "A* evaluation";
		}
	}

	public static class WeightedAStar extends Heuristic {
		private int W;

		public WeightedAStar(Node initialState, int W) {
			super(initialState);
			this.W = W;
		}

		@Override
		public int f(Node n) {
			return n.g() + this.W * this.h(n);
		}

		@Override
		public String toString() {
			return String.format("WA*(%d) evaluation", this.W);
		}
	}

	public static class Greedy extends Heuristic {
		public Greedy(Node initialState) {
			super(initialState);
		}

		@Override
		public int f(Node n) {
			return this.h(n);
		}

		@Override
		public String toString() {
			return "Greedy evaluation";
		}
	}
}
