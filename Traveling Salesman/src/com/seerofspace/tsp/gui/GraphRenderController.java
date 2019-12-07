package com.seerofspace.tsp.gui;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.seerofspace.tsp.graph.Edge;
import com.seerofspace.tsp.graph.Graph;
import com.seerofspace.tsp.graph.Node;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class GraphRenderController {
	
	@FXML BorderPane root;
	@FXML SplitPane splitPane;
	
	@FXML
	private void initialize() {
		CanvasPane canvasPane = new CanvasPane();
		canvasPane.setStyle("-fx-background-color: white");
		AnchorPane pane = (AnchorPane) splitPane.getItems().get(0);
		pane.getChildren().add(canvasPane);
		AnchorPane.setBottomAnchor(canvasPane, 0.0);
		AnchorPane.setLeftAnchor(canvasPane, 0.0);
		AnchorPane.setRightAnchor(canvasPane, 0.0);
		AnchorPane.setTopAnchor(canvasPane, 0.0);
		
		//testStuff(canvasPane.getCanvas());
		
		Graph<String, Integer, CircleNode, LineEdge> graph;
		graph = new Graph<>(new CircleNodeFactory(0, 0, 10, Color.BLACK), new LineEdgeFactory(1, Color.BLACK));
		/*
		CircleNode c1 = new CircleNode("A", 10);
		CircleNode c2 = new CircleNode("B", 10);
		graph.addEdgeUndirected(c1, c2, 10);
		*/
		loadFile(new File("src\\com\\seerofspace\\tsp\\core\\test.txt"), graph);
		
		WorkThread wt = new WorkThread(canvasPane.getCanvas(), graph);
		wt.start();
		
	}
	
	private void testStuff(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		double radius = 10;
		MyCircle c1 = new MyCircle(100, 100, radius);
		MyCircle c2 = new MyCircle(200, 200, radius);
		double[] arrowPointsX = {-6, 0, 6};
		double[] arrowPointsY = {15 + radius, 0 + radius, 15 + radius};
		Platform.runLater(() -> {
			double angle;
			Point2D.Double point = new Point2D.Double();
			gc.setLineWidth(2);
			gc.setTextAlign(TextAlignment.CENTER);
			
			gc.fillOval(c1.getX() - c1.getRadius(), c1.getY() - c1.getRadius(), c1.getRadius() * 2, c1.getRadius() * 2);
			gc.fillOval(c2.getX() - c2.getRadius(), c2.getY() - c2.getRadius(), c2.getRadius() * 2, c2.getRadius() * 2);
			
			gc.beginPath();
			calcPerpendicularPosition(c1, c2, 75, point);
			gc.bezierCurveTo(c1.getX(), c1.getY(), point.x, point.y, c2.getX(), c2.getY());
			gc.stroke();
			
			gc.beginPath();
			calcPerpendicularPosition(c1, c2, -75, point);
			gc.bezierCurveTo(c1.getX(), c1.getY(), point.x, point.y, c2.getX(), c2.getY());
			gc.stroke();
			
			calcPerpendicularPosition(c1, c2, 60, point);
			gc.fillText("10", point.x, point.y);
			angle = Math.atan2(c1.getY() - point.getY(), c1.getX() - point.getX());
			gc.save();
			gc.translate(c1.getX(), c1.getY());
			gc.rotate(Math.toDegrees(angle) + 90);
			gc.fillPolygon(arrowPointsX, arrowPointsY, 3);
			gc.restore();
			
			calcPerpendicularPosition(c1, c2, -60, point);
			gc.fillText("20", point.x, point.y);
			angle = Math.atan2(c2.getY() - point.getY(), c2.getX() - point.getX());
			gc.save();
			gc.translate(c2.getX(), c2.getY());
			gc.rotate(Math.toDegrees(angle) + 90);
			gc.fillPolygon(arrowPointsX, arrowPointsY, 3);
			gc.restore();
		});
	}
	
	private static class CanvasPane extends Pane {

	    private final Canvas canvas;

	    CanvasPane() {
	        canvas = new Canvas();
	        getChildren().add(canvas);
	        canvas.widthProperty().bind(this.widthProperty());
	        canvas.heightProperty().bind(this.heightProperty());
	    }
	    
	    public Canvas getCanvas() {
	    	return canvas;
	    }
	    
	}
	
	private void calcPerpendicularPosition(CircleInterface c1, CircleInterface c2, double distance, Point2D.Double result) {
		double deltaX = c1.getX() - c2.getX();
		double deltaY = c1.getY() - c2.getY();
		double centerX = deltaX / 2 + c2.getX();
		double centerY = deltaY / 2 + c2.getY();
		if(deltaY == 0.0) {
			result.setLocation(centerX, centerY + distance);
			return;
		}
		double perpendicularSlope = -deltaX / deltaY;
		double height = centerY - centerX * perpendicularSlope;
		double x = centerX + distance / Math.sqrt(1 + Math.pow(perpendicularSlope, 2));
		double y = x * perpendicularSlope + height;
		result.setLocation(x, y);
	}
	
	private void loadFile(File file, 
			Graph<String, Integer, CircleNode, LineEdge> graph) {
		
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
