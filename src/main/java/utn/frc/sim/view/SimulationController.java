package utn.frc.sim.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import utn.frc.sim.simulation.SimulationFinishedException;
import utn.frc.sim.simulation.SimulationWrapper;
import utn.frc.sim.util.Fila;
import utn.frc.sim.view.components.RowOfSimulation;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class SimulationController {

    private static final Logger logger = LogManager.getLogger(SimulationController.class);
    private SimulationWrapper simulation;
    private Optional<ClientViewerController> controller;
    private Stage dialog;

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
    private TableColumn<RowOfSimulation, String> colNextBreak;

    @FXML
    private TableColumn<RowOfSimulation, String> colNextClean;

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
    private Hyperlink linkShowClients;

    @FXML
    private Button btnRun;

    @FXML
    private Button btnStep;

    @FXML
    public void initialize() {
        initializeColumns();
        initializeController();
        startNewSimulation();
    }

    private void initializeController() {
        controller = Optional.empty();
    }

    private void initializeColumns() {
        colClock.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getClock()));
        colEvent.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getEvent()));
        colClientOfEvent.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getClientOfEvent()));
        colNextClient.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getNextClient()));
        colNextBreak.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getNextBreak()));
        colNextClean.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getNextClean()));
        colCarpetState.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getMagicCarpetState()));
        colCarpetClient.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getMagicCarpetClient()));
        colCarpetNextEnd.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getMagicCarpetNextEnd()));
        colCarpetQueue.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getMagicCarpetQueue()));
    }

    @FXML
    void linkShowClientsClick(ActionEvent event) {
        showClients();
    }

    private void showClients() {
        if (!controller.isPresent()) {
            controller = loadListView();
            controller.ifPresent(c -> c.addClientsToTable(simulation.getClients()));
        }
        dialog.requestFocus();
    }

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
            controller.ifPresent(c -> c.addClientsToTable(simulation.getClients()));
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
                controller.ifPresent(c -> c.addClientsToTable(simulation.getClients()));
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
        linkShowClients.setVisited(Boolean.FALSE);
    }

    private Optional<ClientViewerController> loadListView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/simulations/client-viewer.fxml"));
            openNewDialog(loader.load());
            return Optional.ofNullable(loader.getController());

        } catch (IOException e) {
            logger.error("Problem opening table view.", e);
            return Optional.empty();
        }
    }

    private void openNewDialog(Parent parent) {
        dialog = new Stage();
        Scene scene = new Scene(parent);
        dialog.setScene(scene);
        dialog.setResizable(Boolean.FALSE);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setOnCloseRequest((e)-> controller = Optional.empty());
        dialog.show();
    }
}