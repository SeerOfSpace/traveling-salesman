package com.seerofspace.tsp.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Node<IdType, WeightType> {
	
	private IdType id;
	private Map<IdType, Edge<IdType, WeightType>> adjacentMap;
	
	public Node(IdType id) {
		this.id = id;
		adjacentMap = new HashMap<>();
	}
	
	protected void addEdge(Edge<IdType, WeightType> edge) {
		adjacentMap.putIfAbsent(edge.getDestination().getId(), edge);
	}
	
	public boolean containsDestination(IdType id) {
		return adjacentMap.containsKey(id);
	}
	
	public boolean containsDestination(Node<IdType, WeightType> node) {
		Edge<IdType, WeightType> edge = adjacentMap.get(node.id);
		if(edge == null) {
			return false;
		}
		if(node == edge.getDestination()) {
			return true;
		}
		return false;
	}
	
	public Edge<IdType, WeightType> getEdge(IdType id) {
		return adjacentMap.get(id);
	}
	
	public Iterator<Edge<IdType, WeightType>> getAdjacentIterator() {
		return Collections.unmodifiableCollection(adjacentMap.values()).iterator();
	}
	
	public Collection<Edge<IdType, WeightType>> getAdjacentCollection() {
		return Collections.unmodifiableCollection(adjacentMap.values());
	}
	
	public int getAdjacentSize() {
		return adjacentMap.size();
	}
	
	public IdType getId() {
		return id;
	}
	
}