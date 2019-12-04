package com.seerofspace.tsp.gui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

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
		WorkThread wt = new WorkThread(canvasPane.getCanvas());
		wt.start();
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
	
}
