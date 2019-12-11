package com.seerofspace.tsp.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GraphRenderDriver extends Application {
	
	public static final String VERSION = "1.1";
	private GraphRenderController controller;
	
	public static void main(String[] args) {
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("GraphRender.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Graph Render");
		primaryStage.show();
		controller = (GraphRenderController) loader.getController();
		primaryStage.setMaximized(true);
	}
	
	@Override
	public void stop(){
		controller.getWorkThread().stop();
	}
	
}
