package com.seerofspace.tsp.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GraphRenderController {
	
	@FXML BorderPane root;
	@FXML SplitPane splitPane;
	private Stage stage;
	
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
		GraphicsContext gc = canvasPane.getGraphicsContext();
		//gc.setFill(Color.RED);
		Platform.runLater(() -> {
			//gc.fillRect(0, 0, canvasPane.getWidth(), canvasPane.getHeight());
		});
		canvasPane.widthProperty().addListener(e -> {
			//gc.fillRect(0, 0, canvasPane.getWidth(), canvasPane.getHeight());
		});
		canvasPane.heightProperty().addListener(e -> {
			//gc.fillRect(0, 0, canvasPane.getWidth(), canvasPane.getHeight());
		});
		/*
		new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(canvasPane.getWidth());
			}
		}).start();
		*/
		WorkThread wt = new WorkThread(canvasPane.canvas);
		wt.start();
	}
	
	private static class CanvasPane extends Pane {

	    final Canvas canvas;

	    CanvasPane() {
	        canvas = new Canvas();
	        getChildren().add(canvas);
	        canvas.widthProperty().bind(this.widthProperty());
	        canvas.heightProperty().bind(this.heightProperty());
	    }
	    
	    public GraphicsContext getGraphicsContext() {
	    	return canvas.getGraphicsContext2D();
	    }
	    
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
}
