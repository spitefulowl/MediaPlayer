package com.player.mediaplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/*
    To fix mp3agic legacy library, use these commands:

    jdeps --generate-module-info . mp3agic-0.9.1.jar
    javac --patch-module mp3agic=mp3agic-0.9.1.jar mp3agic/module-info.java
    jar uf mp3agic-0.9.1.jar -C mp3agic module-info.class
      
*/


public class PlayerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PlayerApplication.class.getResource("MainPane.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setScene(scene);
        stage.setOnHidden(e -> {
            if (PlayerContext.globalTimer != null) {
                PlayerContext.globalTimer.cancel();
            }
        });
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
