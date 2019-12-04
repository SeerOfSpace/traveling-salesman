package com.seerofspace.tsp.gui;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class WorkThread {
	
	private List<MyCircle> circleList;
	private Canvas canvas;
	private GraphicsContext gc;
	private MyCircle activeCircle = null;
	private static boolean stop;
	private Thread thread;
	private static Object lock;
	private MyCircle attractor;
	private Point2D.Double calcTemp;
	
	public DoubleProperty radius = new SimpleDoubleProperty();
	public DoubleProperty percent = new SimpleDoubleProperty();
	
	private static final double RADIUS = 100;
	private static final double PERCENT = 0.005;
	private static final int SLEEP = 0;
	private static final double GRID_SPACING = 20;
	private static final int GRID_COLUMNS = 85;
	private static final int AMOUNT = 2000;
	
	public WorkThread(Canvas canvas) {
		this.canvas = canvas;
		gc = canvas.getGraphicsContext2D();
		stop = false;
		lock = new Object();
		attractor = null;
		calcTemp = new Point2D.Double();
		setup();
		testStuff();
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
				while(!stop) {
					double radius = this.radius.get();
					double percent = this.percent.get();
					for(MyCircle c1 : circleList) {
						if(c1 != activeCircle) {
							for(MyCircle c2 : circleList) {
								if(c2 != c1) {
									if(orbit(c1, c2, RADIUS, PERCENT, 0)) {
										inactiveCount = 0;
									} else {
										inactiveCount++;
									}
								}
							}
							if(attractor != null) {
								attract(c1, attractor, 1000, 0.01, 100);
							}
							c1.calculate();
						}
					}
					draw();
					if(inactiveCount < circleList.size() * circleList.size() + 100 || activeCircle != null) {
						Thread.sleep(SLEEP);
					} else {
						synchronized(lock) {
							System.out.println("waiting");
							//lock.wait();
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
	
	private void testStuff() {
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
	
	public void start() {
		thread.start();
	}
	
	private MyCircle getCircleUnderMouse(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		for(MyCircle circle : circleList) {
			if(circle.containsPoint(x, y)) {
				return circle;
			}
		}
		return null;
	}
	
	private void draw() {
		Platform.runLater(() -> {
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			//gc.setFill(Color.BLACK);
			//gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			circleList.forEach(circle -> {
				gc.setFill(circle.getColor());
				gc.fillOval(circle.getX() - circle.getRadius(), circle.getY() - circle.getRadius(), circle.getRadius() * 2, circle.getRadius() * 2);
			});
			if(attractor != null) {
				gc.setFill(attractor.getColor());
				gc.fillOval(attractor.getX() - attractor.getRadius(), attractor.getY() - attractor.getRadius(), attractor.getRadius() * 2, attractor.getRadius() * 2);
			}
		});
	}
	
	private Point2D.Double baseCalc(MyCircle c1, MyCircle c2, double forceRadius, double percent, double innerRadius) {
		double deltaX = c1.getX() - c2.getX();
		double deltaY = c1.getY() - c2.getY();
		double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
		if(distance >= forceRadius || distance < innerRadius) {
			return null;
		}
		double amount = (forceRadius - distance) * percent + 0.01;
		double amountX = deltaX/distance * amount;
		double amountY = deltaY/distance * amount;
		calcTemp.setLocation(amountX, amountY);
		return calcTemp;
	}
	
	private boolean repel(MyCircle c1, MyCircle c2, double forceRadius, double percent, double innerRadius) {
		Point2D.Double point = baseCalc(c1, c2, forceRadius, percent, innerRadius);
		if(point == null) {
			return false;
		}
		c1.setVectorX(c1.getVectorX() + point.x);
		c1.setVectorY(c1.getVectorY() + point.y);
		return true;
	}
	
	private boolean attract(MyCircle c1, MyCircle c2, double forceRadius, double percent, double innerRadius) {
		Point2D.Double point = baseCalc(c1, c2, forceRadius, percent, innerRadius);
		if(point == null) {
			return false;
		}
		c1.setVectorX(c1.getVectorX() - point.x);
		c1.setVectorY(c1.getVectorY() - point.y);
		return true;
	}
	
	private boolean orbit(MyCircle c1, MyCircle c2, double forceRadius, double percent, double innerRadius) {
		Point2D.Double point = baseCalc(c1, c2, forceRadius, percent, innerRadius);
		if(point == null) {
			return false;
		}
		c1.setVectorX(c1.getVectorX() + -point.y);
		c1.setVectorY(c1.getVectorY() + point.x);
		return true;
	}
	
	public Point2D.Double calcEdgeLabelPosition(MyCircle c1, MyCircle c2, double distance) {
		double deltaX = c1.getX() - c2.getX();
		double deltaY = c1.getY() - c2.getY();
		double centerX = deltaX / 2 + c2.getX();
		double centerY = deltaY / 2 + c2.getY();
		double perpendicularSlope = -deltaX / deltaY;
		double height = centerY - centerX * perpendicularSlope;
		
		return null;
	}
	
	public static void stop() {
		stop = true;
		synchronized(lock) {
			lock.notify();
		}
	}
	
}
