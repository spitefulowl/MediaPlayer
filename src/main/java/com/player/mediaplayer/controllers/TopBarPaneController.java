package com.player.mediaplayer.controllers;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class TopBarPaneController implements Initializable {
    public Button minimizeButton;
    public Button maximizeButton;
    public Button closeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeButtonsIcons();
        initializeButtonActions();
    }

    private void initializeButtonsIcons() {
        minimizeButton.setGraphic(new FontIcon());
        maximizeButton.setGraphic(new FontIcon());
        closeButton.setGraphic(new FontIcon());
    }

    private void initializeButtonActions() {
        minimizeButton.setOnMouseClicked(mouseEvent -> {
            ((Stage)((Node) mouseEvent.getSource()).getScene().getWindow()).setIconified(true);
        });
        maximizeButton.setOnMouseClicked(mouseEvent -> {
            ((Stage)((Node) mouseEvent.getSource()).getScene().getWindow()).setMaximized(true);
        });
        closeButton.setOnMouseClicked(mouseEvent -> {
            Platform.exit();
        });
    }
}
