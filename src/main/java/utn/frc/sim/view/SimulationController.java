package utn.frc.sim.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utn.frc.sim.simulation.SimulationFinishedException;
import utn.frc.sim.simulation.SimulationWrapper;
import utn.frc.sim.util.Fila;
import utn.frc.sim.view.components.RowOfSimulation;

public class SimulationController {

    private static final Logger logger = LogManager.getLogger(SimulationController.class);
    private static final int MAX_SIMULATION = 30;
    private SimulationWrapper simulation;
    private ObservableList<Fila> data;

    @FXML
    private TableView<RowOfSimulation> tableView;

    @FXML
    private TableColumn<RowOfSimulation, String> colClock;

    @FXML
    private TableColumn<RowOfSimulation, String> colEvent;

    @FXML
    private TableColumn<RowOfSimulation, String> colClientOfEvent;

    @FXML
    private TableColumn<RowOfSimulation, String> colCarpetState;

    @FXML
    private TableColumn<RowOfSimulation, String> colCarpetClient;

    @FXML
    private TableColumn<RowOfSimulation, String> colCarpetNextEnd;

    @FXML
    private TableColumn<RowOfSimulation, String> colCarpetQueue;

    @FXML
    private TableColumn<RowOfSimulation, String> colNextClient;


    @FXML
    public void initialize(){
        initializeColumns();
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
    void runClick(ActionEvent event) {
        runSimulation();
    }

    private void runSimulation() {
        tableView.getItems().clear();
        SimulationWrapper simulationWrapper = SimulationWrapper.ofType();

        while(true){
            try {
                simulationWrapper.step();
            } catch (SimulationFinishedException e) {
                break;
            }
            tableView.getItems().add(new RowOfSimulation(simulationWrapper));
        }
    }
}