package com.seerofspace.tsp.gui;

import com.seerofspace.tsp.graph.Node;

import javafx.scene.paint.Color;

public class CircleNode extends Node<String, Integer> implements CircleInterface {
	
	private double x;
	private double y;
	private double vectorX;
	private double vectorY;
	private double radius;
	private Color color;
	protected boolean drawn;
	
	public CircleNode(String id) {
		this(id, 1);
	}
	
	public CircleNode(String id, double radius) {
		this(id, 0, 0, radius);
	}
	
	public CircleNode(String id, double x, double y, double radius) {
		this(id, x, y, radius, Color.BLACK);
	}
	
	public CircleNode(String id, double x, double y, double radius, Color color) {
		super(id);
		this.x = x;
		this.y = y;
		this.setRadius(radius);
		this.setColor(color);
		vectorX = 0;
		vectorY = 0;
		drawn = false;
	}
	
	public void calculate() {
		x += vectorX;
		y += vectorY;
		vectorX = 0;
		vectorY = 0;
	}
	
	public double getDistance(double pointX, double pointY) {
		return Math.sqrt(Math.pow(x - pointX, 2) + Math.pow(y - pointY, 2));
	}
	
	public boolean containsPoint(double pointX, double pointY) {
		return getDistance(pointX, pointY) <= getRadius();
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}

	public double getVectorX() {
		return vectorX;
	}

	public void setVectorX(double vectorX) {
		this.vectorX = vectorX;
	}

	public double getVectorY() {
		return vectorY;
	}

	public void setVectorY(double vectorY) {
		this.vectorY = vectorY;
	}
	
	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
}
