package com.seerofspace.tsp.core;

import java.util.HashMap;
import java.util.Map;

public class Graph<IdType, WeightType> {
	
	private Map<IdType, Node<IdType, WeightType>> map;
	
	public Graph() {
		map = new HashMap<>();
	}
	
	public void addEdgeUndirected(Node<IdType, WeightType> n1, Node<IdType, WeightType> n2, WeightType weight) {
		map.putIfAbsent(n1.getId(), n1);
		map.putIfAbsent(n2.getId(), n2);
		n1.addEdge(new Edge<IdType, WeightType>(n2, weight));
		n2.addEdge(new Edge<IdType, WeightType>(n1, weight));
	}
	
	public void addEdgeUndirected(IdType id1, IdType id2, WeightType weight) {
		Node<IdType, WeightType> n1 = map.get(id1);
		Node<IdType, WeightType> n2 = map.get(id2);
		if(n1 == null) {
			n1 = new Node<>(id1);
			map.put(id1, n1);
		}
		if(n2 == null) {
			n2 = new Node<>(id2);
			map.put(id2, n2);
		}
		n1.addEdge(new Edge<>(n2, weight));
		n2.addEdge(new Edge<>(n1, weight));
	}
	
	public void addEdgeDirected(IdType id1, IdType id2, WeightType weight) {
		Node<IdType, WeightType> n1 = map.get(id1);
		Node<IdType, WeightType> n2 = map.get(id2);
		if(n1 == null) {
			n1 = new Node<>(id1);
			map.put(id1, n1);
		}
		if(n2 == null) {
			n2 = new Node<>(id2);
			map.put(id2, n2);
		}
		n1.addEdge(new Edge<>(n2, weight));
	}
	
	public Node<IdType, WeightType> getNode(IdType id) {
		return map.get(id);
	}
	
	public boolean containsNode(IdType id) {
		return map.containsKey(id);
	}
	
}
