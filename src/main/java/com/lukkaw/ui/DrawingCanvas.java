package com.lukkaw.ui;

import static com.lukkaw.Config.S_X;
import static com.lukkaw.Config.S_Y;

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

	public DrawingCanvas(Controller controller) {
		controller.addListener(this);

		imageView = new ImageView(new Canvas(S_X, S_Y, false).getImage());
		imageView.setFitHeight(S_Y);
		imageView.setFitWidth(S_X);
		imageView.setPreserveRatio(true);
	}

	@Override
	public void draw(Canvas image) {
		System.out.println("canvas received");
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
