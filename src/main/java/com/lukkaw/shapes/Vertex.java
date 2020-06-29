package com.lukkaw.shapes;

import static com.google.common.base.Preconditions.checkArgument;

import com.lukkaw.arithmetics.Vector;

import lombok.ToString;

@ToString
public class Vertex {
	public final Vector p;
	public final Vector n;

	public Vertex(Vector p, Vector n) {
		checkArgument(p.raw.length == n.raw.length);
		this.p = p.copy();
		this.n = n.copy();
	}
}
