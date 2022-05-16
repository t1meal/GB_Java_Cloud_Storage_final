package ru.gb.storage.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.gb.storage.client.servises.NetworkController;

public class ClientFx extends Application {
    private final double WIDTH = 600;
    private final double HEIGHT = 600;


    public static void main(String[] args) {
       launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("DropBox");


        primaryStage.setOnCloseRequest(windowEvent -> {
            System.out.println("Application is closing!");
            primaryStage.close();
            System.exit(1);
        });

        primaryStage.show();
        new NetworkController().connect();
    }
}



