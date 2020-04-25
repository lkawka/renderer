package com.lukkaw.image;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;

import java.util.List;

public class LineTransform {
	private PointPair line;
	private int zone;

	public LineTransform(PointPair line) {
		if (line.getPoint1().getX() > line.getPoint2().getX()) {
			this.line = new PointPair(line.getPoint2(), line.getPoint1());
		} else {
			this.line = line;
		}

		int dx = this.line.getPoint2().getX() - this.line.getPoint1().getX();
		int dy = this.line.getPoint2().getY() - this.line.getPoint1().getY();

		if (dx > abs(dy)) {
			if (dy >= 0) {
				zone = 1;
			} else {
				zone = 8;
			}
		} else {
			if (dy >= 0) {
				zone = 2;
			} else {
				zone = 7;
			}
		}
	}

	public PointPair getLineInZone1() {
		int x1 = line.getPoint1().getX();
		int y1 = line.getPoint1().getY();
		int x2 = line.getPoint2().getX();
		int y2 = line.getPoint2().getY();

		int dx = x2 - x1;
		int dy = y2 - y1;

		if (zone == 1) {
			return line;
		}
		if (zone == 8) {
			return new PointPair(line.getPoint1(), new Point(x2, y2 - 2 * dy));
		}
		if (zone == 2) {
			return new PointPair(line.getPoint1(), new Point(x1 + dy, y1 + dx));
		}
		return new PointPair(line.getPoint1(), new Point(x1 - dy, y1 + dx)); // zone 7

	}

	public List<? extends Point> convertToOriginalZone(List<? extends Point> points) {
		return points.stream().map(this::convertToOriginalZone).collect(toList());
	}

	public Point convertToOriginalZone(Point point) {
		int x1 = line.getPoint1().getX();
		int y1 = line.getPoint1().getY();

		if (zone == 1) {
			return point;
		} else if (zone == 8) {
			return new Point(point.getX(), 2 * y1 - point.getY());
		} else if (zone == 2) {
			return new Point(x1 + point.getY() - y1, y1 + point.getX() - x1);
		}
		return new Point(x1 + point.getY() - y1, y1 - point.getX() + x1); //zone 7

	}
}
