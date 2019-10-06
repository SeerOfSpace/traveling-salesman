package com.seerofspace.tsp.core;

import java.util.Iterator;

public class Tester {
	
	public static void main(String[] args) {
		Graph<String, Integer> graph = new Graph<>();
		graph.addEdgeUndirected("Rockville", "Silver Spring", 13);
		graph.addEdgeUndirected("Silver Spring", "Philidelphia", 136);
		System.out.println("Destinations from Silver Spring");
		Iterator<Edge<String, Integer>> iterator = graph.getNode("Silver Spring").getIterator();
		while(iterator.hasNext()) {
			Edge<String, Integer> edge = iterator.next();
			System.out.println(edge.getDestination().getId() + ": " + edge.getWeight());
		}
	}
	
}
