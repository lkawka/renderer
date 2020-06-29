package com.lukkaw.controller;

import static com.google.common.collect.Lists.newArrayList;
import static com.lukkaw.Config.S_X;
import static com.lukkaw.Config.S_Y;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.lukkaw.algorithms.BackFaceCulling;
import com.lukkaw.algorithms.Projection;
import com.lukkaw.algorithms.Transform;
import com.lukkaw.arithmetics.Vector;
import com.lukkaw.image.Canvas;
import com.lukkaw.image.Color;
import com.lukkaw.image.ImageUtils;
import com.lukkaw.image.Point;
import com.lukkaw.shapes.Cylinder;

import lombok.Getter;

@Getter
public class Controller {

	private final Cylinder cylinder;
	private CameraMovement cameraMovement = new CameraMovement(0D, 0D, 0D, 3D);
	private Vector lightSource = new Vector(50, 0, 0);

	private final List<ImageListener> imageListeners = new ArrayList<>();

	public Controller(Cylinder cylinder) {
		this.cylinder = cylinder;
	}

	public void cameraMoved(CameraMovement cameraMovement) {
		this.cameraMovement = cameraMovement;
		draw();
	}

	public void init() {
		draw();
	}

	private void draw() {
		Canvas canvas = new Canvas(S_X, S_Y, false);
		for (int i = 0; i < cylinder.triangles.length; i++) {
			final var triangle = cylinder.triangles[i];
			final var tfmVertices = new Vector[3];
			for (int j = 0; j < 3; j++) {
				final var vertex = triangle.raw[j];
				tfmVertices[j] = Transform.call(vertex.p, this.cameraMovement.dAlpha, this.cameraMovement.dBeta,
						this.cameraMovement.dGamma, this.cameraMovement.dz);
			}
			if (BackFaceCulling.call(tfmVertices[0], tfmVertices[1], tfmVertices[2])) {
				final var p1 = Projection.call(tfmVertices[0]);
				final var p2 = Projection.call(tfmVertices[1]);
				final var p3 = Projection.call(tfmVertices[2]);

				Color c = Color.random();
				List<Point> points = newArrayList(p1, p2, p3).stream()
						.map(v -> {
							int x = max(0, min(S_X, (int) v.raw[0]));
							int y = max(0, min(S_Y, (int) v.raw[1]));
							return new Point(x, y);
						}).collect(toList());
				ImageUtils.acceptFillPoints(points, point -> canvas.drawPoint(point, c, 1));
			}
		}
		draw(canvas);
	}

	public void addListener(ImageListener listener) {
		this.imageListeners.add(listener);
	}

	private void draw(Canvas image) {
		imageListeners.forEach(listener -> listener.draw(image));
	}

}
