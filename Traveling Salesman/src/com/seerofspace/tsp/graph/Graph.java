package com.seerofspace.tsp.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Graph<IdType, WeightType, 
NodeType extends Node<IdType, WeightType>, 
EdgeType extends Edge<IdType, WeightType>> {
	
	private Map<IdType, NodeType> map;
	private NodeFactoryInterface<IdType, WeightType, NodeType> nodeFactory;
	private EdgeFactoryInterface<IdType, WeightType, EdgeType> edgeFactory;
	
	public Graph(NodeFactoryInterface<IdType, WeightType, NodeType> nodeFactory, 
			EdgeFactoryInterface<IdType, WeightType, EdgeType> edgeFactory) {
		map = new HashMap<>();
		this.nodeFactory = nodeFactory;
		this.edgeFactory = edgeFactory;
	}
	
	public void addEdgeUndirected(NodeType n1, NodeType n2, WeightType weight) {
		map.putIfAbsent(n1.getId(), n1);
		map.putIfAbsent(n2.getId(), n2);
		//n1.addEdge(new Edge<IdType, WeightType>(n2, weight));
		n1.addEdge(edgeFactory.factory(n2, weight));
		//n2.addEdge(new Edge<IdType, WeightType>(n1, weight));
		n2.addEdge(edgeFactory.factory(n1, weight));
	}
	
	public void addEdgeUndirected(IdType id1, IdType id2, WeightType weight) {
		NodeType n1 = map.get(id1);
		NodeType n2 = map.get(id2);
		if(n1 == null) {
			//n1 = new Node<>(id1);
			n1 = nodeFactory.factory(id1);
			map.put(id1, n1);
		}
		if(n2 == null) {
			//n2 = new Node<>(id2);
			n2 = nodeFactory.factory(id2);
			map.put(id2, n2);
		}
		//n1.addEdge(new Edge<>(n2, weight));
		n1.addEdge(edgeFactory.factory(n2, weight));
		//n2.addEdge(new Edge<>(n1, weight));
		n2.addEdge(edgeFactory.factory(n1, weight));
	}
	
	public void addEdgeDirected(NodeType n1, NodeType n2, WeightType weight) {
		map.putIfAbsent(n1.getId(), n1);
		map.putIfAbsent(n2.getId(), n2);
		n1.addEdge(edgeFactory.factory(n2, weight));
	}
	
	public void addEdgeDirected(IdType id1, IdType id2, WeightType weight) {
		NodeType n1 = map.get(id1);
		NodeType n2 = map.get(id2);
		if(n1 == null) {
			//n1 = new Node<>(id1);
			n1 = nodeFactory.factory(id1);
			map.put(id1, n1);
		}
		if(n2 == null) {
			//n2 = new Node<>(id2);
			n2 = nodeFactory.factory(id2);
			map.put(id2, n2);
		}
		//n1.addEdge(new Edge<>(n2, weight));
		n1.addEdge(edgeFactory.factory(n2, weight));
	}
	
	public NodeType getNode(IdType id) {
		return map.get(id);
	}
	
	public boolean containsNode(NodeType node) {
		NodeType temp = map.get(node.getId());
		if(temp == node) {
			return true;
		}
		return false;
	}
	
	public boolean containsNode(IdType id) {
		return map.containsKey(id);
	}
	
	public Iterator<NodeType> getIterator() {
		return Collections.unmodifiableCollection(map.values()).iterator();
	}
	
	public Collection<NodeType> getCollection() {
		return Collections.unmodifiableCollection(map.values());
	}
	
	public List<NodeType> toList() {
		List<NodeType> list = new ArrayList<>(map.size());
		Iterator<NodeType> iterator = getIterator();
		while(iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}
	
	public int getSize() {
		return map.size();
	}
	
}
