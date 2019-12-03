package com.seerofspace.tsp.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GraphRender extends Application {
	
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
		GraphRenderController controller = (GraphRenderController) loader.getController();
		controller.setStage(primaryStage);
		primaryStage.setMaximized(true);
	}
	
	@Override
	public void stop(){
	    WorkThread.stop();
	}
	
}
