package com.player.mediaplayer.controllers;

import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private class EffectUtilities {
        private AnchorPane pane;
        private double xOffset = 0;
        private double yOffset = 0;
        public EffectUtilities(AnchorPane pane) {
            this.pane = pane;
        }
        public EffectUtilities makeDraggable() {
            pane.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    pane.getScene().getWindow().setX(event.getScreenX() - xOffset);
                    pane.getScene().getWindow().setY(event.getScreenY() - yOffset);
                }
            });
            return this;
        }
    }
    public AnchorPane topBarPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // new EffectUtilities(topBarPane).makeDraggable();
    }
}
