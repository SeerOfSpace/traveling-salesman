package com.seerofspace.tsp.core;

import com.seerofspace.tsp.graph.Edge;
import com.seerofspace.tsp.graph.EdgeFactoryInterface;
import com.seerofspace.tsp.graph.Node;

public class MyEdgeFactory<IdType, WeightType> implements 
EdgeFactoryInterface<IdType, WeightType, Edge<IdType, WeightType>> {

	@Override
	public Edge<IdType, WeightType> factory(Node<IdType, WeightType> destination, WeightType weight) {
		return new Edge<>(destination, weight);
	}

}
