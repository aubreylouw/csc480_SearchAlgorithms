package alouw.csc480.search.interfaces;

import java.util.Comparator;

/*
 * Enumeration of evaluation functions
 */
public enum EvaluationFunction {
	NONE {

		@Override
		public Comparator<TreeNode> getComparator(final ProblemState goalState) {
			return transformOperatorComparator;
		}
	}, CUME_COST {
		
		@Override
		public Comparator<TreeNode> getComparator(final ProblemState goalState) {
			return gOfnComparator;
		}
	}, GREEDY {
		@Override
		public Comparator<TreeNode> getComparator(final ProblemState goalState) {
			return new Comparator<TreeNode>() {
				@Override
				public int compare(TreeNode o1, TreeNode o2) {
					Integer h1 = Integer.valueOf(o1.getEvaluationPathCost(GREEDY, goalState));
					Integer h2 = Integer.valueOf(o2.getEvaluationPathCost(GREEDY, goalState));
					int result = h1.compareTo(h2);
					result = (result == 0 && o1.equals(o2) == false) ? 1 : result;
					return result;
				}
			};
		}
	}, ASTAR_1 {
		@Override
		public Comparator<TreeNode> getComparator(final ProblemState goalState) {
			return new Comparator<TreeNode>() {
				@Override
				public int compare(TreeNode o1, TreeNode o2) {
					Integer h1 = Integer.valueOf(o1.getEvaluationPathCost(ASTAR_1, goalState));
					Integer h2 = Integer.valueOf(o2.getEvaluationPathCost(ASTAR_1, goalState));
					int result = h1.compareTo(h2);
					result = (result == 0 && o1.equals(o2) == false) ? 1 : result;
					return result;
				}
			};
		}
	}, ASTAR_2 {

		@Override
		public Comparator<TreeNode> getComparator(final ProblemState goalState) {
			return new Comparator<TreeNode>() {
				@Override
				public int compare(TreeNode o1, TreeNode o2) {
					Integer h1 = Integer.valueOf(o1.getEvaluationPathCost(ASTAR_2, goalState));
					Integer h2 = Integer.valueOf(o2.getEvaluationPathCost(ASTAR_2, goalState));
					int result = h1.compareTo(h2);
					result = (result == 0 && o1.equals(o2) == false) ? 1 : result;
					return result;
				}
			};
		}
	}, ASTAR_3 {
		@Override
		public Comparator<TreeNode> getComparator(final ProblemState goalState) {
			return new Comparator<TreeNode>() {
				@Override
				public int compare(TreeNode o1, TreeNode o2) {
					Integer h1 = Integer.valueOf(o1.getEvaluationPathCost(ASTAR_3, goalState));
					Integer h2 = Integer.valueOf(o2.getEvaluationPathCost(ASTAR_3, goalState));
					int result = h1.compareTo(h2);
					result = (result == 0 && o1.equals(o2) == false) ? 1 : result;
					return result;
				}
			};
		}
	};
	
	public abstract Comparator<TreeNode> getComparator(final ProblemState goalState);
	
	private static Comparator<TreeNode> transformOperatorComparator = new Comparator<TreeNode>() {
		@Override
		public int compare(TreeNode o1, TreeNode o2) {
			return o1.getProblemState().getPredecessorTransformation().compareTo(o2.getProblemState().getPredecessorTransformation());
		}
	};
	
	private static Comparator<TreeNode> gOfnComparator = new Comparator<TreeNode>() {
		@Override
		public int compare(TreeNode o1, TreeNode o2) {
			return o1.getProblemState().compareTo(o2.getProblemState());
		}
	};
}