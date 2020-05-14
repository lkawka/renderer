package com.lukkaw.algorithms;

import java.util.function.Consumer;

import com.lukkaw.image.Point;

public class AlternativeMidpointCircle {
	public static void accept(Point center, int radius, Consumer<Point> consumer) {
		int dE = 3;
		int dSE = 5 - 2 * radius;
		int d = 1 - radius;
		int i = 0;
		int j = radius;

		acceptOctanCirclePoints(center, i, j, consumer);
		while (j > i) {
			if (d < 0) {
				d += dE;
				dE += 2;
				dSE += 2;
			} else {
				d += dSE;
				dE += 2;
				dSE += 4;
				--j;
			}
			++i;
			acceptOctanCirclePoints(center, i, j, consumer);
		}
	}

	private static void acceptOctanCirclePoints(Point center, int x, int y, Consumer<Point> consumer) {
		consumer.accept(new Point(center.getX() + x, center.getY() + y));
		consumer.accept(new Point(center.getX() + x, center.getY() - y));
		consumer.accept(new Point(center.getX() - x, center.getY() + y));
		consumer.accept(new Point(center.getX() - x, center.getY() - y));
		consumer.accept(new Point(center.getX() + y, center.getY() + x));
		consumer.accept(new Point(center.getX() - y, center.getY() + x));
		consumer.accept(new Point(center.getX() + y, center.getY() - x));
		consumer.accept(new Point(center.getX() - y, center.getY() - x));
	}
}
