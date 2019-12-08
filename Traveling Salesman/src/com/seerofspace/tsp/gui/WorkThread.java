package com.seerofspace.tsp.gui;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.seerofspace.tsp.graph.Edge;
import com.seerofspace.tsp.graph.Graph;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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
	
	private static final double RADIUS = 500;
	private static final double PERCENT = 0.005;
	private static final int SLEEP = 1;
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
				Point2D.Double tempStorage = new Point2D.Double();
				Collection<CircleNode> collection = graph.getCollection();
				boolean movement;
				double radius;
				double percent;
				while(!stop) {
					radius = this.radius.get();
					percent = this.percent.get();
					movement = false;
					for(CircleNode c1 : collection) {
						if(c1 != activeCircle) {
							for(CircleNode c2 : collection) {
								if(c2 != c1) {
									repel(c1, c2, radius, percent, 0, tempStorage);
								}
							}
							for(Edge<String, Integer> edge : c1.getAdjacentCollection()) {
								CircleNode c2 = (CircleNode) edge.getDestination();
								attract(c1, c2, 1000, 0.1 * 1 / (edge.getWeight() + 40), 0, tempStorage);
							}
							if(attractor != null) {
								attract(c1, attractor, 1000, 0.01, 100, tempStorage);
							}
							movement = movement | c1.getVectorX() > 0.02;
							movement = movement | c1.getVectorY() > 0.02;
							c1.calculate();
						}
					}
					draw();
					if(movement || activeCircle != null || attractor != null) {
						Thread.sleep(SLEEP);
					} else {
						synchronized(lock) {
							System.out.println("waiting");
							lock.wait();
							System.out.println("waiting done");
						}
					}
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		});
		
	}
	
	@SuppressWarnings("unused")
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
				Color color = Color.hsb(360 / size * (i * squareSide + j), 1, 0.9, 1);
				circle.setColor(color);
				circle.getAdjacentCollection().forEach(edge -> {
					((LineEdge) edge).setColor(Color.hsb(color.getHue(), color.getSaturation(), color.getBrightness(), 0.6));
				});
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
			boolean undirected;
			double angle;
			Point2D.Double point = new Point2D.Double();
			double[] arrowPointsX = {-5, 0, 5};
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setFont(Font.font(20));
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			
			for(CircleNode circle : graph.getCollection()) {
				for(Edge<String, Integer> edge : circle.getAdjacentCollection()) {
					LineEdge line = (LineEdge) edge;
					CircleNode c = (CircleNode) line.getDestination();
					gc.setLineWidth(line.getThickness());
					gc.setFill(line.getColor());
					gc.setStroke(line.getColor());
					double[] arrowPointsY = {10 + c.getRadius(), 0 + c.getRadius(), 10 + c.getRadius()};
					
					if(c.containsDestination(circle)) {
						undirected = line.getWeight().equals(c.getEdge(circle.getId()).getWeight());
						if(undirected) {
							gc.strokeLine(circle.getX(), circle.getY(), c.getX(), c.getY());
							calcPerpendicularPosition(circle, c, 10, point);
						} else {
							int curveDistance = 75;
							int labelDistance = 40;
							if(c.drawn) {
								curveDistance *= -1;
								labelDistance *= -1;
							}
							gc.beginPath();
							calcPerpendicularPosition(circle, c, curveDistance, point);
							gc.bezierCurveTo(circle.getX(), circle.getY(), point.x, point.y, c.getX(), c.getY());
							gc.stroke();
							
							calcPerpendicularPosition(circle, c, curveDistance - 5, point);
							angle = Math.atan2(c.getY() - point.getY(), c.getX() - point.getX());
							gc.save();
							gc.translate(c.getX(), c.getY());
							gc.rotate(Math.toDegrees(angle) + 90);
							gc.fillPolygon(arrowPointsX, arrowPointsY, 3);
							gc.restore();
							calcPerpendicularPosition(circle, c, labelDistance, point);
						}
					} else {
						gc.strokeLine(circle.getX(), circle.getY(), c.getX(), c.getY());
						angle = Math.atan2(c.getY() - circle.getY(), c.getX() - circle.getX());
						gc.save();
						gc.translate(c.getX(), c.getY());
						gc.rotate(Math.toDegrees(angle) + 90);
						gc.fillPolygon(arrowPointsX, arrowPointsY, 3);
						gc.restore();
						calcPerpendicularPosition(circle, c, 10, point);
					}
					
					gc.fillText(line.getWeight().toString(), point.x, point.y);
					gc.setStroke(Color.BLACK);
					gc.setLineWidth(0.1);
					gc.strokeText(line.getWeight().toString(), point.x, point.y);
				}
				
				circle.drawn = true;
			}
			
			gc.setStroke(Color.BLACK);
			gc.setLineWidth(0.1);
			gc.setFont(Font.font(23));
			graph.getCollection().forEach(circle -> {
				gc.setFill(circle.getColor());
				gc.fillOval(circle.getX() - circle.getRadius(), circle.getY() - circle.getRadius(), circle.getRadius() * 2, circle.getRadius() * 2);
				gc.fillText(circle.getId(), circle.getX(), circle.getY() + 40);
				gc.strokeText(circle.getId(), circle.getX(), circle.getY() + 40);
				circle.drawn = false;
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
	
	@SuppressWarnings("unused")
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
