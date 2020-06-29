package com.lukkaw.algorithms;

import static java.lang.Math.max;
import static java.lang.Math.pow;

import com.lukkaw.arithmetics.Vector;
import com.lukkaw.image.Color;

public class PhongaIlluminstion {
	private static Color call(Vector p, Vector n, Vector pc, Vector pi, Vector Ip, Vector ka, Vector kd, Vector ks,
			Vector Ia, int m) {
		var I = Ia.multiply(ka);

		final var tmp = pi.subtract(p);
		final var l = tmp.divide(tmp.length());
		I = I.add(kd.multiply(Ip).multiply(max(0, n.dot(l))));

		final var r = n.multiply(2 * n.dot(l)).subtract(l);
		final var vTmp = pc.subtract(p);
		final var v = vTmp.divide(vTmp.length());
		I = I.add(ks.multiply(Ip).multiply(pow(max(0, v.dot(r)), m)));

		return new Color((int) (I.raw[0] * 255), (int) (I.raw[1] * 255), (int) (I.raw[2] * 255));
	}
}
