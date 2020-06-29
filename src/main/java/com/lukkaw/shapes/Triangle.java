package com.lukkaw.shapes;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;

import lombok.ToString;

@ToString
public class Triangle {
	public final Vertex[] raw;

	public Triangle(Vertex... vertices) {
		checkArgument(vertices.length == 3);
		this.raw = Arrays.copyOf(vertices, vertices.length);
	}
}
