package com.seerofspace.tsp.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import com.seerofspace.tsp.graph.Edge;
import com.seerofspace.tsp.graph.Graph;
import com.seerofspace.tsp.graph.Node;

public class NearestNeighbor {
	
	public static <IdType, WeightType extends Comparable<WeightType>> List<Node<IdType, WeightType>> nearestNeighbor(
			Graph<IdType, WeightType, Node<IdType, WeightType>, Edge<IdType, WeightType>> graph, 
			Node<IdType, WeightType> startingNode) throws MyException {
		
		if(!graph.containsNode(startingNode)) {
			throw new MyException("Graph does not contain the starting node");
		}
		Map<IdType, Node<IdType, WeightType>> visitedNodes = new HashMap<>(graph.getSize());
		List<Node<IdType, WeightType>> route = new ArrayList<>(graph.getSize());
		route.add(startingNode);
		visitedNodes.put(startingNode.getId(), startingNode);
		Node<IdType, WeightType> currentNode = startingNode;
		boolean looped = false;
		
		while(visitedNodes.size() != graph.getSize()) {
			if(currentNode.getAdjacentSize() == 0) {
				throw new MyException("A node has no edges");
			}
			
			Node<IdType, WeightType> nextNode = findNextPath(currentNode, visitedNodes);
			if(nextNode == null) {
				List<Node<IdType, WeightType>> pathList = findNextPathBFSearch(currentNode, visitedNodes);
				if(pathList == null) {
					throw new MyException("Path cannot be found");
				}
				route.addAll(pathList);
				nextNode = pathList.get(pathList.size() - 1);
			} else {
				route.add(nextNode);
			}
			visitedNodes.put(nextNode.getId(), nextNode);
			currentNode = nextNode;
			
			if(visitedNodes.size() == graph.getSize() && !looped) {
				visitedNodes.remove(startingNode.getId());
				looped = true;
			}
		}
		
		return route;
	}
	
	private static <IdType, WeightType extends Comparable<WeightType>> Node<IdType, WeightType> findNextPath(
			Node<IdType, WeightType> currentNode, 
			Map<IdType, Node<IdType, WeightType>> visitedNodes) {
		
		Edge<IdType, WeightType> lowest = null;
		for(Edge<IdType, WeightType> nextEdge : currentNode.getAdjacentCollection()) {
			if(nextEdge.getDestination() == currentNode) {
				continue;
			}
			if(!visitedNodes.containsKey(nextEdge.getDestination().getId())) {
				if(lowest == null || nextEdge.getWeight().compareTo(lowest.getWeight()) < 0) {
					lowest = nextEdge;
				}
			}
		}
		if(lowest == null) {
			return null;
		}
		return lowest.getDestination();
	}
	
	private static class BacktraceNode<IdType, WeightType> {

		BacktraceNode<IdType, WeightType> parent;
		Node<IdType, WeightType> node;
		
		public BacktraceNode(Node<IdType, WeightType> node, BacktraceNode<IdType, WeightType> parent) {
			this.parent = parent;
			this.node = node;
		}
		
	}
	
	private static <IdType, WeightType extends Comparable<WeightType>> List<Node<IdType, WeightType>> findNextPathBFSearch(
			Node<IdType, WeightType> currentNode, 
			Map<IdType, Node<IdType, WeightType>> visitedNodes) {
		
		List<Node<IdType, WeightType>> searchedNodes = new ArrayList<>();
		searchedNodes.add(currentNode);
		
		Queue<BacktraceNode<IdType, WeightType>> queue = new LinkedList<>();
		List<Node<IdType, WeightType>> list = getOrderedAdjacentList(currentNode);
		list.forEach(node -> {
			queue.add(new BacktraceNode<IdType, WeightType>(node, null));
			searchedNodes.add(node);
		});
		
		while(!queue.isEmpty()) {
			
			BacktraceNode<IdType, WeightType> nextBacktraceNode = queue.poll();
			
			if(!visitedNodes.containsKey(nextBacktraceNode.node.getId())) {
				List<Node<IdType, WeightType>> path = new ArrayList<>();
				do {
					path.add(nextBacktraceNode.node);
					nextBacktraceNode = nextBacktraceNode.parent;
				} while(nextBacktraceNode != null);
				Collections.reverse(path);
				return path;
			}
			
			if(nextBacktraceNode.node.getAdjacentSize() != 0) {
				list = getOrderedAdjacentList(nextBacktraceNode.node);
				for(Node<IdType, WeightType> node : list) {
					if(!searchedNodes.contains(node)) { 
						queue.add(new BacktraceNode<IdType, WeightType>(node, nextBacktraceNode));
						searchedNodes.add(node);
					}
				}
			}
			
		}
		
		return null;
	}
	
	private static <IdType, WeightType extends Comparable<WeightType>> List<Node<IdType, WeightType>> getOrderedAdjacentList(
			Node<IdType, WeightType> node) {
		
		List<Node<IdType, WeightType>> list = node.getAdjacentCollection().stream()
				.sorted((e1, e2) -> e1.getWeight().compareTo(e2.getWeight()))
				.map(e -> e.getDestination())
				.collect(Collectors.toList());
		
		return list;
	}
	
}
