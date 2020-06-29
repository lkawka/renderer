package com.lukkaw.arithmetics;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;

import lombok.Data;

@Data
public class Matrix {
	public double[][] raw;

	public Matrix(double[][] raw) {
		this.raw = Arrays.copyOf(raw, raw.length);
	}

	public static Matrix create4x4(double... vals) {
		checkArgument(vals.length == 16);

		final double[][] res = new double[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				res[i][j] = vals[i * 4 + j];
			}
		}
		return new Matrix(res);
	}

	public static Matrix eye(int n) {
		checkArgument(n > 0);

		final double[][] res = new double[n][n];
		for (int i = 0; i < 4; i++) {
			res[i][i] = 1;
		}
		return new Matrix(res);
	}

	public Matrix multiply(Matrix mul) {
		checkArgument(this.raw.length == mul.raw[0].length);
		checkArgument(this.raw[0].length == mul.raw.length);

		final int n = this.raw.length;
		final int m = this.raw[0].length;
		final double[][] res = new double[n][n];

		for (int k = 0; k < m; k++) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					res[i][j] += this.raw[i][k] * mul.raw[k][j];
				}
			}
		}
		return new Matrix(res);
	}

	public Vector transform(Vector vec) {
		checkArgument(this.raw.length == vec.raw.length);

		final int n = this.raw.length;
		final int m = this.raw[0].length;

		final double[] res = new double[n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				res[i] += this.raw[i][j] * vec.raw[j];
			}
		}

		return new Vector(res);
	}

	public Matrix transpose() {
		checkArgument(this.raw.length == this.raw[0].length);

		final int n = this.raw.length;
		final double[][] res = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				res[i][j] = this.raw[j][i];
			}
		}
		return new Matrix(res);
	}
}
