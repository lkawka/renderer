package com.lukkaw.image;

import static java.lang.Math.abs;
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

	public static Optional<Point> linePoint(PointPair line, Point point) {
		return linePoints(line).stream().filter(linePoint -> inVicinity(new PointPair(linePoint, point))).findFirst();
	}

	public static List<Point> linePoints(PointPair line) {
		List<Point> points = new ArrayList<>();

		if (line.getPoint1().getX() > line.getPoint2().getX()) {
			Point tmp = line.getPoint1();
			line.setPoint1(line.getPoint2());
			line.setPoint2(tmp);
		}

		int x1 = line.getPoint1().getX();
		int y1 = line.getPoint1().getY();
		int x2 = line.getPoint2().getX();
		int y2 = line.getPoint2().getY();

		int dx = x2 - x1;
		int dy = y2 - y1;

		points.add(new Point(x1, y1));
		points.add(new Point(x2, y2));

		if (dx >= abs(dy)) {
			if (dy >= 0) {
				points.addAll(zone1LinePoints(x1, y1, x2, y2, dx, dy));
			} else {
				points.addAll(zone8LinePoints(x1, y1, x2, y2, dx, dy));
			}
		} else {
			if (dy >= 0) {
				points.addAll(zone2LinePoints(x1, y1, x2, y2, dx, dy));
			} else {
				points.addAll(zone7LinePoints(x1, y1, x2, y2, dx, dy));
			}
		}

		return points;
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

	private static List<Point> zone1LinePoints(int x1, int y1, int x2, int y2, int dx, int dy) {
		List<Point> points = new ArrayList<>();
		int d = 2 * dy - dx;
		int dE = 2 * dy;
		int dNE = 2 * (dy - dx);

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
			points.add(new Point(x1, y1));
			points.add(new Point(x2, y2));
		}
		return points;
	}

	private static List<Point> zone8LinePoints(int x1, int y1, int x2, int y2, int dx, int dy) {
		List<Point> points = new ArrayList<>();
		int d = 2 * dy + dx;
		int dE = 2 * dy;
		int dSE = 2 * (dy + dx);

		while (x1 < x2) {
			++x1;
			--x2;
			if (d > 0) {
				d += dE;
			} else {
				--y1;
				++y2;
				d += dSE;

			}
			points.add(new Point(x1, y1));
			points.add(new Point(x2, y2));
		}
		return points;
	}

	private static List<Point> zone2LinePoints(int x1, int y1, int x2, int y2, int dx, int dy) {
		List<Point> points = new ArrayList<>();
		int d = dy - 2 * dx;
		int dN = -2 * dx;
		int dNE = 2 * dy - 2 * dx;

		while (y1 < y2) {
			y1++;
			y2--;
			if (d > 0) {
				d += dN;
			} else {
				x1++;
				x2--;
				d += dNE;
			}
			points.add(new Point(x1, y1));
			points.add(new Point(x2, y2));
		}
		return points;
	}

	private static List<Point> zone7LinePoints(int x1, int y1, int x2, int y2, int dx, int dy) {
		List<Point> points = new ArrayList<>();
		int d = dy + 2 * dx;
		int dS = 2 * dx;
		int dSE = 2 * dy + 2 * dx;

		while (y1 > y2) {
			y1--;
			y2++;
			if (d > 0) {
				x1++;
				x2--;
				d += dSE;
			} else {
				d += dS;
			}
			points.add(new Point(x1, y1));
			points.add(new Point(x2, y2));
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
