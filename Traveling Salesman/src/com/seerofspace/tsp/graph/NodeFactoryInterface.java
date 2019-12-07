package com.seerofspace.tsp.graph;

public interface NodeFactoryInterface<IdType, WeightType, NodeType extends Node<IdType, WeightType>> {
	
	public NodeType factory(IdType id);
	
}
