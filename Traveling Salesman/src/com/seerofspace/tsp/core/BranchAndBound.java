package com.seerofspace.tsp.core;

import java.util.List;
import java.util.stream.Collectors;

import com.seerofspace.tsp.graph.Edge;
import com.seerofspace.tsp.graph.Graph;
import com.seerofspace.tsp.graph.Node;

public class BranchAndBound {
	
	public static <IdType> List<Node<IdType, Integer>> branchAndBound(
			Graph<IdType, Integer, Node<IdType, Integer>, Edge<IdType, Integer>> graph,
			Node<IdType, Integer> startingNode) {
		
		
		
		return null;
	}
	
	public static <IdType> List<Node<IdType, Integer>> branchAndBoundRecursive(
			Graph<IdType, Integer, Node<IdType, Integer>, Edge<IdType, Integer>> graph,
			Node<IdType, Integer> currentNode,
			List<Node<IdType, Integer>> currentSolution,
			Integer currentLength,
			List<Node<IdType, Integer>> bestSolution,
			Integer bestLength) {
		
		if(currentLength > bestLength) {
			//return
		}
		
		for(Node<IdType, Integer> nextNode : currentNode.getAdjacentCollection().stream().map(e -> e.getDestination()).collect(Collectors.toList())) {
			
		}
		
		return null;
	}
	
	public static int getRouteLength(List<Node<String, Integer>> route) {
		int length = 0;
		for(int i = 0; i < route.size() - 1; i++) {
			length += route.get(i).getEdge(route.get(i + 1).getId()).getWeight();
		}
		return length;
	}
	
}
