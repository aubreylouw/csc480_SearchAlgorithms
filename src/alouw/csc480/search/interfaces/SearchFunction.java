package alouw.csc480.search.interfaces;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;
import java.util.TreeMap;

import alouw.csc480.search.implementations.SearchStatsCollector;

public enum SearchFunction {
	
	BREADTH_FIRST {
		@Override
		public Optional<TreeNode> apply(final ArrayDeque<TreeNode> queue, final EvaluationFunction evalFunction, 
				final ProblemState goalState, SearchStatsCollector statsCollector) {
			
			// local variables for tracking search state
			final HashSet<ProblemState> statesExplored = new HashSet<>();
			boolean solutionFound = false;
			TreeNode solutionNode = null;
			
			// confirm that the queue has at least an initial root node
			assert(!queue.isEmpty() && queue.peekFirst().isRootNode());
			
			// the queue is treated as FIFO queue
			// nodes are taken from the front and inserted in the rear
			// the queue stores at most the current and current -1 depth set of nodes
			while (!solutionFound) {

				// capture max length of queue
				statsCollector.setMaxQueueSize(queue.size());
				
				// take from the front
				TreeNode currentNode = queue.pop();
				
				// count the # of nodes explored
				statsCollector.incrementNodesExplored(1);
				
				// break if the solution has been found
				if (currentNode.getProblemState().equals(goalState)) {
					solutionNode = currentNode;
					solutionFound = true;
					
					statsCollector.incrementSolutionCost(currentNode.getProblemState().getCumulativeTransformationCostFromInitialState());
					statsCollector.incrementSolutionPath(currentNode.getDepth());
					
					break;
				}
				
				// if it is not the solution, store the problem state in a set
				statesExplored.add(currentNode.getProblemState());
				
				// insert at the front of the queue any nodes w/ a new config of the problem state
				currentNode.getChildrenNodes().stream().sorted(evalFunction.getComparator(goalState)).forEachOrdered(x -> {
					if (!statesExplored.contains(x.getProblemState())) queue.add(x);});
			}
			
			return Optional.ofNullable(solutionNode);
		}
	}, 
	
	DEPTH_FIRST {
		@Override
		public Optional<TreeNode> apply(ArrayDeque<TreeNode> queue, EvaluationFunction evalFunction, 
				ProblemState goalState, SearchStatsCollector statsCollector) {
			
			// local variables for tracking search state
			final HashSet<ProblemState> statesExplored = new HashSet<>();
			boolean solutionFound = false;
			TreeNode solutionNode = null;
			
			// confirm that the queue has at least an initial root node
			assert(!queue.isEmpty() && queue.peekFirst().isRootNode());

			// the queue is treated as a LIFO queue
			// nodes are taken from and inserted at the front
			// the queue stores at most the current and current -1 depth set of nodes
			while (!solutionFound) {

				// capture max length of queue
				statsCollector.setMaxQueueSize(queue.size());
				
				// take from the front
				TreeNode currentNode = queue.pop();
				
				// count the # of nodes explored
				statsCollector.incrementNodesExplored(1);
				
				// break if the solution has been found
				if (currentNode.getProblemState().equals(goalState)) {
					solutionNode = currentNode;
					solutionFound = true;
					
					statsCollector.incrementSolutionCost(currentNode.getProblemState().getCumulativeTransformationCostFromInitialState());
					statsCollector.incrementSolutionPath(currentNode.getDepth());
					
					break;
				}
				
				// if it is not the solution, store the problem state in a set
				statesExplored.add(currentNode.getProblemState());
				
				// insert at the front of the queue any nodes w/ a new config of the problem state
				currentNode.getChildrenNodes().stream().sorted(evalFunction.getComparator(goalState)).forEachOrdered(x -> {
					if (!statesExplored.contains(x.getProblemState())) queue.push(x);});
			}
				
			return Optional.ofNullable(solutionNode);
		}
	}, 
	
