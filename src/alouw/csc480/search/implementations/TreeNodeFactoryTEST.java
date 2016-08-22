package alouw.csc480.search.implementations;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import alouw.csc480.search.interfaces.EvaluationFunction;
import alouw.csc480.search.interfaces.ProblemState;
import alouw.csc480.search.interfaces.ProblemTransformationOperation;
import alouw.csc480.search.interfaces.TreeNode;

public class TreeNodeFactoryTEST {
	
	private static final String goalState = "1 2 3 5 0 4 6 7 8";
	private static final ProblemState problemGoal = ProblemStateFactory.getNewProblemState(goalState, 
			ProblemTransformationOperation.NONE);
	
	private static final String stateOne = "1 2 3 4 0 5 6 7 8";
	private static final ProblemTransformationOperation operationOne = ProblemTransformationOperation.DOWN;
	private static final ProblemState problemOne = ProblemStateFactory.
			getNewProblemState(stateOne, operationOne, 10);
	
	@Test
	public void testRootTreeNodeFactoryExecution() {
		TreeNode treeNode = TreeNodeFactory.getNewRootNode(problemOne);
		Assert.assertEquals(stateOne, treeNode.getProblemState().toString());
		Assert.assertEquals(treeNode.isRootNode(), true);
		Assert.assertEquals(treeNode.getDepth(), 0);
		
		try {
			TreeNodeFactory.getNewRootNode(null);
			Assert.fail();
		} catch (Exception e) {} 
	}
	
	/*
	 * Test creation of non-root node w/ correct parenting relationship
	 */
	@Test
	public void testTreeNodeFactoryExecution() {
		TreeNode rootNode = TreeNodeFactory.getNewRootNode(problemOne);
		TreeNode treeNode = TreeNodeFactory.getNewTreeNode(rootNode, problemOne);
		
		Assert.assertEquals(stateOne, treeNode.getProblemState().toString());
		Assert.assertEquals(treeNode.getParentNode(), rootNode);
		Assert.assertEquals(treeNode.getDepth(), rootNode.getDepth()+1);
		
		try {
			TreeNodeFactory.getNewRootNode(null);
			Assert.fail();
		} catch (Exception e) {} 
	}
	
	@Test 
	public void testTreeNodeSuccessorFunction() {
		TreeNode rootNode = TreeNodeFactory.getNewRootNode(
				ProblemStateFactory.getNewProblemState(stateOne, 
						ProblemTransformationOperation.NONE, 0));
		List<TreeNode> childrenList = rootNode.getChildrenNodes();
		Assert.assertNotNull(childrenList);
		Assert.assertEquals(childrenList.size(), 4);
		for (TreeNode n: childrenList) {
			Assert.assertEquals(n.getParentNode(), rootNode);
			Assert.assertEquals(n.isRootNode(), false);
			Assert.assertEquals(n.getDepth()-1, rootNode.getDepth());
			
			ProblemState p = n.getProblemState();
			Assert.assertEquals(p.applyTransformation(p.getPredecessorTransformation().reverseTransformation()).toString(),
					rootNode.getProblemState().toString());
		}
	}
	
	@Test
	public void testTreeNodeCostFunctions() {
		TreeNode rootNode = TreeNodeFactory.getNewRootNode(
				ProblemStateFactory.getNewProblemState(stateOne, 
						ProblemTransformationOperation.NONE, 0));
		for (TreeNode n: rootNode.getChildrenNodes()) {
			
			switch(n.getProblemState().getPredecessorTransformation()) {
				case UP:{
					Assert.assertEquals(2, n.getEvaluationPathCost(EvaluationFunction.NONE, problemGoal));
					Assert.assertEquals(4, n.getEvaluationPathCost(EvaluationFunction.GREEDY, problemGoal));
					Assert.assertEquals(6, n.getEvaluationPathCost(EvaluationFunction.ASTAR_1, problemGoal));
					Assert.assertEquals(8, n.getEvaluationPathCost(EvaluationFunction.ASTAR_2, problemGoal));
					Assert.assertEquals(22, n.getEvaluationPathCost(EvaluationFunction.ASTAR_3, problemGoal));
					break;
				}
				case DOWN:{
					Assert.assertEquals(7, n.getEvaluationPathCost(EvaluationFunction.NONE, problemGoal));
					Assert.assertEquals(4, n.getEvaluationPathCost(EvaluationFunction.GREEDY, problemGoal));
					Assert.assertEquals(11, n.getEvaluationPathCost(EvaluationFunction.ASTAR_1, problemGoal));
					Assert.assertEquals(13, n.getEvaluationPathCost(EvaluationFunction.ASTAR_2, problemGoal));
					Assert.assertEquals(32, n.getEvaluationPathCost(EvaluationFunction.ASTAR_3, problemGoal));
					break;
				}
				case LEFT:{
					Assert.assertEquals(4, n.getEvaluationPathCost(EvaluationFunction.NONE, problemGoal));
					Assert.assertEquals(3, n.getEvaluationPathCost(EvaluationFunction.GREEDY, problemGoal));
					Assert.assertEquals(7, n.getEvaluationPathCost(EvaluationFunction.ASTAR_1, problemGoal));
					Assert.assertEquals(8, n.getEvaluationPathCost(EvaluationFunction.ASTAR_2, problemGoal));
					Assert.assertEquals(18, n.getEvaluationPathCost(EvaluationFunction.ASTAR_3, problemGoal));
					break;
				}
				case RIGHT:{
					Assert.assertEquals(5, n.getEvaluationPathCost(EvaluationFunction.NONE, problemGoal));
					Assert.assertEquals(3, n.getEvaluationPathCost(EvaluationFunction.GREEDY, problemGoal));
					Assert.assertEquals(8, n.getEvaluationPathCost(EvaluationFunction.ASTAR_1, problemGoal));
					Assert.assertEquals(9, n.getEvaluationPathCost(EvaluationFunction.ASTAR_2, problemGoal));
					Assert.assertEquals(18, n.getEvaluationPathCost(EvaluationFunction.ASTAR_3, problemGoal));
					break;
				}
				case NONE:	Assert.fail(); // should not be reachable
			}
		}
	}
}