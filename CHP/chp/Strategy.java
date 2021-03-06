package chp;


import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.PriorityQueue;

public abstract class Strategy {
	private HashSet<Node> explored;
	private final long startTime;

	public Strategy() {
		this.explored = new HashSet<Node>();
		this.startTime = System.currentTimeMillis();
	}

	public void addToExplored(Node n) {
		this.explored.add(n);
	}

	public boolean isExplored(Node n) {
		return this.explored.contains(n);
	}

	public int countExplored() {
		return this.explored.size();
	}

	public String searchStatus() {
		return String.format("#Explored: %,6d, #Frontier: %,6d, #Generated: %,6d, Time: %3.2f s", this.countExplored(), this.countFrontier(), this.countExplored()+this.countFrontier(), this.timeSpent());
	}

	public float timeSpent() {
		return (System.currentTimeMillis() - this.startTime) / 1000f;
	}

	public abstract Node getAndRemoveLeaf();

	public abstract void addToFrontier(Node n);

	public abstract boolean inFrontier(Node n);

	public abstract int countFrontier();

	public abstract boolean frontierIsEmpty();

	@Override
	public abstract String toString();

	public static class StrategyBestFirst extends Strategy {
		private Heuristic heuristic;
		private PriorityQueue<Node> frontier;
		private HashSet<Node> frontierSet;

		public StrategyBestFirst(Heuristic h) {
			super();
			this.heuristic = h;
			frontier = new PriorityQueue<Node>(heuristic);
			frontierSet = new HashSet<Node>();
		}

		@Override
		public Node getAndRemoveLeaf() {
			Node n = frontier.poll();
			frontierSet.remove(n);
			return n;
		}

		@Override
		public void addToFrontier(Node n) {
			frontier.add(n);
			frontierSet.add(n);
		}

		@Override
		public int countFrontier() {
			return frontier.size();
		}

		@Override
		public boolean frontierIsEmpty() {
			return frontier.isEmpty();
		}

		@Override
		public boolean inFrontier(Node n) {
			return frontierSet.contains(n);
		}

		@Override
		public String toString() {
			return "Best-first Search using " + this.heuristic.toString();
		}
	}
}
