package com.lukkaw.algorithms;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import com.lukkaw.arithmetics.Matrix;
import com.lukkaw.arithmetics.Vector;

public class Transform {

	public static Vector call(Vector p, double dx, double dy, double dz, double z) {
		final var T = Matrix.eye(4);
		T.raw[3][2] = z;

		final var Rx = Matrix.create4x4(
				1, 0, 0, 0,
				0, cos(dx), sin(dx), 0,
				0, -sin(dx), cos(dx), 0,
				0, 0, 0, 1);

		final var Ry = Matrix.create4x4(
				cos(dy), 0, -sin(dy), 0,
				0, 1, 0, 0,
				sin(dy), 0, cos(dy), 0,
				0, 0, 0, 1);

		final var Rz = Matrix.create4x4(
				cos(dz), -sin(dz), 0, 0,
				sin(dz), cos(dz), 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);

		return Matrix.eye(4)
				.multiply(Ry)
				.multiply(Rx)
				.multiply(Rz)
				.multiply(T)
				.transpose()
				.transform(p);
	}
}
