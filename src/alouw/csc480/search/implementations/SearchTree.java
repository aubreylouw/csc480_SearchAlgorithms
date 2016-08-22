package alouw.csc480.search.implementations;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import alouw.csc480.search.interfaces.EvaluationFunction;
import alouw.csc480.search.interfaces.ProblemState;
import alouw.csc480.search.interfaces.SearchFunction;
import alouw.csc480.search.interfaces.TreeNode;

/*
 * The main driver class for navigating a set of tree nodes. 
 * 
 */
public class SearchTree {
	
	// queue to be traversed when searching for a solution
	private final ArrayDeque<TreeNode> searchTree = new ArrayDeque<>();
	
	// user supplied arguments to construct a SearchTree
	private final SearchFunction searchFunction;
	private final EvaluationFunction evalFunction;
	private final ProblemState goalState;
	
	// the root node anchoring the searchTree
	private final TreeNode rootNode;
	
	// a node with a problem state equal to the goal state discovered by traversing the searchTree
	// null is a possible value
	private TreeNode solutionNode;
	private boolean solutionFound = false;
	
	//collector of statistics about search performance
	private final SearchStatsCollector statsCollector;

	SearchTree(final SearchFunction searchFunction, final EvaluationFunction evalFunction, 
			final ProblemState initialState, final ProblemState goalState) {
		this.searchFunction = searchFunction;
		this.evalFunction = evalFunction;
		this.goalState = goalState;
		
		this.rootNode = TreeNodeFactory.getNewRootNode(initialState);
		this.searchTree.add(this.rootNode);
		
		this.statsCollector = new SearchStatsCollector(initialState, goalState, searchFunction, evalFunction);
	}
	
	public boolean search() {
			
		this.solutionFound = false;
		Optional<TreeNode> solutionNode = this.searchFunction.apply(searchTree, evalFunction, 
				goalState, statsCollector);
		
		if (solutionNode.isPresent()) {
			this.solutionNode = solutionNode.get();
			this.solutionFound = true;
		}
				
		return this.solutionFound;
	}
	
	public Optional<Deque<TreeNode>> getSolution() {
		Deque<TreeNode> solutionPath = new ArrayDeque<>();
		
		Optional<Deque<TreeNode>> result = Optional.of(solutionPath);
		
		if (this.solutionFound) {
			TreeNode node = this.solutionNode;
			assert(node != null);
				
			do {
				solutionPath.push(node);
				node = node.getParentNode();
			} while (node != null && !node.equals(solutionPath.peek()));
		} else solutionPath = null;
		
		return result;
	}
	
	public void printStats() {
		System.out.println(this.statsCollector.toString());
	}
}