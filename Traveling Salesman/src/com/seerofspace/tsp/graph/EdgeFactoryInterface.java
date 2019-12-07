package com.seerofspace.tsp.graph;

public interface EdgeFactoryInterface<IdType, WeightType, EdgeType extends Edge<IdType, WeightType>> {
	
	public EdgeType factory(Node<IdType, WeightType> destination, WeightType weight);
	
}
