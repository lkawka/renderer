package com.lukkaw.algorithms;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import com.lukkaw.image.Point;
import com.lukkaw.image.PointPair;

public class SnanLineActiveEdgeTableVertexSorting {

	public static void accept(List<Point> vertices, Consumer<Point> consumer) {
		List<Integer> indices = IntStream.range(0, vertices.size())
				.boxed()
				.sorted(comparing(i -> vertices.get(i).y))
				.collect(toList());
		int n = indices.size();
		int k = 0;
		int i = indices.get(k);
		int ymin = vertices.get(indices.get(0)).y;
		int ymax = vertices.get(indices.get(vertices.size() - 1)).y;
		int y = ymin;
		List<ActiveEdge> AET = new ArrayList<>();
		while (y < ymax) {
			while (vertices.get(i).y == y) {
				int j = (i - 1 + n) % n;
				if (vertices.get(j).y > vertices.get(i).y) {
					AET.add(new ActiveEdge(new Point(vertices.get(i)), new Point(vertices.get(j))));
				}
				j = (i + 1) % n;
				if (vertices.get(j).y > vertices.get(i).y) {
					AET.add(new ActiveEdge(new Point(vertices.get(i)), new Point(vertices.get(j))));
				}
				k += 1;
				i = indices.get(k);
			}
			AET.sort(comparing(pair -> pair.point1.x));
			for (int j = 0; j < AET.size(); j += 2) {
				for (int l = AET.get(j).point1.x; l <= AET.get((j + 1) % AET.size()).point1.x; l++) {
					consumer.accept(new Point(l, y));
				}
			}
			y += 1;
			int finalY = y;
			AET.removeIf(e -> e.point2.y == finalY);
			AET.forEach(ActiveEdge::step);
		}
	}

	private static class ActiveEdge extends PointPair {
		double slope;
		double sum;

		public ActiveEdge(Point point1, Point point2) {
			super(point1, point2);
			int xs = (point2.x - point1.x);
			int ys = (point2.y - point1.y);
			this.slope = (double) xs / ys;
			this.sum = point1.x;
		}

		public void step() {
			sum += slope;
			point1 = new Point((int) sum, point1.y);
		}
	}
}
