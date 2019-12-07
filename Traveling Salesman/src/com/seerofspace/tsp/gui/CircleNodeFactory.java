package com.seerofspace.tsp.gui;

import com.seerofspace.tsp.graph.NodeFactoryInterface;

import javafx.scene.paint.Color;

public class CircleNodeFactory implements NodeFactoryInterface<String, Integer, CircleNode> {
	
	private double x;
	private double y;
	private double radius;
	private Color color;
	
	public CircleNodeFactory() {
		this(0, 0, 1, Color.BLACK);
	}
	
	public CircleNodeFactory(double x, double y, double radius, Color color) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
	}
	
	@Override
	public CircleNode factory(String id) {
		return new CircleNode(id, x, y, radius, color);
	}
	
}
