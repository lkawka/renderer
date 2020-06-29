package com.lukkaw.arithmetics;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.Arrays;

import lombok.ToString;

@ToString
public class Vector {
	public final double[] raw;

	public Vector(double... vals) {
		this.raw = Arrays.copyOf(vals, vals.length);
	}

	public Vector subtract(Vector vec) {
		checkArgument(this.raw.length == vec.raw.length);
		final var n = this.raw.length;
		final var res = new double[n];
		for (int i = 0; i < n; i++) {
			res[i] = this.raw[i] - vec.raw[i];
		}
		return new Vector(res);
	}

	public Vector divide(double val) {
		checkArgument(val != 0);

		final var n = this.raw.length;
		final var res = new double[n];
		for (int i = 0; i < n; i++) {
			res[i] = this.raw[i] / val;
		}
		return new Vector(res);
	}

	public double length() {
		double sum = 0;
		for (int i = 0; i < this.raw.length; i++) {
			sum += pow(this.raw[i], 2);
		}
		return sqrt(sum);
	}

	public Vector cross3(Vector vec) {
		checkArgument(this.raw.length == vec.raw.length);
		checkArgument(this.raw.length >= 3);

		final var res = new double[this.raw.length];
		res[0] = this.raw[1] * vec.raw[2] - this.raw[2] * vec.raw[1];
		res[1] = this.raw[2] * vec.raw[0] - this.raw[0] * vec.raw[2];
		res[2] = this.raw[0] * vec.raw[1] - this.raw[1] * vec.raw[0];
		return new Vector(res);
	}

	public double dot(Vector vec) {
		checkArgument(this.raw.length == vec.raw.length);

		final var n = this.raw.length;
		double res = 0;
		for (int i = 0; i < n; i++) {
			res += this.raw[i] * vec.raw[i];
		}
		return res;
	}

	public Vector multiply(Vector vec) {
		checkArgument(this.raw.length == vec.raw.length);
		checkArgument(this.raw.length >= 3);

		final var n = this.raw.length;
		final var res = new double[n];
		for (int i = 0; i < n; i++) {
			res[i] = this.raw[i] * vec.raw[i];
		}
		return new Vector(res);
	}

	public Vector add(Vector vec) {
		checkArgument(this.raw.length == vec.raw.length);
		checkArgument(this.raw.length >= 3);

		final var n = this.raw.length;
		final var res = new double[n];
		for (int i = 0; i < n; i++) {
			res[i] = this.raw[i] + vec.raw[i];
		}
		return new Vector(res);
	}

	public Vector multiply(double val) {
		checkArgument(val != 0);

		final var n = this.raw.length;
		final var res = new double[n];
		for (int i = 0; i < n; i++) {
			res[i] = this.raw[i] * val;
		}
		return new Vector(res);
	}

	public Vector add(double val) {
		final var n = this.raw.length;
		final var res = new double[n];
		for (int i = 0; i < n; i++) {
			res[i] = this.raw[i] + val;
		}
		return new Vector(res);
	}

	public Vector divide(Vector vec) {
		checkArgument(this.raw.length == vec.raw.length);
		final var n = this.raw.length;
		final var res = new double[n];
		for (int i = 0; i < n; i++) {
			checkArgument(vec.raw[i] != 0);
			res[i] = this.raw[i] / vec.raw[i];
		}
		return new Vector(res);
	}

	public Vector copy() {
		return new Vector(this.raw);
	}
}