	IDA {
		@Override
		public Optional<TreeNode> apply(ArrayDeque<TreeNode> queue, EvaluationFunction evalFunction, 
				ProblemState goalState, SearchStatsCollector statsCollector) {
			
			// confirm that the queue has at least an initial root node
			assert(!queue.isEmpty() && queue.peekFirst().isRootNode());
			
			// local variables for tracking search state
			final HashSet<ProblemState> statesExplored = new HashSet<>();
			boolean solutionFound = false;
			TreeNode solutionNode = null;
			TreeNode rootNode = queue.peek(); 
			int depthLimit = 0;
			
			// the queue is treated as FIFO queue up to a specific depth
			// nodes are taken from the front and inserted in the rear
			// when we have examined all the nodes at the current depth, we start over and advance the depth limit by one
			while (!solutionFound) {

				// while loop invariant 
				if (!solutionFound) assert(!queue.isEmpty());
				
				// push root back onto the queue
				queue.push(rootNode);
				statesExplored.clear();
				
				while (!queue.isEmpty()) {
					
					// capture max length of queue
					statsCollector.setMaxQueueSize(queue.size());
					
					// take from the front
					TreeNode currentNode = queue.pop();
					
					// count the # of nodes explored
					statsCollector.incrementNodesExplored(1);
					
					// break if the solution has been found
					if (currentNode.getProblemState().equals(goalState)) {
						solutionNode = currentNode;
						solutionFound = true;
						
						statsCollector.incrementSolutionCost(currentNode.getProblemState().getCumulativeTransformationCostFromInitialState());
						statsCollector.incrementSolutionPath(currentNode.getDepth());
						
						break;
					}
					
					// if it is not the solution, store the problem state in a set
					statesExplored.add(currentNode.getProblemState());
					
					// add to the queue if < depth Limit && if we have not rediscovered the initial state
					if (currentNode.getDepth() < depthLimit)
						currentNode.getChildrenNodes().stream().sorted(evalFunction.getComparator(goalState)).forEachOrdered(x -> {
							if (!statesExplored.contains(x.getProblemState())) queue.push(x);});
				}
				
				// advance the search frontier by one
				depthLimit += 1;
			}
			
			return Optional.ofNullable(solutionNode);
		}
	}, 
	
	UNIFORM_COST {
		@Override
		public Optional<TreeNode> apply(ArrayDeque<TreeNode> queue, EvaluationFunction evalFunction, 
				ProblemState goalState, SearchStatsCollector statsCollector) {
						
			// local variables for tracking search state
			final TreeMap<TreeNode, TreeNode> sortedMap = new TreeMap<TreeNode, TreeNode>(evalFunction.getComparator(goalState));
			final HashSet<ProblemState> statesExplored = new HashSet<>();
			boolean solutionFound = false;
			TreeNode solutionNode =  null;
			
			// confirm that the queue has at least an initial root node
			assert(!queue.isEmpty() && queue.peekFirst().isRootNode());
			TreeNode node = queue.pop();
			sortedMap.put(node, node);
			
			// the frontier set of nodes is sorted
			while (!solutionFound) {
				
				//System.out.println("*************");
				//sortedMap.keySet().stream().forEachOrdered(x-> System.out.println(x));
				
				// capture max length of queue
				statsCollector.setMaxQueueSize(sortedMap.size());
				
				// take from the front
				TreeNode currentNode = sortedMap.pollFirstEntry().getKey();
				
				// count the # of nodes explored
				statsCollector.incrementNodesExplored(1);
				
				// break if the solution has been found
				if (currentNode.getProblemState().equals(goalState)) {
					solutionNode = currentNode;
					solutionFound = true;
					
					statsCollector.incrementSolutionCost(currentNode.getProblemState().getCumulativeTransformationCostFromInitialState());
					statsCollector.incrementSolutionPath(currentNode.getDepth());
					
					break;
				}
				
				// if it is not the solution, store the problem state in a set
				statesExplored.add(currentNode.getProblemState());

				// push the children into the set iff not already in the expanded set {statesExplored}
				currentNode.getChildrenNodes().stream().forEach(x -> {
					//System.out.print("CHILD " + x);
					if (!statesExplored.contains(x.getProblemState())) {
						if (!sortedMap.containsKey(x)) {
							sortedMap.put(x, x);
							//System.out.println(" ....NEW!");
						} else if (sortedMap.containsKey(x)) {
							if(x.getProblemState().compareTo(sortedMap.get(x).getProblemState()) < 0) {
								sortedMap.put(x, x);
								//System.out.println(" ....REPLACED!");
							} //else System.out.println(" ....THERE BUT NOT REPLACED!");
						} //else System.out.println(" ....IGNORED?!?!");
					} //else System.out.println(" ....NOT UNIQUE!!");
				});
			}
			return Optional.ofNullable(solutionNode);
		}
	}, 
	
	GREEDY {
		@Override
		public Optional<TreeNode> apply(ArrayDeque<TreeNode> queue, EvaluationFunction evalFunction, 
				ProblemState goalState, SearchStatsCollector statsCollector) {
			return UNIFORM_COST.apply(queue, evalFunction, goalState, statsCollector);		
		}
	}, 
	
	ASTAR {
		@Override
		public Optional<TreeNode> apply(ArrayDeque<TreeNode> queue, EvaluationFunction evalFunction, 
				ProblemState goalState, SearchStatsCollector statsCollector) {
			return GREEDY.apply(queue, evalFunction, goalState, statsCollector);
		}
	};
	
	public abstract Optional<TreeNode> apply(final ArrayDeque<TreeNode> queue, 
				final EvaluationFunction evalFunction, final ProblemState goalState,
				SearchStatsCollector statsCollector);
}