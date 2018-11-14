package utn.frc.sim.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import utn.frc.sim.simulation.SimulationFinishedException;
import utn.frc.sim.simulation.SimulationWrapper;
import utn.frc.sim.util.Fila;
import utn.frc.sim.view.components.RowOfSimulation;

public class SimulationController {

    private static final Logger logger = LogManager.getLogger(SimulationController.class);
    private SimulationWrapper simulation;

    @FXML
    private TableView<RowOfSimulation> tableView;

    @FXML
    private TableColumn<RowOfSimulation, String> colClock;

    @FXML
    private TableColumn<RowOfSimulation, String> colEvent;

    @FXML
    private TableColumn<RowOfSimulation, String> colClientOfEvent;

    @FXML
    private TableColumn<RowOfSimulation, String> colNextClient;

    @FXML
    private TableColumn<RowOfSimulation, String> colCarpetState;

    @FXML
    private TableColumn<RowOfSimulation, String> colCarpetClient;

    @FXML
    private TableColumn<RowOfSimulation, String> colCarpetNextEnd;

    @FXML
    private TableColumn<RowOfSimulation, String> colCarpetQueue;

    @FXML
    private Label lblMaxDurationInQueue;

    @FXML
    private Label lblMaxAmountInQueue;

    @FXML
    public void initialize() {
        initializeColumns();
        startNewSimulation();
    }

    private void initializeColumns() {
        colClock.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getClock()));
        colEvent.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getEvent()));
        colClientOfEvent.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getClientOfEvent()));
        colNextClient.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getNextClient()));
        colCarpetState.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getMagicCarpetState()));
        colCarpetClient.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getMagicCarpetClient()));
        colCarpetNextEnd.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getMagicCarpetNextEnd()));
        colCarpetQueue.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getMagicCarpetQueue()));
    }


    @FXML
    private Button btnRun;

    @FXML
    private Button btnStep;

    @FXML
    void resetClick(ActionEvent event) {
        enableRunButtons();
        resetStatistics();
        startNewSimulation();
    }

    @FXML
    void runClick(ActionEvent event) {
        runSimulationToEnd();
    }

    @FXML
    void stepClick(ActionEvent event) {
        runSimulationOneStep();
    }

    private void runSimulationOneStep() {
        try {
            runOneStepOfSimulationAndAddToTable();
        } catch (SimulationFinishedException e) {
            finishSimulation();
        }
    }

    private void startNewSimulation() {
        logger.info("Starting new simulation.");
        tableView.getItems().clear();
        simulation = SimulationWrapper.ofType();
    }

    private void runSimulationToEnd() {
        while (true) {
            try {
                runOneStepOfSimulationAndAddToTable();
            } catch (SimulationFinishedException e) {
                finishSimulation();
                break;
            }
        }
    }

    private void runOneStepOfSimulationAndAddToTable() throws SimulationFinishedException {
        simulation.step();
        tableView.getItems().add(new RowOfSimulation(simulation));
    }

    private void finishSimulation() {
        logger.info("Simulation ended.");
        setStatistics();
        disableRunButtons();
    }

    private void disableRunButtons() {
        btnRun.setDisable(Boolean.TRUE);
        btnStep.setDisable(Boolean.TRUE);
    }

    private void resetStatistics() {
        setStatistics(Strings.EMPTY, Strings.EMPTY);
    }

    private void setStatistics() {
        setStatistics(simulation.getMaxDurationInQueue(), simulation.getMaxAmountInQueue());
    }

    private void setStatistics(String maxDurationInQueue, String maxAmountInQueue) {
        lblMaxDurationInQueue.setText(maxDurationInQueue);
        lblMaxAmountInQueue.setText(maxAmountInQueue);
    }

    private void enableRunButtons() {
        btnRun.setDisable(Boolean.FALSE);
        btnStep.setDisable(Boolean.FALSE);
    }
}