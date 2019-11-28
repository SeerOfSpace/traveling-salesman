package com.seerofspace.tsp.gui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GraphRenderController {
	
	@FXML BorderPane root;
	
	@FXML
	private void initialize() {
		CanvasPane canvasPane = new CanvasPane(600.0, 400.0);
		canvasPane.widthProperty();
		//canvasPane.setStyle("-fx-background-color: white");
		//root.setCenter(canvasPane);
		Pane pane = (Pane) root.getCenter();
		pane.getChildren().add(canvasPane);
		GraphicsContext gc = canvasPane.getGraphicsContext();
		gc.setFill(Color.RED);
		gc.fillRect(0, 0, canvasPane.getWidth(), canvasPane.getHeight());
		root.widthProperty().addListener(e -> {
			canvasPane.setMinWidth(root.getWidth());
			canvasPane.setMaxWidth(root.getWidth());
			System.out.println(canvasPane.getWidth());
			gc.fillRect(0, 0, canvasPane.getWidth(), canvasPane.getHeight());
		});
		root.heightProperty().addListener(e -> {
			canvasPane.setMinHeight(root.getHeight());
			canvasPane.setMaxHeight(root.getHeight());
			System.out.println(canvasPane.getHeight());
			gc.fillRect(0, 0, canvasPane.getWidth(), canvasPane.getHeight());
		});
	}
	
	private static class CanvasPane extends Pane {

	    final Canvas canvas;

	    CanvasPane(Double width, Double height) {
	    	this.setWidth(width);
	    	this.setHeight(height);
	    	//this.setPrefWidth(USE_COMPUTED_SIZE);
	    	//this.setPrefHeight(USE_COMPUTED_SIZE);
	    	//this.setMinWidth(1);
	    	//this.setMinHeight(1);
	        canvas = new Canvas(width, height);
	        getChildren().add(canvas);
	        
	        canvas.widthProperty().bind(this.widthProperty());
	        canvas.heightProperty().bind(this.heightProperty());
	    }
	    
	    public GraphicsContext getGraphicsContext() {
	    	return canvas.getGraphicsContext2D();
	    }
	    
	}
	
	public class ResizableCanvas extends Canvas {

	    @Override
	    public boolean isResizable() {
	        return true;
	    }

	    @Override
	    public double maxHeight(double width) {
	        return Double.POSITIVE_INFINITY;
	    }

	    @Override
	    public double maxWidth(double height) {
	        return Double.POSITIVE_INFINITY;
	    }

	    @Override
	    public double minWidth(double height) {
	        return 1D;
	    }

	    @Override
	    public double minHeight(double width) {
	        return 1D;
	    }

	    @Override
	    public void resize(double width, double height) {
	        this.setWidth(width);
	        this.setHeight(height);
	    }
	}
	
}
