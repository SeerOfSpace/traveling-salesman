package com.seerofspace.tsp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.seerofspace.tsp.graph.Edge;
import com.seerofspace.tsp.graph.Graph;
import com.seerofspace.tsp.graph.Node;

public class NearestNeighbor {
	
	public static <IdType, WeightType extends Comparable<WeightType>> List<Node<IdType, WeightType>> nearestNeighbor(
			Graph<IdType, WeightType> graph, 
			Node<IdType, WeightType> startingNode) {
		
		if(!graph.containsNode(startingNode)) {
			throw new IllegalArgumentException("Starting node does not exist within graph");
		}
		Map<IdType, Node<IdType, WeightType>> visitedNodes = new HashMap<>(graph.getSize());
		List<Node<IdType, WeightType>> route = new ArrayList<>(graph.getSize());
		route.add(startingNode);
		visitedNodes.put(startingNode.getId(), startingNode);
		Node<IdType, WeightType> currentNode = startingNode;
		
		while(visitedNodes.size() != graph.getSize()) {
			if(currentNode.getAdjacentSize() == 0) {
				throw new RuntimeException("Node has no edges");
			}
			if(isOnlyCircular(currentNode)) {
				throw new RuntimeException("All edges are circular");
			}
			Iterator<Edge<IdType, WeightType>> iterator = currentNode.getAdjacentIterator();
			Edge<IdType, WeightType> lowest = null;
			Edge<IdType, WeightType> lowestVisited = null;
			
			while(iterator.hasNext()) {
				Edge<IdType, WeightType> temp = iterator.next();
				if(temp.getDestination() == currentNode) {
					continue;
				}
				if(visitedNodes.containsKey(temp.getDestination().getId())) {
					if(lowestVisited == null) {
						lowestVisited = temp;
					} else if(temp.getWeight().compareTo(lowestVisited.getWeight()) < 0) {
						lowestVisited = temp;
					}
				} else {
					if(lowest == null) {
						lowest = temp;
					} else if(temp.getWeight().compareTo(lowest.getWeight()) < 0) {
						lowest = temp;
					}
				}
			}
			
			Node<IdType, WeightType> result;
			if(lowest == null) { 
				result = lowestVisited.getDestination();
			} else {
				result = lowest.getDestination();
			}
			route.add(result);
			visitedNodes.putIfAbsent(result.getId(), result);
			currentNode = result;
		}
		
		route.add(startingNode);
		return route;
	}
	
	private static <IdType, WeightType> boolean isOnlyCircular(Node<IdType, WeightType> node) {
		Iterator<Edge<IdType, WeightType>> iterator = node.getAdjacentIterator();
		while(iterator.hasNext()) {
			Edge<IdType, WeightType> edge = iterator.next();
			if(edge.getDestination() != node) {
				return false;
			}
		}
		return true;
	}
	
}
