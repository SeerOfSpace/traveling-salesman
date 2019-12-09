package com.seerofspace.tsp.core;

import java.util.ArrayList;
import java.util.Collections;
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
	
	public static <IdType, WeightType extends Comparable<WeightType>, NodeType extends Node<IdType, WeightType>, EdgeType extends Edge<IdType, WeightType>> List<Node<IdType, WeightType>> nearestNeighbor(
			Graph<IdType, WeightType, NodeType, EdgeType> graph, 
			NodeType startingNode) {
		
		if(!graph.containsNode(startingNode)) {
			throw new IllegalArgumentException("Starting node does not exist within graph");
		}
		Map<IdType, Node<IdType, WeightType>> visitedNodes = new HashMap<>(graph.getSize());
		List<Node<IdType, WeightType>> route = new ArrayList<>(graph.getSize());
		route.add(startingNode);
		visitedNodes.put(startingNode.getId(), startingNode);
		Node<IdType, WeightType> currentNode = startingNode;
		boolean looped = false;
		
		while(visitedNodes.size() != graph.getSize()) {
			if(currentNode.getAdjacentSize() == 0) {
				throw new RuntimeException("Node has no edges");
			}
			
			Node<IdType, WeightType> nextNode = findNextPathShallow(currentNode, visitedNodes);
			if(nextNode == null) {
				List<Node<IdType, WeightType>> pathList = findNextPathDeep(currentNode, visitedNodes);
				if(pathList == null) {
					throw new RuntimeException("Path cannot be found");
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
	
	private static <IdType, WeightType extends Comparable<WeightType>> Node<IdType, WeightType> findNextPathShallow(
			Node<IdType, WeightType> currentNode, 
			Map<IdType, Node<IdType, WeightType>> visitedNodes) {
		
		Iterator<Edge<IdType, WeightType>> iterator = currentNode.getAdjacentIterator();
		Edge<IdType, WeightType> lowest = null;
		while(iterator.hasNext()) {
			Edge<IdType, WeightType> nextEdge = iterator.next();
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
	
	private static <IdType, WeightType extends Comparable<WeightType>> List<Node<IdType, WeightType>> findNextPathDeep(
			Node<IdType, WeightType> currentNode, 
			Map<IdType, Node<IdType, WeightType>> visitedNodes) {
		
		Map<IdType, Node<IdType, WeightType>> visitedNodesQueue = new HashMap<>();
		visitedNodesQueue.put(currentNode.getId(), currentNode);
		
		Queue<BacktraceNode<IdType, WeightType>> queue = new LinkedList<>();
		List<Node<IdType, WeightType>> list = getOrderedAdjacentList(currentNode);
		list.forEach(e -> {
			queue.add(new BacktraceNode<IdType, WeightType>(e, null));
			visitedNodesQueue.put(e.getId(), e);
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
				for(Node<IdType, WeightType> e : list) {
					if(!visitedNodesQueue.containsKey(e.getId())) { 
						queue.add(new BacktraceNode<IdType, WeightType>(e, nextBacktraceNode));
						visitedNodesQueue.put(e.getId(), e);
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
