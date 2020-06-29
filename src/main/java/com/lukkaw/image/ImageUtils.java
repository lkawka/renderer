package com.lukkaw.image;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.lukkaw.algorithms.AlternativeMidpointCircle;
import com.lukkaw.algorithms.CohenSutherland;
import com.lukkaw.algorithms.SnanLineActiveEdgeTableVertexSorting;
import com.lukkaw.algorithms.SymmetricMidpointLine;

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
		SymmetricMidpointLine.accept(line, consumer);
	}

	public static void acceptFillPoints(List<Point> vertices, Consumer<Point> consumer) {
		SnanLineActiveEdgeTableVertexSorting.accept(vertices, consumer);
		for (int i = 1; i < vertices.size(); i++) {
			acceptLinePoints(new PointPair(vertices.get(i - 1), vertices.get(i)), consumer);
		}
	}

	public static void acceptClippingPoints(PointPair line, PointPair rectangle, Consumer<Point> outsideConsumer,
			Consumer<Point> insideConsumer) {
		CohenSutherland.accept(line, rectangle, outsideConsumer, insideConsumer);
	}

	public static void acceptCirclePoints(Point center, int radius, Consumer<Point> consumer) {
		AlternativeMidpointCircle.accept(center, radius, consumer);
	}
}
