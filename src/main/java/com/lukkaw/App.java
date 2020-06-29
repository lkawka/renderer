package com.lukkaw;

import static com.lukkaw.Config.D_ALPHA;
import static com.lukkaw.Config.D_Z;
import static com.lukkaw.Config.H;
import static com.lukkaw.Config.N;
import static com.lukkaw.Config.R;
import static com.lukkaw.Config.S_X;
import static com.lukkaw.Config.S_Y;

import com.lukkaw.arithmetics.Matrix;
import com.lukkaw.arithmetics.Vector;
import com.lukkaw.controller.Controller;
import com.lukkaw.shapes.Cylinder;
import com.lukkaw.ui.Layout;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

	Controller controller = new Controller(new Cylinder(H, R, N));

	public static void main(String[] args) {
		var m = new Matrix(new double[][] { new double[] { 1, 2 }, new double[] { 2, 3 } });
		System.out.println(m.transform(new Vector(1, 2)));
		launch(args);
	}

	@Override
	public void start(Stage stage) {

		Layout layout = new Layout(controller, stage);

		var scene = new Scene(layout.createUI(), S_X, S_Y);
		controller.init();
		scene.setOnKeyPressed(keyEvent -> {
			final var movement = controller.getCameraMovement();
			final var code = keyEvent.getCode();
			if (code == KeyCode.UP) {
				controller.cameraMoved(movement.dAlpha(D_ALPHA));
			} else if (code == KeyCode.DOWN) {
				controller.cameraMoved(movement.dAlpha(-D_ALPHA));
			} else if (code == KeyCode.RIGHT) {
				controller.cameraMoved(movement.dBeta(D_ALPHA));
			} else if (code == KeyCode.LEFT) {
				controller.cameraMoved(movement.dBeta(-D_ALPHA));
			} else if (code == KeyCode.E) {
				controller.cameraMoved(movement.dGamma(D_ALPHA));
			} else if (code == KeyCode.Q) {
				controller.cameraMoved(movement.dGamma(-D_ALPHA));
			} else if (code == KeyCode.W) {
				controller.cameraMoved(movement.dz(D_Z));
			} else if (code == KeyCode.S) {
				controller.cameraMoved(movement.dz(-D_Z));
			}
		});

		stage.setTitle("Renderer 3D");
		stage.setScene(scene);
		stage.show();
	}

}