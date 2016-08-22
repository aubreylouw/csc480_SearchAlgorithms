package alouw.csc480.search.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import alouw.csc480.search.interfaces.EvaluationFunction;
import alouw.csc480.search.interfaces.ProblemState;
import alouw.csc480.search.interfaces.TreeNode;

public final class TreeNodeFactory {
	
	public static TreeNode getNewRootNode(ProblemState problemState) {
		return new TreeNodeImpl(problemState);
	}
	
	public static TreeNode getNewTreeNode(TreeNode parentNode, ProblemState problemState) {
		return new TreeNodeImpl(parentNode, problemState);
	}
}

class TreeNodeImpl implements TreeNode {

	private final boolean isRootNode;
	private final ProblemState problemState;
	private final TreeNode parentNode;
	private final int nodeDepth;
	
	// state set during comparisons; bad class design :(
	private EvaluationFunction evalFunction;
	private ProblemState goalState;
	
	TreeNodeImpl(ProblemState state) {
		if (state == null) throw new IllegalArgumentException("ProblemState cannot be <null>");
		
		this.isRootNode = true;
		this.parentNode = this;
		this.problemState = state;
		this.nodeDepth = 0;
	}
	
	TreeNodeImpl(TreeNode parentNode, ProblemState state) {
		if (parentNode== null) throw new IllegalArgumentException("ParentNode cannot be <null>");
		if (state == null) throw new IllegalArgumentException("ProblemState cannot be <null>");
		
		this.isRootNode = false;
		this.parentNode = parentNode;
		this.problemState = state;
		this.nodeDepth = 1 + this.parentNode.getDepth();
	}
	
	@Override
	public List<TreeNode> getChildrenNodes() {
		List<TreeNode> result = new ArrayList<>();
		
		this.problemState.getAllValidSucessorStates().
			stream().forEach(x -> result.add(TreeNodeFactory.getNewTreeNode(this, x)));
	
		return result;
	}

	@Override
	public TreeNode getParentNode() {
		return this.parentNode;
	}

	@Override
	public Boolean isRootNode() {
		return this.isRootNode;
	}

	@Override
	public ProblemState getProblemState() {
		return this.problemState;
	}

	@Override
	public int getDepth() {
		return this.nodeDepth;
	}

	@Override
	public int getEvaluationPathCost(EvaluationFunction h, ProblemState goalState) {
		this.evalFunction = h;
		this.goalState = goalState;
		return this.problemState.getExpectedTransformationCostToGoalState(h, goalState);
	}

	public String toString() {
		StringBuilder resultString = new StringBuilder();
		
		resultString.append("Depth: ").append(String.format("%0$"+ 8 + "s", this.getDepth())).append(" |");
		resultString.append("Op: ").append(String.format("%1$"+ 6 + "s", this.getProblemState().getPredecessorTransformation())).append(" |");
		resultString.append("Move cost: ").append(String.format("%1$"+ 2 + "s", this.getProblemState().getLocalTransformationCostFromPredecessorState())).append(" |");
		resultString.append("Cume move cost: ").append(String.format("%1$"+ 8 + "s", this.getProblemState().getCumulativeTransformationCostFromInitialState())).append(" |");
		
		if (this.evalFunction != null && this.goalState != null) 
			resultString.append("Eval cost: ").append(String.format("%1$"+ 10 + "s", this.getEvaluationPathCost(evalFunction, goalState))).append(" |");
		
		resultString.append("State: ").append(String.format("%1$"+ 10 + "s", this.getProblemState().toString()));
		
		return resultString.toString();
	}
	
	@Override
	public boolean equals(Object that){
		if (this == that) return true;
		if (!(that instanceof TreeNodeImpl)) return false;
		
		TreeNodeImpl thatNode = (TreeNodeImpl) that;
		
		boolean result = Objects.equals(this.getProblemState(), thatNode.getProblemState());
		
		return result;
	}
	
	@Override 
	public int hashCode() {
		return Objects.hash(this.getProblemState());
	}
}
