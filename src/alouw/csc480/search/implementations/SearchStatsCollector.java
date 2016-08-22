package alouw.csc480.search.implementations;

import alouw.csc480.search.interfaces.EvaluationFunction;
import alouw.csc480.search.interfaces.ProblemState;
import alouw.csc480.search.interfaces.SearchFunction;

public class SearchStatsCollector {
	private final ProblemState initialState;
	private final ProblemState goalState;
	private final SearchFunction searchType;
	private final EvaluationFunction evalFunction;
	private long lengthSolutionPath;
	private long costSolutionPath;
	private long numberOfNodesExplored;
	private long maximumSizeOfQueue;
	
	public SearchStatsCollector(final ProblemState initialState, final ProblemState goalState, 
			final SearchFunction searchType, final EvaluationFunction evalFunction) {
		this.initialState = initialState;
		this.goalState = goalState;
		this.searchType = searchType;
		this.evalFunction = evalFunction;
	}
	
	public void incrementSolutionPath(long incr) {this.lengthSolutionPath += incr;}
	public void incrementSolutionCost(long incr) {this.costSolutionPath += incr;}
	public void incrementNodesExplored(long incr) {this.numberOfNodesExplored += incr;}
	public void setMaxQueueSize(long maxQueueSize) {this.maximumSizeOfQueue = Math.max(this.maximumSizeOfQueue, maxQueueSize);}
	
	public String toString() {
		StringBuilder stringValue = new StringBuilder();
		stringValue.append("Initial state: ").append(initialState.toString()).append(" ---> Goal state: ").append(goalState.toString());
		stringValue.append("\n");
		stringValue.append(searchType).append(" using f(n)= ").append(evalFunction).append(" :: ");
		stringValue.append("Length = ").append(lengthSolutionPath).append(" ; ");
		stringValue.append("Cost   = ").append(costSolutionPath).append(" ; ");
		stringValue.append("Time   = ").append(numberOfNodesExplored).append(" ; ");
		stringValue.append("Space  = ").append(maximumSizeOfQueue);
		
		return stringValue.toString();
	}
}