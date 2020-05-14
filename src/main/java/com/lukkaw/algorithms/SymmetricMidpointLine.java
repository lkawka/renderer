package com.lukkaw.algorithms;

import java.util.function.Consumer;

import com.lukkaw.image.LineTransform;
import com.lukkaw.image.Point;
import com.lukkaw.image.PointPair;

public class SymmetricMidpointLine {

	public static void accept(PointPair line, Consumer<Point> consumer) {
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
}
