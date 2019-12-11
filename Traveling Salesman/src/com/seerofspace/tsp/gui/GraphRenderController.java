package com.seerofspace.tsp.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Supplier;

import com.seerofspace.tsp.core.BranchAndBound;
import com.seerofspace.tsp.core.MyException;
import com.seerofspace.tsp.core.NearestNeighbor;
import com.seerofspace.tsp.graph.Edge;
import com.seerofspace.tsp.graph.Graph;
import com.seerofspace.tsp.graph.Node;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

public class GraphRenderController {
	
	@FXML private AnchorPane anchorPane;
	@FXML private Button loadButton;
	@FXML private Button generateButton;
	@FXML private Button generateCompleteButton;
	@FXML private Button nearestNeighborButton;
	@FXML private Button branchAndBoundButton;
	@FXML private CheckBox pauseCheckBox;
	@FXML private VBox sideBar;
	@FXML private MenuItem exitMenuItem;
	@FXML private MenuItem aboutMenuItem;
	@FXML private MenuItem controlsMenuItem;
	private FileChooser fileChooser;
	private Graph<String, Integer, CircleNode, LineEdge> graph;
	private WorkThread wt;
	private List<CircleNode> previousPath;
	private List<Color> previousColors;
	private Group statisticsGroup;
	private Thread animationThread;
	
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
		animationThread = new Thread();
		
		fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text File", "*.txt"), new ExtensionFilter("All Files", "*.*"));
		loadButton.setOnAction(e -> {
			animationThread.interrupt();
			clearStatistics();
			loadButtonFunc();
		});
		
		nearestNeighborButton.setOnAction(e -> {
			animationThread.interrupt();
			synchronized(this) {
				revertLines();
				wt.draw();
			}
			clearStatistics();
			nearestNeighborButtonFunc();
		});
		
		branchAndBoundButton.setOnAction(e -> {
			animationThread.interrupt();
			synchronized(this) {
				revertLines();
				wt.draw();
			}
			clearStatistics();
			branchAndBoundButtonFunc();
		});
		
		generateButton.setOnAction(e -> {
			animationThread.interrupt();
			clearStatistics();
			pauseCheckBox.setSelected(false);
			generateRandomButtonFunc();
		});
		
		generateCompleteButton.setOnAction(e -> {
			animationThread.interrupt();
			clearStatistics();
			pauseCheckBox.setSelected(false);
			generateRandomCompleteButtonFunc();
		});
		
		pauseCheckBox.selectedProperty().addListener(e -> {
			if(pauseCheckBox.isSelected()) {
				wt.pause();
			} else {
				wt.unpause();
			}
		});
		
		exitMenuItem.setOnAction(e -> {
			Platform.exit();
		});
		
		aboutMenuItem.setOnAction(e -> {
			new Alert(AlertType.NONE, "version " + GraphRenderDriver.VERSION, ButtonType.CLOSE).showAndWait();
		});
		
		controlsMenuItem.setOnAction(e -> {
			String s = 
					"Click individual nodes to move them around" + "\n" + 
					"Click on an empty space to attract the nodes to the spot" + "\n" + 
					"Control or right click on individual nodes to mark them as the starting node" + "\n" +
					"Control or right click the node again to deselect them" + "\n" +
					"Use the pause button to move the nodes around without physics" + "\n" +
					"The side bar is resizable if it is too small" + "\n" +
					"In the side bar, additional statistics like total route length will show up when using either algorithm";
			new Alert(AlertType.NONE, s, ButtonType.CLOSE).showAndWait();
		});
		
