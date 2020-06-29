package com.lukkaw.shapes;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.lukkaw.arithmetics.Vector;

import lombok.ToString;

@ToString
public class Cylinder {
	public final Triangle[] triangles;

	public Cylinder(int h, int r, int n) {
		List<Vertex> topVertices = newArrayList();
		topVertices.add(new Vertex(new Vector(0, h, 0, 1), new Vector(0, 1, 0, 0)));
		for (int i = 0; i < n; i++) {
			topVertices.add(new Vertex(
					new Vector(r * cos(2 * PI * i / n), h, r * sin(2 * PI * i / n), 1),
					new Vector(0, 1, 0, 0)));
		}

		List<Vertex> bottomVertices = newArrayList();
		for (int i = 0; i < n; i++) {
			bottomVertices.add(new Vertex(
					new Vector(r * cos(2 * PI * i / n), 0, r * sin(2 * PI * i / n), 1),
					new Vector(0, -1, 0, 0)
			));
		}
		bottomVertices.add(new Vertex(new Vector(0, 0, 0, 1), new Vector(0, -1, 0, 0)));

		List<Vector> sidePs = newArrayList();
		sidePs.addAll(topVertices.subList(1, topVertices.size()).stream().map(v -> v.p).collect(toList()));
		sidePs.addAll(bottomVertices.subList(0, bottomVertices.size() - 1).stream().map(v -> v.p).collect(toList()));
		List<Vertex> sideVertices = newArrayList();
		for (var p : sidePs) {
			sideVertices.add(new Vertex(p, new Vector(p.raw[0] / r, 0, p.raw[2] / r, 0)));
		}

		List<Vertex> verticesList = newArrayList();
		verticesList.addAll(topVertices);
		verticesList.addAll(sideVertices);
		verticesList.addAll(bottomVertices);
		Vertex[] v = new Vertex[verticesList.size()];
		verticesList.toArray(v);

		List<Triangle> triangles = newArrayList();
		for (int i = 0; i < n - 1; i++) {
			triangles.add(new Triangle(v[0], v[i + 2], v[i + 1]));
		}
		triangles.add(new Triangle(v[0], v[1], v[n]));

		for (int i = n; i < 2 * n - 1; i++) {
			triangles.add(new Triangle(v[i + 1], v[i + 2], v[i + 1 + n]));
		}
		triangles.add(new Triangle(v[2 * n], v[n + 1], v[3 * n]));

		for (int i = 2 * n; i < 3 * n - 1; i++) {
			triangles.add(new Triangle(v[i + 1], v[i + 2 - n], v[i + 2]));
		}
		triangles.add(new Triangle(v[3 * n], v[n + 1], v[2 * n + 1]));

		for (int i = 3 * n; i < 4 * n - 1; i++) {
			triangles.add(new Triangle(v[4 * n + 1], v[i + 1], v[i + 2]));
		}
		triangles.add(new Triangle(v[4 * n + 1], v[4 * n], v[3 * n + 1]));

		this.triangles = new Triangle[triangles.size()];
		triangles.toArray(this.triangles);
	}
}
