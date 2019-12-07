package com.seerofspace.tsp.gui;

import com.seerofspace.tsp.graph.Edge;
import com.seerofspace.tsp.graph.Node;

import javafx.scene.paint.Color;

public class LineEdge extends Edge<String, Integer> {
	
	private double thickness;
	private Color color;
	
	public LineEdge(Node<String, Integer> destination, Integer weight) {
		this(destination, weight, 1);
	}
	
	public LineEdge(Node<String, Integer> destination, Integer weight, double thickness) {
		this(destination, weight, thickness, Color.BLACK);
	}
	
	public LineEdge(Node<String, Integer> destination, Integer weight, double thickness, Color color) {
		super(destination, weight);
		this.setThickness(thickness);
		this.setColor(color);
	}

	public double getThickness() {
		return thickness;
	}

	public void setThickness(double thickness) {
		this.thickness = thickness;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
}
