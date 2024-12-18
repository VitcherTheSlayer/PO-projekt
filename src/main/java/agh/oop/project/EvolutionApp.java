package agh.oop.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class EvolutionApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("testWindow.fxml"));
        AnchorPane rootPane = fxmlLoader.load();

        Scene scene = new Scene(rootPane);
        stage.setScene(scene);

        stage.show();
    }
}
