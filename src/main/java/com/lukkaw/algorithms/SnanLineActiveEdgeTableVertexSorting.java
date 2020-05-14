package com.lukkaw.algorithms;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import com.lukkaw.image.ImageUtils;
import com.lukkaw.image.Point;
import com.lukkaw.image.PointPair;

import lombok.Getter;

public class SnanLineActiveEdgeTableVertexSorting {

	public static void accept(List<Point> vertices, Consumer<Point> consumer) {
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
				ImageUtils.acceptLinePoints(new PointPair(new Point(AET.get(j - 1).getPoint1().getX(), y),
						new Point(AET.get(j).getPoint1().getX(), y)), consumer);
			}
			y += 1;
			AET.removeIf(e -> max(e.getPoint1().getY(), e.getPoint2().getY()) == ymax);
			AET.forEach(ActiveEdge::step);
		}
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
}
