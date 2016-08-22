package alouw.csc480.search.interfaces;

import java.util.List;

/*
 * A mutable node that maintains pointers to its immediate parent and children. 
 * Each node encapsulates the following:
 * 	- the depth within the tree at which it was discovered; and
 *  - whether it has been explored yet; and
 *  - whether it is a root node; and
 *  - the immutable problem state within a problem space associated with this node 
 */

public interface TreeNode{
   // a possibly empty list of children
   public List<TreeNode> getChildrenNodes();
   
   // the parent node of the current node
   // current Node == getParentNode for ROOT
   public TreeNode getParentNode();
   
   // whether the current node is the root node
   public Boolean isRootNode();
   
   // returns an object modeling some external state 
   public ProblemState getProblemState();
   
   // depth of node in tree
   public int getDepth();
   
   // the *speculative* cost of traveling to this node if not explored
   // the *actual cost of traveling to this node if explored
   public int getEvaluationPathCost(EvaluationFunction h, ProblemState goalState);
}
