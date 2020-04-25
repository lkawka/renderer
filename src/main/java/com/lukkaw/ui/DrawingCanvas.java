package com.lukkaw.ui;

import com.lukkaw.Config;
import com.lukkaw.controller.Controller;
import com.lukkaw.controller.ImageListener;
import com.lukkaw.image.Canvas;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class DrawingCanvas implements ImageListener {

	private ImageView imageView;

	public DrawingCanvas(Config config, Controller controller) {
		controller.addListener(this);

		imageView = new ImageView(new Canvas(config, false).getImage());
		imageView.setFitHeight(config.getCanvasHeight());
		imageView.setFitWidth(config.getCanvasWidth());
		imageView.setPreserveRatio(true);

		imageView.setOnMouseClicked(controller::imageClicked);
	}

	@Override
	public void draw(Canvas image) {
		imageView.setImage(image.getImage());
	}

	public Node createUI() {
		Label label = new Label("Canvas");

		Group group = new Group(imageView);

		VBox box = new VBox();
		box.setSpacing(8);
		box.setPadding(new Insets(8));
		box.getChildren().addAll(label, group);

		return box;
	}

}
