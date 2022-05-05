package com.player.mediaplayer;

import com.player.mediaplayer.controllers.ContentPaneController;
import com.player.mediaplayer.controllers.ControlPaneController;
import com.player.mediaplayer.models.PlayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        final PlayList playList = new PlayList();
        final Callback<Class<?>, Object> controllerFactory = new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> classInstance) {
                if (classInstance == ContentPaneController.class) {
                    return new ContentPaneController(playList);
                } else if (classInstance == ControlPaneController.class) {
                    return new ControlPaneController(playList);
                } else
                    try {
                        return classInstance.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        return null;
                    }
            }
        };

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MainPane.fxml"));
        fxmlLoader.setControllerFactory(controllerFactory);
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
