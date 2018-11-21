package utn.frc.sim.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import utn.frc.sim.model.clients.Client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientViewerController {

    @FXML
    private TableView<Client> tableView;

    @FXML
    private TableColumn<Client, String> colClient;

    @FXML
    private TableColumn<Client, String> colState;

    @FXML
    private TableColumn<Client, String> colInTime;

    @FXML
    private TableColumn<Client, String> colServeTime;

    @FXML
    private TableColumn<Client, String> colOutTime;


    @FXML
    public void initialize(){
        initColumns();
    }

    private void initColumns() {
        colClient.setCellValueFactory(e -> new SimpleStringProperty((e.getValue().getClientNumberString())));
        colState.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getStateString()));
        colInTime.setCellValueFactory(e -> new SimpleStringProperty(getFormattedLocalDateTime(e.getValue().getInTime())));
        colServeTime.setCellValueFactory(e -> new SimpleStringProperty(getFormattedLocalDateTime(e.getValue().getServeTime())));
        colOutTime.setCellValueFactory(e -> new SimpleStringProperty(getFormattedLocalDateTime(e.getValue().getOutTime())));
    }

    public void addClientsToTable(List<Client> clients){
        tableView.getItems().clear();
        tableView.getItems().addAll(clients);
    }

    private String getFormattedLocalDateTime(LocalDateTime localDateTime){
        if(localDateTime == null){
            return "-";
        }
        return DateTimeFormatter.ofPattern("HH:mm:ss SSS").format(localDateTime);
    }
}
