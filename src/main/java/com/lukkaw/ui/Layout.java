package com.lukkaw.ui;

import com.lukkaw.Config;
import com.lukkaw.controller.Controller;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Layout {

	private Config config;
	private Controller controller;
	private Stage stage;

	public Parent createUI() {
		BorderPane borderPane = new BorderPane();
		borderPane.setStyle("-fx-background-color: #f0f0f0;");

		AppToolbar toolbar = new AppToolbar(controller, stage);
		DrawingCanvas drawingCanvas = new DrawingCanvas(config, controller);
		DrawableControl drawableControl = new DrawableControl(controller);

		borderPane.setTop(toolbar.createUI());
		borderPane.setCenter(drawingCanvas.createUI());
		borderPane.setRight(drawableControl.createUI());
		return borderPane;
	}
}
