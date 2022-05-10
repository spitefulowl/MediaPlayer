module com.player.mediaplayer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires mp3agic;
    requires javafx.media;
    requires org.kordamp.ikonli.materialdesign2;

    opens com.player.mediaplayer to javafx.fxml;
    opens com.player.mediaplayer.models;
    exports com.player.mediaplayer;
    exports com.player.mediaplayer.controllers;
    opens com.player.mediaplayer.controllers to javafx.fxml;
}