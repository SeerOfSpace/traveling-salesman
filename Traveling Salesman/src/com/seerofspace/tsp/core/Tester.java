package com.seerofspace.tsp.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.seerofspace.tsp.graph.Edge;
import com.seerofspace.tsp.graph.Graph;
import com.seerofspace.tsp.graph.Node;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Tester {
	
	public static void main(String[] args) throws MyException {
		
		Graph<String, Integer, Node<String, Integer>, Edge<String, Integer>> graph = 
				new Graph<>(new MyNodeFactory<>(), new MyEdgeFactory<>());
		
		loadFile(new File("src\\com\\seerofspace\\tsp\\core\\test.txt"), graph);
		//printGraph(graph);
		List<Node<String, Integer>> route = NearestNeighbor.nearestNeighbor(graph, graph.getNode("Rockville"));
		route.forEach(e -> {
			System.out.println(e.getId());
		});
		System.out.println();
		System.out.println(getRouteLength(route));
		System.out.println();
		route = BranchAndBound.branchAndBound(graph, graph.getNode("Rockville"));
		route.forEach(e -> {
			System.out.println(e.getId());
		});
		System.out.println();
		System.out.println(getRouteLength(route));
	}
	
	public static <IdType, WeightType> void printGraph(
			Graph<IdType, WeightType, Node<IdType, WeightType>, Edge<IdType, WeightType>> graph) {
		
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
	
	private static void loadFile(File file, 
			Graph<String, Integer, ?, ?> graph) {
		
		try {
			Scanner scanner1 = new Scanner(file);
			Scanner scanner2;
			while(scanner1.hasNextLine()) {
				String line = scanner1.nextLine();
				if(line.charAt(0) == '#') {
					continue;
				}
				scanner2 = new Scanner(line);
				scanner2.useDelimiter(", ");
				String id1 = scanner2.next();
				String id2 = scanner2.next();
				int weight = scanner2.nextInt();
				boolean directed = scanner2.nextBoolean();
				if(directed) {
					graph.addEdgeDirected(id1, id2, weight);
				} else {
					graph.addEdgeUndirected(id1, id2, weight);
				}
				scanner2.close();
			}
			scanner1.close();
		} catch (FileNotFoundException | NoSuchElementException e) {
			e.printStackTrace();
			new Alert(AlertType.ERROR, e.getMessage()).showAndWait();
		}
	}
	
	public static int getRouteLength(List<Node<String, Integer>> route) {
		int length = 0;
		for(int i = 0; i < route.size() - 1; i++) {
			length += route.get(i).getEdge(route.get(i + 1).getId()).getWeight();
		}
		return length;
	}
	
}
