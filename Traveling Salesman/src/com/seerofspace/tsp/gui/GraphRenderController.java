package com.seerofspace.tsp.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.seerofspace.tsp.core.NearestNeighbor;
import com.seerofspace.tsp.graph.Edge;
import com.seerofspace.tsp.graph.Graph;
import com.seerofspace.tsp.graph.Node;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

public class GraphRenderController {
	
	@FXML private AnchorPane anchorPane;
	@FXML private Button loadButton;
	@FXML private Button generateButton;
	@FXML private Button nearestNeighborButton;
	@FXML private CheckBox pauseCheckBox;
	@FXML private VBox sideBar;
	private FileChooser fileChooser;
	private Graph<String, Integer, CircleNode, LineEdge> graph;
	private WorkThread wt;
	private List<CircleNode> previousPath;
	private List<Color> previousColors;
	private Group statisticsGroup;
	
	@FXML
	private void initialize() {
		CanvasPane canvasPane = new CanvasPane();
		canvasPane.setStyle("-fx-background-color: white");
		anchorPane.getChildren().add(canvasPane);
		AnchorPane.setBottomAnchor(canvasPane, 0.0);
		AnchorPane.setLeftAnchor(canvasPane, 0.0);
		AnchorPane.setRightAnchor(canvasPane, 0.0);
		AnchorPane.setTopAnchor(canvasPane, 0.0);
		
		CanvasPane topCanvas = new CanvasPane();
		anchorPane.getChildren().add(topCanvas);
		AnchorPane.setBottomAnchor(topCanvas, 0.0);
		AnchorPane.setLeftAnchor(topCanvas, 0.0);
		AnchorPane.setRightAnchor(topCanvas, 0.0);
		AnchorPane.setTopAnchor(topCanvas, 0.0);
		topCanvas.setMouseTransparent(true);
		
		statisticsGroup = new Group();
		sideBar.getChildren().add(statisticsGroup);
		
		wt = new WorkThread(canvasPane.getCanvas());
		graph = null;
		previousPath = null;
		previousColors = new ArrayList<>();
		
		fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text File", "*.txt"), new ExtensionFilter("All Files", "*.*"));
		loadButton.setOnAction(e -> {
			clearStatistics();
			loadButtonFunc();
		});
		
		nearestNeighborButton.setOnAction(e -> {
			nearestNeighborButtonFunc();
		});
		
		generateButton.setOnAction(e -> {
			clearStatistics();
			generateButtonFunc();
		});
		
		pauseCheckBox.setOnAction(e -> {
			if(pauseCheckBox.isSelected()) {
				wt.pause();
			} else {
				wt.unpause();
			}
		});
		
	}
	
	private synchronized void loadButtonFunc() {
		File file = fileChooser.showOpenDialog(loadButton.getScene().getWindow());
		if(file == null) {
			return;
		}
		File parent = file.getParentFile();
		if(parent != null) {
			fileChooser.setInitialDirectory(file.getParentFile());
		}
		graph = new Graph<>(new CircleNodeFactory(0, 0, 10, Color.BLACK), new LineEdgeFactory(2, Color.BLACK));
		loadFile(file, graph);
		reloadWt();
	}
	
	private synchronized void generateButtonFunc() {
		int maxWeight = 100;
		int maxNodes = 10;
		graph = new Graph<>(new CircleNodeFactory(0, 0, 10, Color.BLACK), new LineEdgeFactory(2, Color.BLACK));
		Random r = new Random();
		int num = r.ints(1, 3, maxNodes + 1).findFirst().getAsInt();
		for(int i = 0; i < num; i++) {
			if(i > 0) {
				graph.addEdgeUndirected(mapToName(i), mapToName(i - 1), r.ints(1, 1, maxWeight).findFirst().getAsInt());
			}
			int numEdges = r.ints(1, 1, num).findFirst().getAsInt();
			int[] edges = r.ints(numEdges, 1, num).toArray();;
			for(int j = 0; j < numEdges; j++) {
				if(i == edges[j]) {
					continue;
				}
				int weight = r.ints(1, 1, maxWeight).findFirst().getAsInt();
				graph.addEdgeDirected(mapToName(i), mapToName(edges[j]), weight);
			}
		}
		for(CircleNode circle : graph.getCollection()) {
			for(Edge<String, Integer> edge : circle.getAdjacentCollection()) {
				if(edge.getDestination().getEdge(circle.getId()) == null) {
					int weight = r.ints(1, 1, maxWeight).findFirst().getAsInt();
					graph.addEdgeDirected((CircleNode) edge.getDestination(), circle, weight);
				}
			}
		}
		reloadWt();
	}
	
