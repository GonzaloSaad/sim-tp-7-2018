package utn.frc.sim;

import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utn.frc.sim.simulation.SimulationFinishedException;
import utn.frc.sim.simulation.SimulationWrapper;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/simulations/simulation.fxml"));
        primaryStage.setTitle("Hello CIDS");
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(e -> forceClose());
        primaryStage.show();

    }

    private static void forceClose() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);

    }
}
