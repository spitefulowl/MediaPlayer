package com.player.mediaplayer;

import com.player.mediaplayer.models.PlayerState;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
        stage.setScene(scene);
        stage.setOnHidden(e -> {
            if (PlayerContext.globalTimer != null) {
                PlayerContext.globalTimer.cancel();
            }
            PlayerContext.getInstance().getPlayer().saveState();
        });
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image("file:src/main/resources/com/player/mediaplayer/images/music_notes_icon.png"));
        PlayerContext.getInstance().getPlayer().applyState();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