		animateHelpMenu();
		
	}
	
	private void animateHelpMenu() {
		Menu helpMenu = controlsMenuItem.getParentMenu();
		String originalStyle = helpMenu.getStyle();		
		Transition transition = new Transition() {
			{
				this.setCycleDuration(Duration.millis(800));
				this.setAutoReverse(true);
				this.setCycleCount(4);
			}
			@Override
			protected void interpolate(double frac) {
				String s1 = String.format("%.100f", frac * 0.5);
				helpMenu.setStyle("-fx-background-color: rgba(255, 255, 0, " + s1 + ");");
			}
		};
		transition.setOnFinished(e -> {
			helpMenu.setStyle(originalStyle);
		});
		transition.play();
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
	
	private synchronized void generateRandomButtonFunc() {
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
				String name1 = mapToName(i);
				String name2 = mapToName(edges[j]);
				boolean directed = r.nextBoolean();
				int weight = r.ints(1, 1, maxWeight).findFirst().getAsInt();
				if(directed) {
					int weight2 = r.ints(1, 1, maxWeight).findFirst().getAsInt();
					graph.addEdgeDirected(name1, name2, weight);
					graph.addEdgeDirected(name2, name1, weight2);
				} else {
					graph.addEdgeUndirected(name1, name2, weight);
				}
			}
		}
		reloadWt();
	}
	
	private synchronized void generateRandomCompleteButtonFunc() {
		int maxWeight = 200;
		int maxNodes = 10;
		graph = new Graph<>(new CircleNodeFactory(0, 0, 10, Color.BLACK), new LineEdgeFactory(2, Color.BLACK));
		Random r = new Random();
		int num = r.ints(1, 3, maxNodes + 1).findFirst().getAsInt();
		for(int i = 0; i < num; i++) {
			for(int j = 0; j < num; j++) {
				if(i == j) {
					continue;
				}
				String name1 = mapToName(i);
				String name2 = mapToName(j);
				boolean directed = r.nextBoolean();
				int weight = r.ints(1, 1, maxWeight).findFirst().getAsInt();
				if(directed) {
					graph.addEdgeDirected(name1, name2, weight);
				} else {
					graph.addEdgeUndirected(name1, name2, weight);
				}
			}
		}
		reloadWt();
	}
	
	private synchronized void nearestNeighborButtonFunc() {
		Supplier<List<CircleNode>> supplier = new Supplier<List<CircleNode>>() {
			@Override
			public List<CircleNode> get() {
				try {
					@SuppressWarnings("unchecked")
					List<CircleNode> path = (List<CircleNode>) (List<?>)NearestNeighbor.nearestNeighbor(
							(Graph<String, Integer, Node<String, Integer>, Edge<String, Integer>>) (Graph<?, ?, ?, ?>) graph,
							(Node<String, Integer>) wt.getStartingNode());
					return path;
				} catch (MyException e) {
					new Alert(AlertType.ERROR, e.getMessage()).showAndWait();
					return null;
				}
			}
		};
		showAlgorithm(supplier);
	}
	
	private synchronized void branchAndBoundButtonFunc() {
		Supplier<List<CircleNode>> supplier = new Supplier<List<CircleNode>>() {
			@Override
			public List<CircleNode> get() {
				try {
					@SuppressWarnings("unchecked")
					List<CircleNode> path = (List<CircleNode>) (List<?>)BranchAndBound.branchAndBound(
							(Graph<String, Integer, Node<String, Integer>, Edge<String, Integer>>) (Graph<?, ?, ?, ?>) graph,
							(Node<String, Integer>) wt.getStartingNode());
					return path;
				} catch (MyException e) {
					new Alert(AlertType.ERROR, e.getMessage()).showAndWait();
					return null;
				}
			}
		};
		showAlgorithm(supplier);
	}
	
	private synchronized void showAlgorithm(Supplier<List<CircleNode>> supplier) {
		if(graph == null) {
			new Alert(AlertType.ERROR, "No graph created").showAndWait();
			return;
		}
		if(wt.getStartingNode() == null) {
			new Alert(AlertType.ERROR, "No starting node selected").showAndWait();
			return;
		}
		
		List<CircleNode> path = supplier.get();
		
		if(path == null) {
			return;
		}
		previousPath = path;
		previousColors.clear();
		showStatistics();
		
		animationThread = new Thread(() -> {
			synchronized(this) {
				List<MyTransition> transitionList = new ArrayList<>();
				try {
					for(int i = 0; i < path.size() - 1; i++) {
						Thread.sleep(500);
						if(Thread.interrupted()) {
							throw new InterruptedException();
						}
						LineEdge line = (LineEdge) path.get(i).getEdge(path.get(i + 1).getId());
						previousColors.add(line.getColor());
						
						MyTransition transition = new MyTransition(line);
						transitionList.add(transition);
						transition.play();
					}
				} catch(InterruptedException e1) {
					transitionList.forEach(e -> {
						e.stop();
					});
				}
			}
		});
		animationThread.start();
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
			Scanner scanner1 = new Scanner(file);
			Scanner scanner2;
			while(scanner1.hasNextLine()) {
				String line = scanner1.nextLine();
				if(line.charAt(0) == '#') {
					continue;
				}
				scanner2 = new Scanner(line);
				scanner2.useDelimiter(", ");
				String id1 = scanner2.next();
				String id2 = scanner2.next();
				int weight = scanner2.nextInt();
				boolean directed = scanner2.nextBoolean();
				if(directed) {
					graph.addEdgeDirected(id1, id2, weight);
				} else {
					graph.addEdgeUndirected(id1, id2, weight);
				}
				scanner2.close();
			}
			scanner1.close();
		} catch (FileNotFoundException | NoSuchElementException e) {
			e.printStackTrace();
			new Alert(AlertType.ERROR, e.getMessage()).showAndWait();
		}
	}
	
	protected WorkThread getWorkThread() {
		return wt;
	}
	
	private void revertLines() {
		if(previousPath == null) {
			return;
		}
		for(int i = 0; i < previousColors.size(); i++) {
			LineEdge line = (LineEdge) previousPath.get(i).getEdge(previousPath.get(i + 1).getId());
			line.setColor(previousColors.get(i));
			line.setThickness(2);
		}
	}
	
	private String mapToName(int num) {
		String s = "";
		while(num >= 0) {
			s += (char) (num % 26 + 'A');
			num -= 26;
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
			} else {
				builder.append(": " + (char) 11015);
			}
			builder.append("\n");
		}
		Label label = new Label();
		label.setText(builder.toString());
		label.setFont(Font.font(java.awt.Font.MONOSPACED, 16));
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
