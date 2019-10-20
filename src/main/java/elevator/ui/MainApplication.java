package elevator.ui;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import elevator.model.Elevator;
import elevator.model.Floor;
import elevator.model.Passenger;
import elevator.simulation.ElevatorSimulator;
import elevator.simulation.SimulationParameters;
import elevator.strategies.elevator.NearestHeadingTowardsElevatorStrategy;
import elevator.strategies.embarkation.SimpleEmbarkationStrategy;
import elevator.strategies.queue.ByOrderFloorQueueMergeStrategy;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainApplication extends Application {
	
	
	public static void main(String[] args) {
        launch(args);
    }

	private TextField numElevators;
	private TextField numFloors;
	private TextField seed;
	private Button runButton;
	private Button resetButton;
	private Canvas canvas;
	private TextField numPassengers;

	private Thread simulationThread;
	private ElevatorSimulator simulator;
	private SimulationParameters params;
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Elevator simulator");
		AnchorPane anchorPane = new AnchorPane();
		
		canvas = new Canvas(1024, 768);
		anchorPane.getChildren().add(canvas);

		VBox vbox = new VBox();
		anchorPane.getChildren().add(vbox);
		AnchorPane.setRightAnchor(vbox, 10d);
		AnchorPane.setTopAnchor(vbox, 10d);
		
		numElevators = new TextField();
		numElevators.setText("3");
		
		numFloors = new TextField();
		numFloors.setText("10");
		
		numPassengers = new TextField();
		numPassengers.setText("20");
		
		seed = new TextField();
		seed.setText(String.valueOf(System.currentTimeMillis()));
		
		setIntegerOnly(numElevators);
		setIntegerOnly(numFloors);
		setIntegerOnly(numPassengers);
		setIntegerOnly(seed);
		
		addLabelAndControl(vbox, numElevators, "Number of elevators");
		addLabelAndControl(vbox, numFloors, "Number of floors");
		addLabelAndControl(vbox, numPassengers, "Number of passengers");
		addLabelAndControl(vbox, seed, "Random seed");
		
		runButton = new Button();
		runButton.setText("Run");
		runButton.setOnMouseClicked(this::runButtonClicked);
		
		vbox.getChildren().add(runButton);
		
		resetButton = new Button();
		resetButton.setText("Stop");
		resetButton.setOnMouseClicked(this::resetButtonClicked);
		resetButton.setDisable(true);
		
		vbox.getChildren().add(resetButton);
		
		
		Scene s = new Scene(anchorPane, 1024, 768, Color.WHITE);
		primaryStage.setScene(s);
        primaryStage.show();
        
        Thread redrawThread = new Thread(() -> {
        	while (true) {
        		try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
        		Platform.runLater(this::redraw);
        	}
        });
        redrawThread.setDaemon(true);
        redrawThread.start();
	}
	
	private void setIntegerOnly(TextField textField) {
		textField.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		            textField.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});		
	}

	private void resetButtonClicked(MouseEvent e) {
		if (isRunning()) {
			pause();
		}
		resetButton.setDisable(true);
	}
	
	private void runButtonClicked(MouseEvent e) {
		if (isRunning()) {
			pause();
			runButton.setText("Resume");
		} else {
			run();
			resetButton.setDisable(false);
			runButton.setText("Pause");
		}
	}
	
	private boolean isRunning() {
		return simulationThread != null && simulationThread.isAlive();
	}

	private void pause() {
		if (simulationThread != null && simulationThread.isAlive()) {
			simulationThread.interrupt();
		}
		if (simulator != null ) {
			simulator.stop();
		}
	}

	private void run() {

		params = new SimulationParameters()
			.withNumberOfPassengers(Integer.parseInt(numPassengers.getText()))
			.withDelayBetweenPassengers(rand -> 
				Duration.ofSeconds(uniformDistribution(rand, 5, 10)))
			.withNumberOfElevators(Integer.parseInt(numElevators.getText()))
			.withNumberOfFloors(Integer.parseInt(numFloors.getText()))
			.withSeed(Long.parseLong(seed.getText()))
			.withDebugOutput(false)
			.withElevatorSelectionStrategy(new NearestHeadingTowardsElevatorStrategy())
			.withEmbarkationStrategy(new SimpleEmbarkationStrategy())
			.withFloorQueueMergeStrategy(new ByOrderFloorQueueMergeStrategy())
			.withTimeFactor(2)
			.withTimeout(Duration.ofMinutes(10));
		
		simulationThread = new Thread(() -> runSimulation(params), "SimulationThread");
		simulationThread.start();
		
	}

	private void runSimulation(SimulationParameters params) {
		simulator = new ElevatorSimulator(params);			
		
		try {
			simulator.run();
		} catch (Exception e) {
			System.out.println(e);
		}
		
		Platform.runLater(() -> {
			resetButton.setDisable(true);
			runButton.setText("Run");
		});
	}

	private static int uniformDistribution(Random random, int start, int end) {
		return start + random.nextInt(end - start);
	}
	
	private void addLabelAndControl(Pane parent, Node control, String labelText) {
		Label l = new Label();
		l.setText(labelText);
		parent.getChildren().add(l);
		
		parent.getChildren().add(control);
	}
	
	
	private void redraw() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		if (!isRunning() || simulator == null) {
			return;
		}
		
		List<Elevator> elevators = simulator.getEnvironment().getElevators();
		List<Floor> floors = simulator.getEnvironment().getFloors();
		
		final double topPadding = 50;
		final double leftPadding = 10;
		final double rightPadding = 200;
		final double passengerPadding = 200;
		final double elevatorLeftPadding = 150;
		final double elevatorRightPadding = rightPadding + passengerPadding;
		final double bottomPadding = 10;
		
		double floorHeight = (canvas.getHeight() - topPadding - bottomPadding) / floors.size();
		double elevatorShaftWidth = (canvas.getWidth() - elevatorLeftPadding - elevatorRightPadding) / elevators.size();
		if (elevatorShaftWidth > floorHeight / 1.5) {
			elevatorShaftWidth = floorHeight / 1.5;
		}
				
		//
		// Draw ceilings and floors and floor numbers
		//
		gc.setStroke(Color.BLACK);
		for (int i = floors.size(); i >= 0; --i) {
			double y = topPadding + (floors.size() - i) * floorHeight;
			gc.strokeLine(leftPadding, y, canvas.getWidth() - rightPadding, y);
			
			if (i < floors.size()) {
				Floor floor = floors.get(i);
				gc.fillText(floor.getName(), 20, y - floorHeight / 2 + getTextHeight(floor.getName(), gc.getFont()) / 2);

				// Draw passengers
				double headWidth = 20;
				double eyeWidth = 4;
				double eyeXOffset = 2;

				double x = elevatorLeftPadding + elevators.size() * elevatorShaftWidth + 5;
				for (Passenger passenger : floor.getPassengers()) {
					
					double headY = y - headWidth - 2;
					gc.strokeOval(x, headY, headWidth, headWidth);
					double eyeY = headY + headWidth / 2 - 4;
					gc.strokeOval(x + headWidth / 2 - eyeXOffset - eyeWidth, eyeY, eyeWidth, eyeWidth);
					gc.strokeOval(x + headWidth / 2 + eyeXOffset,            eyeY, eyeWidth, eyeWidth);
					
					// Draw destination floor above the head
					String destText = String.valueOf(passenger.getDestinationFloor()); 
					gc.fillText(
						destText, 
						x + headWidth / 2 - getTextWidth(destText, gc.getFont()) / 2, 
						headY - 4);
				
					x += headWidth * 2 + 2;
				}
			}
			
		}
		
		//
		// Draw elevator shafts
		//
		for (int i=0; i <= elevators.size(); ++i) {
			
			double x = elevatorLeftPadding + i * elevatorShaftWidth;
			gc.strokeLine(x, topPadding, x, canvas.getHeight() - bottomPadding);

			if (i < elevators.size()) {
				Elevator elevator = elevators.get(i);
				// Elevator name
				gc.fillText(elevator.getName(), x + elevatorShaftWidth / 2 - getTextWidth(elevator.getName(), gc.getFont()) / 2, 20);
				
				// Draw elevator
				gc.setFill(Color.BLUE);
				double y = topPadding + (floors.size() - elevator.getCurrentFloor() - 1) * floorHeight;
				gc.fillRect(
					x + 2, 
					y + 2, 
					elevatorShaftWidth - 4, 
					floorHeight - 4);
				
				// Draw number of passengers
				gc.setFill(Color.WHITE);
				String passText = "#P: " + elevator.getNumberOfPassengers();
				gc.fillText(passText, 
					x + elevatorShaftWidth / 2 - getTextWidth(passText, gc.getFont()) / 2, 
					y + 2 + getTextHeight(passText, gc.getFont()));

				// Draw destination floor
				if (elevator.getDestinationFloor() != -1) {
					String destText = "Dst: " + elevator.getDestinationFloor();
					gc.fillText(destText, 
						x + elevatorShaftWidth / 2 - getTextWidth(passText, gc.getFont()) / 2, 
						y + floorHeight - 4);
				}
				
				gc.setFill(Color.BLACK);
			}
			
		}
	}

	private double getTextHeight(String text, Font font) {
		Text _text = new Text();
		_text.setText(text);
		_text.setFont(font);
		return _text.getBoundsInLocal().getHeight();
	}

	private double getTextWidth(String text, Font font) {
		Text _text = new Text();
		_text.setText(text);
		_text.setFont(font);
		return _text.getBoundsInLocal().getWidth();
	}

}
