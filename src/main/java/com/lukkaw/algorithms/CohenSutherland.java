package com.lukkaw.algorithms;

import static java.lang.Math.max;

import java.util.function.Consumer;

import com.lukkaw.image.ImageUtils;
import com.lukkaw.image.Point;
import com.lukkaw.image.PointPair;

public class CohenSutherland {

	public static void accept(PointPair line, PointPair rectangle, Consumer<Point> outsideConsumer,
			Consumer<Point> insideConsumer) {
		new Algorithm(line, rectangle, outsideConsumer, insideConsumer).accept();
	}

	private enum OutCode {
		INSIDE(0), LEFT(1), RIGHT(2), BOTTOM(4), TOP(8);
		private final int val;

		private OutCode(int val) {
			this.val = val;
		}

		public int val() {
			return val;
		}
	}

	private static class Algorithm {
		private int xmin, ymin;
		private int xmax, ymax;
		private int x0, y0;
		private int x1, y1;

		private Consumer<Point> outsideConsumer;
		private Consumer<Point> insideConsumer;

		public Algorithm(PointPair line, PointPair rectangle, Consumer<Point> outsideConsumer,
				Consumer<Point> insideConsumer) {
			this.x0 = line.getPoint1().getX();
			this.y0 = line.getPoint1().getY();
			this.x1 = line.getPoint2().getX();
			this.y1 = line.getPoint2().getY();

			this.xmin = rectangle.getPoint1().getX();
			this.ymin = rectangle.getPoint1().getY();
			this.xmax = rectangle.getPoint2().getX();
			this.ymax = rectangle.getPoint2().getY();

			this.outsideConsumer = outsideConsumer;
			this.insideConsumer = insideConsumer;
		}

		public void accept() {
			int xx0 = x0, yy0 = y0;
			int xx1 = x1, yy1 = y1;

			int outCode0 = computeOutCode(xx0, yy0);
			int outCode1 = computeOutCode(xx1, yy1);
			boolean accept = false;

			while (true) {
				if ((outCode0 | outCode1) == 0) {
					accept = true;
					break;
				} else if ((outCode0 & outCode1) != 0) {
					break;
				} else {
					int x = 0, y = 0;
					int outCodeOut = max(outCode1, outCode0);
					if ((outCodeOut & OutCode.TOP.val()) != 0) {
						x = xx0 + (xx1 - xx0) * (ymax - yy0) / (yy1 - yy0);
						y = ymax;
					} else if ((outCodeOut & OutCode.BOTTOM.val()) != 0) {
						x = xx0 + (xx1 - xx0) * (ymin - yy0) / (yy1 - yy0);
						y = ymin;
					} else if ((outCodeOut & OutCode.RIGHT.val()) != 0) {
						y = yy0 + (yy1 - yy0) * (xmax - xx0) / (xx1 - xx0);
						x = xmax;
					} else if ((outCodeOut & OutCode.LEFT.val()) != 0) {
						y = yy0 + (yy1 - yy0) * (xmin - xx0) / (xx1 - xx0);
						x = xmin;
					}

					// Now we move outside point to intersection point to clip
					// and get ready for next pass.
					if (outCodeOut == outCode0) {
						xx0 = x;
						yy0 = y;
						outCode0 = computeOutCode(xx0, yy0);
					} else {
						xx1 = x;
						yy1 = y;
						outCode1 = computeOutCode(xx1, yy1);
					}
				}
			}
			if (accept) {
				if (x0 != xx0 || y0 != yy0) {
					ImageUtils.acceptLinePoints(new PointPair(new Point(x0, y0), new Point(xx0, yy0)), outsideConsumer);
				}
				if (x1 != xx1 || y1 != yy1) {
					ImageUtils.acceptLinePoints(new PointPair(new Point(x1, y1), new Point(xx1, yy1)), outsideConsumer);
				}
				ImageUtils.acceptLinePoints(new PointPair(new Point(xx0, yy0), new Point(xx1, yy1)), insideConsumer);
			} else {
				ImageUtils.acceptLinePoints(new PointPair(new Point(x0, y0), new Point(x1, y1)), outsideConsumer);
			}
		}

		private int computeOutCode(double x, double y) {
			int code = OutCode.INSIDE.val();
			if (x < xmin)
				code |= OutCode.LEFT.val();
			else if (x > xmax)
				code |= OutCode.RIGHT.val();
			if (y < ymin)
				code |= OutCode.BOTTOM.val();
			else if (y > ymax)
				code |= OutCode.TOP.val();
			return code;
		}
	}
}
