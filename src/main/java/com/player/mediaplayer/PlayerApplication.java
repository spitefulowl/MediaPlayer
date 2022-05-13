package com.player.mediaplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class PlayerApplication extends Application {
    private void loadFonts() {
        Font.loadFont("file:src/main/resources/com/player/mediaplayer/fonts/Proxima Nova Bold.otf", 18);
    }
    @Override
    public void start(Stage stage) throws IOException {
        loadFonts();
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
