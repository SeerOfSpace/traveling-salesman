package com.seerofspace.tsp.core;

import com.seerofspace.tsp.graph.Node;
import com.seerofspace.tsp.graph.NodeFactoryInterface;

public class MyNodeFactory<IdType, WeightType> implements 
NodeFactoryInterface<IdType, WeightType, Node<IdType, WeightType>> {

	@Override
	public Node<IdType, WeightType> factory(IdType id) {
		return new Node<>(id);
	}

}
