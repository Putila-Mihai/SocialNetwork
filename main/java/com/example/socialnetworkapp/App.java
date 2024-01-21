package com.example.socialnetworkapp;

import com.example.socialnetworkapp.controlers.LogIn;
import com.example.socialnetworkapp.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initView(primaryStage);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
//        FXMLLoader stageLoader = new FXMLLoader();
//        stageLoader.setLocation(getClass().getResource("/com/example/socialnetworkapp/LogIn.fxml"));
//        AnchorPane LogInLayout = stageLoader.load();
//        primaryStage.setScene(new Scene(LogInLayout, Color.WHITE));
//        primaryStage.setTitle("App");
//        //Image icon = new Image("/com.example.reteadesocializare/imgs/Soboclan.jpg");
//       // primaryStage.getIcons().add(icon);
//        LogIn logInController = stageLoader.getController();
        FXMLLoader stageLoader = new FXMLLoader();
        stageLoader.setLocation(getClass().getResource("/com/example/socialnetworkapp/LogIn.fxml"));
        AnchorPane LogInLayout = stageLoader.load();

        Scene scene = new Scene(LogInLayout, Color.WHITE);
        scene.getStylesheets().add(getClass().getResource("/com/example/socialnetworkapp/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Messenger");

        LogIn logInController = stageLoader.getController();
    }
}
