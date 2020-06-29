package com.lukkaw.ui;

import com.lukkaw.controller.Controller;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Layout {

	private Controller controller;
	private Stage stage;

	public Parent createUI() {
		BorderPane borderPane = new BorderPane();
		borderPane.setStyle("-fx-background-color: #f0f0f0;");

		DrawingCanvas drawingCanvas = new DrawingCanvas(controller);

		borderPane.setCenter(drawingCanvas.createUI());
		return borderPane;
	}
}
