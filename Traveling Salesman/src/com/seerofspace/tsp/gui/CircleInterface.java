package com.seerofspace.tsp.gui;

import javafx.scene.paint.Color;

public interface CircleInterface {
	
	public double getDistance(double pointX, double pointY);
	
	public boolean containsPoint(double pointX, double pointY);
	
	public double getX();
	
	public void setX(double x);
	
	public double getY();
	
	public void setY(double y);
	
	public double getRadius();

	public void setRadius(double radius);

	public Color getColor();

	public void setColor(Color color);
	
	public double getVectorX();

	public void setVectorX(double vectorX);

	public double getVectorY();

	public void setVectorY(double vectorY);
	
}
