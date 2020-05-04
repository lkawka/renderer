package com.lukkaw.image;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ImageUtils {
	private static final int VICINITY = 5;

	public static double distance(PointPair pair) {
		return sqrt(pow(pair.getPoint1().getX() - pair.getPoint2().getX(), 2) +
				pow(pair.getPoint1().getY() - pair.getPoint2().getY(), 2));
	}

	public static double distance(Point point1, Point point2) {
		return distance(new PointPair(point1, point2));
	}

	public static boolean inVicinity(Point point1, Point point2) {
		return inVicinity(new PointPair(point1, point2));
	}

	public static boolean inVicinity(PointPair pair) {
		return distance(pair) < VICINITY;
	}

	public static boolean inVicinity(Point center, int radius, Point point) {
		AtomicBoolean inVicinity = new AtomicBoolean(false);
		circlePoints(center, radius, circlePoint -> {
			if (inVicinity(circlePoint, point)) {
				inVicinity.set(true);
			}
		});
		return inVicinity.get();
	}

	public static Color lerp(Color color1, Color color2, double t) {
		return new Color((int) (color1.getR() * (1 - t) + color2.getR() * t),
				(int) (color1.getG() * (1 - t) + color2.getG() * t),
				(int) (color1.getB() * (1 - t) + color2.getB() * t));
	}

	public static int det(Point a, Point b, Point c) {
		return a.getX() * b.getY() - a.getX() * c.getY() - a.getY() * b.getX() + a.getY() * c.getX() + b.getX() * c
				.getY() - b.getY() * c.getX();
	}

	public static Optional<Point> linePoint(PointPair line, Point point) {
		List<Point> result = new ArrayList<>();
		linePoints(line, linePoint -> {
			if (inVicinity(new PointPair(linePoint, point))) {
				result.add(linePoint);
			}
		});
		return result.stream().findFirst();
	}

	public static void linePoints(PointPair line, Consumer<Point> consumer) {
		LineTransform lineTransform = new LineTransform(line);
		PointPair transformedLine = lineTransform.getLineInZone1();

		int x1 = transformedLine.getPoint1().getX();
		int y1 = transformedLine.getPoint1().getY();
		int x2 = transformedLine.getPoint2().getX();
		int y2 = transformedLine.getPoint2().getY();

		int dx = x2 - x1;
		int dy = y2 - y1;
		int d = 2 * dy - dx;
		int dE = 2 * dy;
		int dNE = 2 * (dy - dx);

		while (x1 < x2) {
			consumer.accept(lineTransform.convertToOriginalZone(new Point(x1, y1)));
			consumer.accept(lineTransform.convertToOriginalZone(new Point(x2, y2)));
			++x1;
			--x2;
			if (d < 0)
				d += dE;
			else {
				d += dNE;
				++y1;
				--y2;
			}
		}
	}

	public static void circlePoints(Point center, int radius, Consumer<Point> consumer) {
		int dE = 3;
		int dSE = 5 - 2 * radius;
		int d = 1 - radius;
		int i = 0;
		int j = radius;

		octanCirclePoints(center, i, j, consumer);
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
			octanCirclePoints(center, i, j, consumer);
		}
	}

	private static void octanCirclePoints(Point center, int x, int y, Consumer<Point> consumer) {
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
