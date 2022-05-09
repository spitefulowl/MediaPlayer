package com.player.mediaplayer;

import com.player.mediaplayer.controllers.ContentPaneController;
import com.player.mediaplayer.controllers.ControlPaneController;
import com.player.mediaplayer.models.Player;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MainPane.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setScene(scene);
        stage.setOnHidden(e -> {
            if (PlayerContext.globalTimer != null) {
                PlayerContext.globalTimer.cancel();
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
