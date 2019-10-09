package com.seerofspace.tsp.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.seerofspace.tsp.graph.Edge;
import com.seerofspace.tsp.graph.Graph;
import com.seerofspace.tsp.graph.Node;

public class Tester {
	
	public static void main(String[] args) {
		Graph<String, Integer> graph = new Graph<>();
		loadFile(new File("src\\com\\seerofspace\\tsp\\core\\test.txt"), graph);
		printGraph(graph);
		List<Node<String, Integer>> route = NearestNeighbor.nearestNeighbor(graph, graph.getNode("Rockville"));
		route.forEach(e -> {
			System.out.println(e.getId());
		});
	}
	
	public static <IdType, WeightType> void printGraph(Graph<IdType, WeightType> graph) {
		Iterator<Node<IdType, WeightType>> iterator = graph.getIterator();
		while(iterator.hasNext()) {
			Node<IdType, WeightType> node = iterator.next();
			Iterator<Edge<IdType, WeightType>> adjacentIterator = node.getAdjacentIterator();
			System.out.println(node.getId() + ":");
			while(adjacentIterator.hasNext()) {
				Edge<IdType, WeightType> edge = adjacentIterator.next();
				System.out.println(edge.getDestination().getId() + " " + edge.getWeight());
			}
			System.out.println();
		}
	}
	
	public static void loadFile(File file, Graph<String, Integer> graph) {
		try {
			Scanner scanner = new Scanner(file);
			scanner.useDelimiter(", |\\r\\n");
			while(scanner.hasNextLine()) {
				String id1 = scanner.next();
				String id2 = scanner.next();
				int weight = scanner.nextInt();
				boolean directed = scanner.nextBoolean();
				if(directed) {
					graph.addEdgeDirected(id1, id2, weight);
				} else {
					graph.addEdgeUndirected(id1, id2, weight);
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
