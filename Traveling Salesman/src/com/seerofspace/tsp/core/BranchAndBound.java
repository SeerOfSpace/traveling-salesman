package com.seerofspace.tsp.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.seerofspace.tsp.graph.Edge;
import com.seerofspace.tsp.graph.Graph;
import com.seerofspace.tsp.graph.Node;

public class BranchAndBound {
	
	public static <IdType> List<Node<IdType, Integer>> branchAndBound(
			Graph<IdType, Integer, Node<IdType, Integer>, Edge<IdType, Integer>> graph,
			Node<IdType, Integer> startingNode) {
		
		if(!isCompleteGraph(graph)) {
			throw new IllegalArgumentException("Graph is not complete");
		}
		DataStruct<IdType> dataStruct = new DataStruct<>();
		dataStruct.graphSize = graph.getSize();
		dataStruct.bestLength = Integer.MAX_VALUE;
		dataStruct.currentSolution = new Stack<>();
		dataStruct.currentSolution.push(startingNode);
		branchAndBoundRecursive(startingNode, 0, dataStruct);
		return dataStruct.bestSolution;
	}
	
	private static <IdType> void branchAndBoundRecursive(
			Node<IdType, Integer> currentNode,
			int currentLength,
			DataStruct<IdType> dataStruct) {
		
		if(dataStruct.currentSolution.size() == dataStruct.graphSize) {
			Node<IdType, Integer> startingNode = dataStruct.currentSolution.get(0);
			int lastLength = dataStruct.currentSolution.peek().getEdge(startingNode.getId()).getWeight();
			if(currentLength + lastLength < dataStruct.bestLength) {
				dataStruct.bestLength = currentLength + lastLength;
				dataStruct.bestSolution = new ArrayList<>(dataStruct.currentSolution);
				dataStruct.bestSolution.add(startingNode);
			}
			return;
		}
		
		for(Edge<IdType, Integer> nextEdge : currentNode.getAdjacentCollection()) {
			Node<IdType, Integer> nextNode = nextEdge.getDestination();
			if(dataStruct.currentSolution.contains(nextNode)) {
				continue;
			}
			int newLength = currentLength + nextEdge.getWeight();
			if(newLength >= dataStruct.bestLength) {
				continue;
			}
			dataStruct.currentSolution.push(nextNode);
			branchAndBoundRecursive(nextNode, newLength, dataStruct);
			dataStruct.currentSolution.pop();
		}
	}
	
	private static class DataStruct<IdType> {
		int graphSize;
		Stack<Node<IdType, Integer>> currentSolution;
		List<Node<IdType, Integer>> bestSolution;
		int bestLength;
	}
	
	private static <IdType> boolean isCompleteGraph(Graph<IdType, ?, ?, ?> graph) {
		for(Node<IdType, ?> node : graph.getCollection()) {
			int size = node.containsDestination(node.getId()) ? graph.getSize() : graph.getSize() - 1;
			if(node.getAdjacentSize() != size) {
				return false;
			}
		}
		return true;
	}
	
}
