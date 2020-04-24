package com.lukkaw;

import com.lukkaw.controller.Controller;
import com.lukkaw.ui.Layout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    Config config = new Config();
    Controller controller = new Controller(config);

    @Override
    public void start(Stage stage) {
        Layout layout = new Layout(config, controller, stage);

        var scene = new Scene(layout.createUI(), config.getAppWidth(), config.getAppHeight());
        stage.setTitle("Renderer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}