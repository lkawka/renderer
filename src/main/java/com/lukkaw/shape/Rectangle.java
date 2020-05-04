package com.lukkaw.shape;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.function.Consumer;

import com.lukkaw.image.Point;
import com.lukkaw.image.PointPair;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Rectangle extends Shape {
	private Point point1;
	private Point point2;

	public void setPoint1(Point point1) {
		if (point2 == null) {
			this.point1 = point1;
			return;
		}
		rearrangePoints(point1, this.point2);
	}

	public void setPoint2(Point point2) {
		rearrangePoints(this.point1, point2);
	}

	public void setPoints(Point point1, Point point2) {
		rearrangePoints(point1, point2);
	}

	private void rearrangePoints(Point point1, Point point2) {
		System.out.print("p1: " + point1 + ", p2: " + point2);
		this.point1 = new Point(min(point1.getX(), point2.getX()), min(point1.getY(), point2.getY()));
		this.point2 = new Point(max(point1.getX(), point2.getX()), max(point1.getY(), point2.getY()));
	}

	public void consumeVertices(Consumer<Point> consumer) {
		int x1 = point1.getX();
		int y1 = point1.getY();
		int x2 = point2.getX();
		int y2 = point2.getY();

		consumer.accept(new Point(x1, y1));
		consumer.accept(new Point(x1, y2));
		consumer.accept(new Point(x2, y2));
		consumer.accept(new Point(x2, y1));
	}

	public void consumeLines(Consumer<PointPair> consumer) {
		int x1 = point1.getX();
		int y1 = point1.getY();
		int x2 = point2.getX();
		int y2 = point2.getY();

		consumer.accept(new PointPair(new Point(x1, y1), new Point(x2, y1)));
		consumer.accept(new PointPair(new Point(x2, y1), new Point(x2, y2)));
		consumer.accept(new PointPair(new Point(x2, y2), new Point(x1, y2)));
		consumer.accept(new PointPair(new Point(x1, y2), new Point(x1, y1)));
	}

}
