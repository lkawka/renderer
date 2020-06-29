package com.lukkaw.algorithms;

import static com.lukkaw.Config.SCALING;
import static com.lukkaw.Config.S_X;
import static com.lukkaw.Config.S_Y;
import static com.lukkaw.Config.THETA;
import static java.lang.Math.tan;

import com.lukkaw.arithmetics.Matrix;
import com.lukkaw.arithmetics.Vector;

public class Projection {

	public static Vector call(Vector p) {
		final var move = new Vector(2, 1.5, 1, 0);

		final var fov = 1 / tan(THETA / 2);
		final var fNear = 0.1;
		final double fFar = 1000;

		final var M = Matrix.create4x4(
				S_X * fov / S_Y, 0, 0, 0,
				0, fov, 0, 0,
				0, 0, fFar / (fFar - fNear), 1,
				0, 0, (-fFar * fNear) / (fFar - fNear), 0);

		final var S = Matrix.create4x4(
				SCALING, 0, 0, 0,
				0, SCALING, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);

		final var tfmP = M.transform(p);
		if (p.raw[2] == 0) {
			return S.transform(tfmP.add(move).divide(2));
		}
		return S.transform(tfmP.divide(p.raw[2]).add(move).divide(2));
	}
}
