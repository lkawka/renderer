package com.lukkaw.algorithms;

import static com.google.common.base.Preconditions.checkArgument;

import com.lukkaw.arithmetics.Vector;

public class BackFaceCulling {
	public static boolean call(Vector v1, Vector v2, Vector v3) {
		checkArgument(v1.raw.length == v2.raw.length);
		checkArgument(v2.raw.length == v3.raw.length);

		final var e1 = v2.subtract(v1);
		final var e2 = v3.subtract(v1);

		var normal = new Vector(
				e1.raw[1] * e2.raw[2] - e1.raw[2] * e2.raw[1],
				e1.raw[2] * e2.raw[0] - e1.raw[0] * e2.raw[2],
				e1.raw[0] * e2.raw[1] - e1.raw[1] * e2.raw[0],
				0);
		normal = normal.divide(normal.length());

		final var sum = normal.raw[0] * v1.raw[0] +
				normal.raw[1] * v1.raw[1] +
				normal.raw[2] * v1.raw[2];

		return sum <= 0;
	}
}
