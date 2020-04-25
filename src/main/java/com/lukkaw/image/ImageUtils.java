package com.lukkaw.image;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
		return circlePoints(center, radius).stream().anyMatch(circlePoint -> inVicinity(circlePoint, point));
	}

	public static Color lerp(Color color1, Color color2, double t) {
		return new Color((int) (color1.getR() * (1 - t) + color2.getR() * t),
				(int) (color1.getB() * (1 - t) + color2.getB() * t),
				(int) (color1.getB() * (1 - t) + color2.getB() * t));
	}

	public static Optional<? extends Point> linePoint(PointPair line, Point point) {
		return linePoints(line).stream().filter(linePoint -> inVicinity(new PointPair(linePoint, point))).findFirst();
	}

	public static List<? extends Point> linePoints(PointPair line) {
		LineTransform lineTransform = new LineTransform(line);
		PointPair transformedLine = lineTransform.getLineInZone1();

		int x1 = transformedLine.getPoint1().getX();
		int y1 = transformedLine.getPoint1().getY();
		int x2 = transformedLine.getPoint2().getX();
		int y2 = transformedLine.getPoint2().getY();

		List<Point> zone1Points = new ArrayList<>();

		int dx = x2 - x1;
		int dy = y2 - y1;
		int d = 2 * dy - dx;
		int dE = 2 * dy;
		int dNE = 2 * (dy - dx);

		zone1Points.add(new Point(x1, y1));
		zone1Points.add(new Point(x2, y2));
		while (x1 < x2) {
			++x1;
			--x2;
			if (d < 0)
				d += dE;
			else {
				d += dNE;
				++y1;
				--y2;
			}
			zone1Points.add(new Point(x1, y1));
			zone1Points.add(new Point(x2, y2));
		}

		return lineTransform.convertToOriginalZone(zone1Points);
	}

	public static List<Point> circlePoints(Point center, int radius) {
		int dE = 3;
		int dSE = 5 - 2 * radius;
		int d = 1 - radius;
		int i = 0;
		int j = radius;

		List<Point> points = new ArrayList<>(octanCirclePoints(center, i, j));

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
			points.addAll(octanCirclePoints(center, i, j));
		}
		return points;
	}

	private static List<Point> octanCirclePoints(Point center, int x, int y) {
		List<Point> points = new ArrayList<>();
		points.add(new Point(center.getX() + x, center.getY() + y));
		points.add(new Point(center.getX() + x, center.getY() - y));
		points.add(new Point(center.getX() - x, center.getY() + y));
		points.add(new Point(center.getX() - x, center.getY() - y));
		points.add(new Point(center.getX() + y, center.getY() + x));
		points.add(new Point(center.getX() - y, center.getY() + x));
		points.add(new Point(center.getX() + y, center.getY() - x));
		points.add(new Point(center.getX() - y, center.getY() - x));
		return points;
	}
}
