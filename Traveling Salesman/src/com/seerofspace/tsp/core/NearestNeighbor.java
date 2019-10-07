package com.seerofspace.tsp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
			
			/*
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
			*/
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
	
	public static <IdType, WeightType extends Comparable<WeightType>> Node<IdType, WeightType> findNextPathShallow(
			Node<IdType, WeightType> currentNode, 
			Map<IdType, Node<IdType, WeightType>> visitedNodes) {
		
		Iterator<Edge<IdType, WeightType>> iterator = currentNode.getAdjacentIterator();
		Edge<IdType, WeightType> lowest = null;
		while(iterator.hasNext()) {
			Edge<IdType, WeightType> temp = iterator.next();
			if(temp.getDestination() == currentNode) {
				continue;
			}
			if(visitedNodes.containsKey(temp.getDestination().getId())) {
				if(lowest == null) {
					lowest = temp;
				} else if(temp.getWeight().compareTo(lowest.getWeight()) < 0) {
					lowest = temp;
				}
			}
		}
		return lowest.getDestination();
	}
	
	private static class BacktraceNode<IdType, WeightType> {
		
		private Node<IdType, WeightType> node;
		private BacktraceNode<IdType, WeightType> parent;
		
		public BacktraceNode(Node<IdType, WeightType> node, BacktraceNode<IdType, WeightType> parent) {
			this.node = node;
			this.parent = parent;
		}
		
		public Node<IdType, WeightType> getNode() {
			return node;
		}
		
		public BacktraceNode<IdType, WeightType> getParent() {
			return parent;
		}
		
	}
	
	public static <IdType, WeightType extends Comparable<WeightType>> List<Node<IdType, WeightType>> findNextPathDeep(
			Node<IdType, WeightType> currentNode, 
			Map<IdType, Node<IdType, WeightType>> visitedNodes) {
		
		Queue<BacktraceNode<IdType, WeightType>> queue = new LinkedList<>();
		List<Node<IdType, WeightType>> list = getOrderedAdjacentList(currentNode);
		list.forEach(e -> {
			queue.add(new BacktraceNode<IdType, WeightType>(e, null));
		});
		
		Map<IdType, Node<IdType, WeightType>> visitedNodesQueue = new HashMap<>();
		visitedNodesQueue.put(currentNode.getId(), currentNode);
		
		while(!queue.isEmpty()) {
			
			BacktraceNode<IdType, WeightType> nextNode = queue.poll();
			visitedNodesQueue.putIfAbsent(nextNode.getNode().getId(), nextNode.getNode());
			
			if(!visitedNodes.containsKey(nextNode.getNode().getId())) {
				List<Node<IdType, WeightType>> path = new ArrayList<>();
				do {
					path.add(nextNode.getNode());
					nextNode = nextNode.getParent();
				} while(nextNode != null);
				return path;
			}
			
			if(nextNode.getNode().getAdjacentSize() != 0) {
				list = getOrderedAdjacentList(nextNode.getNode());
				for(Node<IdType, WeightType> e : list) {
					if(!visitedNodesQueue.containsKey(e.getId())) {
						queue.add(new BacktraceNode<IdType, WeightType>(e, nextNode));
					}
				}
			}
			
		}
		
		return null;
	}
	
	private static <IdType, WeightType extends Comparable<WeightType>> List<Node<IdType, WeightType>> getOrderedAdjacentList(
			Node<IdType, WeightType> node) {
		
		List<Edge<IdType, WeightType>> list = new ArrayList<>(node.getAdjacentSize());
		List<Node<IdType, WeightType>> result = new ArrayList<>(node.getAdjacentSize());
		node.getAdjacentIterator().forEachRemaining(e -> {
			list.add(e);
		});
		list.sort((e1, e2) -> {
			return e1.getWeight().compareTo(e2.getWeight());
		});
		list.forEach(e -> {
			result.add(e.getDestination());
		});
		return result;
	}
	
}
