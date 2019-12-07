package com.seerofspace.tsp.gui;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.seerofspace.tsp.graph.Graph;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class WorkThread {
	
	private Graph<String, Integer, CircleNode, LineEdge> graph;
	private Canvas canvas;
	private GraphicsContext gc;
	private CircleNode activeCircle;
	private static boolean stop;
	private Thread thread;
	private static Object lock;
	private MyCircle attractor;
	
	public DoubleProperty radius = new SimpleDoubleProperty();
	public DoubleProperty percent = new SimpleDoubleProperty();
	
	private static final double RADIUS = 200;
	private static final double PERCENT = 0.005;
	private static final int SLEEP = 10;
	private static final double GRID_SPACING = 30;
	private static final int GRID_COLUMNS = 85;
	private static final int AMOUNT = 2000;
	
	public WorkThread(Canvas canvas, Graph<String, Integer, CircleNode, LineEdge> graph) {
		this.canvas = canvas;
		this.graph = graph;
		gc = canvas.getGraphicsContext2D();
		stop = false;
		lock = new Object();
		activeCircle = null;
		attractor = null;
		radius = new SimpleDoubleProperty(RADIUS);
		percent = new SimpleDoubleProperty(PERCENT);
		setup();
		Platform.runLater(() -> {
			setupGrid();
		});
		//testStuff();
	}
	
	private void setup() {
		canvas.setOnMousePressed(e -> {
			activeCircle = getCircleUnderMouse(e);
			if(activeCircle != null) {
				activeCircle.setVectorX(0);
				activeCircle.setVectorY(0);
				
			} else {
				attractor = new MyCircle(e.getX(), e.getY(), 5, Color.BLUE);
			}
			synchronized(lock) {
				lock.notify();
			}
		});
		canvas.setOnMouseDragged(e -> {
			if(activeCircle != null) {
				activeCircle.setX(e.getX());
				activeCircle.setY(e.getY());
			} else {
				attractor.setX(e.getX());
				attractor.setY(e.getY());
			}
		});
		canvas.setOnMouseReleased(e -> {
			activeCircle = null;
			attractor = null;
		});
		
		thread = new Thread(() -> {
			try {
				draw();
				Thread.sleep(2000);
				double inactiveCount = 0;
				Point2D.Double tempStorage = new Point2D.Double();
				Collection<CircleNode> collection = graph.getCollection();
				while(!stop) {
					double radius = this.radius.get();
					double percent = this.percent.get();
					for(CircleNode c1 : collection) {
						if(c1 != activeCircle) {
							for(CircleNode c2 : collection) {
								if(c2 != c1) {
									if(repel(c1, c2, radius, percent, 0, tempStorage)) {
										inactiveCount = 0;
									} else {
										inactiveCount++;
									}
								}
							}
							if(attractor != null) {
								attract(c1, attractor, 1000, 0.01, 100, tempStorage);
							}
							c1.calculate();
						}
					}
					draw();
					if(inactiveCount < collection.size() * collection.size() + 100 || activeCircle != null || attractor != null) {
						Thread.sleep(SLEEP);
					} else {
						synchronized(lock) {
							System.out.println("waiting");
							lock.wait();
							System.out.println("waiting done");
							inactiveCount = 0;
						}
					}
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		});
		
	}
	
	private void testStuff(List<MyCircle> circleList) {
		circleList = new ArrayList<>();
		for(int i = 0; i < Math.ceil(AMOUNT / (double) GRID_COLUMNS); i++) {
			int num;
			for(int j = 0; j < GRID_COLUMNS && (num = i * GRID_COLUMNS + j) < AMOUNT; j++) {
				Color color = Color.hsb(360 / (double) AMOUNT * num, 1, 1, 0.25);
				MyCircle circle = new MyCircle((j + 1) * GRID_SPACING, (i + 1) * GRID_SPACING, 10, color);
				circleList.add(circle);
			}
		}
	}
	
	private void setupGrid() {
		Iterator<CircleNode> iterator = graph.getIterator();
		int size = graph.getSize();
		int squareSide = (int) Math.ceil(Math.sqrt(size));
		double centerX = canvas.getWidth() / 2;
		double centerY = canvas.getHeight() / 2;
		
		for(int i = 0; i < squareSide; i++) {
			double y = ((double) i - (double) squareSide / 2.0) * GRID_SPACING + centerY;
			for(int j = 0; j < squareSide && iterator.hasNext(); j++) {
				CircleNode circle = iterator.next();
				double x = ((double) j - (double) squareSide / 2.0) * GRID_SPACING + centerX;
				circle.setX(x);
				circle.setY(y);
			}
		}
	}
	
	public void start() {
		thread.start();
	}
	
	private CircleNode getCircleUnderMouse(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		for(CircleNode circle : graph.getCollection()) {
			if(circle.containsPoint(x, y)) {
				return circle;
			}
		}
		return null;
	}
	
	private void draw() {
		Platform.runLater(() -> {
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			graph.getCollection().forEach(circle -> {
				gc.setFill(circle.getColor());
				gc.fillOval(circle.getX() - circle.getRadius(), circle.getY() - circle.getRadius(), circle.getRadius() * 2, circle.getRadius() * 2);
			});
			if(attractor != null) {
				gc.setFill(attractor.getColor());
				gc.fillOval(attractor.getX() - attractor.getRadius(), attractor.getY() - attractor.getRadius(), attractor.getRadius() * 2, attractor.getRadius() * 2);
			}
		});
	}
	
	private boolean baseCalc(CircleInterface c1, CircleInterface c2, double outerRadius,
			double percent, double innerRadius, Point2D.Double result) {
		
		double deltaX = c1.getX() - c2.getX();
		double deltaY = c1.getY() - c2.getY();
		double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
		if(distance >= outerRadius || distance < innerRadius) {
			return false;
		}
		double amount = (outerRadius - distance) * percent + 0.01;
		double amountX = deltaX/distance * amount;
		double amountY = deltaY/distance * amount;
		result.setLocation(amountX, amountY);
		return true;
	}
	
	private boolean repel(CircleInterface c1, CircleInterface c2, double outerRadius,
			double percent, double innerRadius, Point2D.Double tempStorage) {
		
		boolean success = baseCalc(c1, c2, outerRadius, percent, innerRadius, tempStorage);
		if(!success) {
			return false;
		}
		c1.setVectorX(c1.getVectorX() + tempStorage.x);
		c1.setVectorY(c1.getVectorY() + tempStorage.y);
		return true;
	}
	
	private boolean attract(CircleInterface c1, CircleInterface c2, double outerRadius,
			double percent, double innerRadius, Point2D.Double tempStorage) {
		
		boolean success = baseCalc(c1, c2, outerRadius, percent, innerRadius, tempStorage);
		if(!success) {
			return false;
		}
		c1.setVectorX(c1.getVectorX() - tempStorage.x);
		c1.setVectorY(c1.getVectorY() - tempStorage.y);
		return true;
	}
	
	private boolean orbit(CircleInterface c1, CircleInterface c2, double outerRadius,
			double percent, double innerRadius, Point2D.Double tempStorage) {
		
		boolean success = baseCalc(c1, c2, outerRadius, percent, innerRadius, tempStorage);
		if(!success) {
			return false;
		}
		c1.setVectorX(c1.getVectorX() + -tempStorage.y);
		c1.setVectorY(c1.getVectorY() + tempStorage.x);
		return true;
	}
	
	private void calcPerpendicularPosition(CircleInterface c1, CircleInterface c2, double distance, Point2D.Double result) {
		double deltaX = c1.getX() - c2.getX();
		double deltaY = c1.getY() - c2.getY();
		double centerX = deltaX / 2 + c2.getX();
		double centerY = deltaY / 2 + c2.getY();
		double perpendicularSlope = -deltaX / deltaY;
		double height = centerY - centerX * perpendicularSlope;
		double x = centerX + distance / Math.sqrt(1 + Math.pow(perpendicularSlope, 2));
		double y = x * perpendicularSlope + height;
		result.setLocation(x, y);
	}
	
	public static void stop() {
		stop = true;
		synchronized(lock) {
			lock.notify();
		}
	}
	
}
