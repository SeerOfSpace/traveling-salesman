package com.seerofspace.tsp.core;

public class Edge<IdType ,WeightType> {
	
	private WeightType weight;
	private Node<IdType, WeightType> destination;
	
	public Edge(Node<IdType, WeightType> destination, WeightType weight) {
		this.destination = destination;
		this.weight = weight;
	}
	
	public Node<IdType, WeightType> getDestination() {
		return destination;
	}
	
	public WeightType getWeight() {
		return weight;
	}
	
}