	private synchronized void nearestNeighborButtonFunc() {
		if(graph == null || wt.getStartingNode() == null) {
			return;
		}
		revertLines();
		
		@SuppressWarnings("unchecked")
		List<CircleNode> path = (List<CircleNode>) (List<?>)NearestNeighbor.nearestNeighbor(
				(Graph<String, Integer, Node<String, Integer>, Edge<String, Integer>>) (Graph<?, ?, ?, ?>) graph,
				(Node<String, Integer>) wt.getStartingNode());
		
		if(path == null) {
			return;
		}
		previousPath = path;
		previousColors.clear();
		showStatistics();
		
		Thread thread = new Thread(() -> {
			synchronized(this) {
				for(int i = 0; i < path.size() - 1; i++) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					LineEdge line = (LineEdge) path.get(i).getEdge(path.get(i + 1).getId());
					previousColors.add(line.getColor());
					new MyTransition(line).play();
				}
			}
		});
		thread.start();
	}
	
	private class CanvasPane extends Pane {

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
	
	protected WorkThread getWorkThread() {
		return wt;
	}
	
	private void revertLines() {
		if(previousPath == null) {
			return;
		}
		for(int i = 0; i < previousPath.size() - 1; i++) {
			LineEdge line = (LineEdge) previousPath.get(i).getEdge(previousPath.get(i + 1).getId());
			line.setColor(previousColors.get(i));
			line.setThickness(2);
		}
	}
	
	private String mapToName(int num) {
		String s = "";
		while(num >= 0) {
			s += (char) (num % 26 + 'A');
			num -= 25;
		}
		return s;
	}
	
	private void reloadWt() {
		wt.loadGraph(graph);
		previousPath = null;
		wt.start();
	}
	
	private void showStatistics() {
		List<javafx.scene.Node> list = statisticsGroup.getChildren();
		clearStatistics();
		StringBuilder builder = new StringBuilder();
		builder.append("\n");
		builder.append("Total Length: " + getRouteLength(previousPath) + "\n");
		builder.append("\n");
		builder.append("Route: " + "\n");
		previousPath.forEach(e -> {
			
		});
		for(int i = 0; i < previousPath.size(); i++) {
			CircleNode c = previousPath.get(i);
			builder.append(c.getId());
			if(i > 0) {
				builder.append(": " + previousPath.get(i - 1).getEdge(c.getId()).getWeight());
			}
			builder.append("\n");
		}
		Label label = new Label();
		label.setText(builder.toString());
		list.add(label);
	}
	
	private void clearStatistics() {
		List<javafx.scene.Node> list = statisticsGroup.getChildren();
		list.removeAll(list);
	}
	
	private int getRouteLength(List<CircleNode> route) {
		int length = 0;
		for(int i = 0; i < route.size() - 1; i++) {
			length += route.get(i).getEdge(route.get(i + 1).getId()).getWeight();
		}
		return length;
	}
	
	private class MyTransition extends Transition {
		
		private LineEdge line;
		private Color color;
		private double thickness;
		
		public MyTransition(LineEdge line) {
			setCycleDuration(Duration.millis(200));
            setInterpolator(Interpolator.EASE_OUT);
			this.line = line;
			color = line.getColor();
			thickness = line.getThickness();
		}
		
		@Override
		protected void interpolate(double frac) {
			line.setColor(color.interpolate(Color.hsb(0, 1, 1, 1), frac));
			line.setThickness(thickness * (1 + frac));
			wt.draw();
		}
		
	}
	
}
