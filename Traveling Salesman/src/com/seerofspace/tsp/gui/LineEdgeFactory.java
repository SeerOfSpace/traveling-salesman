package com.seerofspace.tsp.gui;

import com.seerofspace.tsp.graph.EdgeFactoryInterface;
import com.seerofspace.tsp.graph.Node;

import javafx.scene.paint.Color;

public class LineEdgeFactory implements EdgeFactoryInterface<String, Integer, LineEdge> {
	
	private double thickness;
	private Color color;
	
	public LineEdgeFactory() {
		this(1, Color.BLACK);
	}
	
	public LineEdgeFactory(double thickness, Color color) {
		this.thickness = thickness;
		this.color = color;
	}
	
	@Override
	public LineEdge factory(Node<String, Integer> destination, Integer weight) {
		return new LineEdge(destination, weight, thickness, color);
	}

}
