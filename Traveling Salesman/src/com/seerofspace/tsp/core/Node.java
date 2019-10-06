package com.seerofspace.tsp.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Node<IdType, WeightType> {
	
	private IdType id;
	private Map<IdType, Edge<IdType, WeightType>> adjacentMap;
	
	public Node(IdType value) {
		this.id = value;
		adjacentMap = new HashMap<>();
	}
	
	protected void addEdge(Edge<IdType, WeightType> edge) {
		adjacentMap.putIfAbsent(edge.getDestination().getId(), edge);
	}
	
	public boolean containsDestination(IdType id) {
		return adjacentMap.containsKey(id);
	}
	
	public Edge<IdType, WeightType> getEdge(IdType id) {
		return adjacentMap.get(id);
	}
	
	public Iterator<Edge<IdType, WeightType>> getIterator() {
		return Collections.unmodifiableCollection(adjacentMap.values()).iterator();
	}
	
	public IdType getId() {
		return id;
	}
	
}