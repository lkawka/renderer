package com.lukkaw.image;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import lombok.Getter;

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
		acceptCirclePoints(center, radius, circlePoint -> {
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
		acceptLinePoints(line, linePoint -> {
			if (inVicinity(new PointPair(linePoint, point))) {
				result.add(linePoint);
			}
		});
		return result.stream().findFirst();
	}

	public static void acceptLinePoints(PointPair line, Consumer<Point> consumer) {
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

		while (x1 <= x2) {
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

	public static void acceptFillPoints(List<Point> vertices, Consumer<Point> consumer) {
		List<Integer> indices = IntStream.range(0, vertices.size())
				.boxed()
				.sorted(comparing(i -> vertices.get(i).getY()))
				.collect(toList());
		int index = 0;
		int vertexIndex = indices.get(index);
		int ymin = vertices.get(indices.get(0)).getY();
		int ymax = vertices.get(indices.get(vertices.size() - 1)).getY();
		int y = ymin;
		List<ActiveEdge> AET = new ArrayList<>();
		while (y < ymax) {
			while (vertices.get(vertexIndex).getY() == y) {
				if (vertexIndex != 0 && vertices.get(vertexIndex - 1).getY() > vertices.get(vertexIndex).getY()) {
					AET.add(new ActiveEdge(vertices.get(vertexIndex - 1), vertices.get(vertexIndex)));
				}
				if (vertexIndex != vertices.size() - 1 &&
						vertices.get(vertexIndex + 1).getY() > vertices.get(vertexIndex).getY()) {
					AET.add(new ActiveEdge(vertices.get(vertexIndex), vertices.get(vertexIndex + 1)));
				}
				index += 1;
				vertexIndex = indices.get(index);
			}
			AET.sort(comparing(pair -> min(pair.getPoint1().getX(), pair.getPoint2().getX())));
			for (int j = 1; j < AET.size(); j += 2) {
				acceptLinePoints(new PointPair(new Point(AET.get(j - 1).getPoint1().getX(), y),
						new Point(AET.get(j).getPoint1().getX(), y)), consumer);
			}
			y += 1;
			AET.removeIf(e -> max(e.getPoint1().getY(), e.getPoint2().getY()) == ymax);
			AET.forEach(ActiveEdge::step);
		}
	}

	public static void acceptClippingPoints(List<Point> polygon, PointPair rectangle, Consumer<Point> outsideConsumer,
			Consumer<Point> insideConsumer) {

	}

	@Getter
	private static class ActiveEdge extends PointPair {
		private Integer slope;

		public ActiveEdge(Point point1, Point point2) {
			super(point1, point2);
			int divider = point1.getX().equals(point2.getX()) ? 1 : point2.getX() - point1.getX();
			this.slope = (point2.getY() - point1.getY()) / divider;
		}

		public void step() {
			setPoint1(new Point(getPoint2().getX() + getSlope(), getPoint1().getY()));
		}
	}

	public static void acceptCirclePoints(Point center, int radius, Consumer<Point> consumer) {
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
