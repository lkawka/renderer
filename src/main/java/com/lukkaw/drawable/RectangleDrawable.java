package com.lukkaw.drawable;

import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lukkaw.image.Canvas;
import com.lukkaw.image.ImageUtils;
import com.lukkaw.image.Point;
import com.lukkaw.image.PointPair;
import com.lukkaw.shape.Rectangle;
import com.lukkaw.shape.Shape;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RectangleDrawable extends Drawable {
	private final Rectangle rectangle = new Rectangle();
	private MovingState movingState = MovingState.NOTHING_SELECTED;
	private Point selectedPoint;
	private PointPair selectedLine;

	public RectangleDrawable() {
		super(ShapeType.RECTANGLE);
	}

	@Override
	public void draw(Canvas canvas) {
		switch (state) {
		case DRAWING:
			if (rectangle.getPoint1() != null) {
				canvas.drawPoint(rectangle.getPoint1(), rectangle.getColor(), rectangle.getBrush());
			}
			break;
		case MOVING:
		case DONE:
			rectangle.acceptLines(line -> canvas.drawLine(line, rectangle.getColor(), rectangle.getBrush()));
			break;
		}
	}

	@Override
	public void edit(Point click) {
		if (state != DrawableState.MOVING) {
			movingState = MovingState.NOTHING_SELECTED;
		}

		switch (state) {
		case DRAWING:
			if (rectangle.getPoint1() == null) {
				rectangle.setPoint1(click);
			} else {
				rectangle.setPoint2(click);
				state = DrawableState.DONE;
			}
			break;
		case MOVING:
			switch (movingState) {
			case NOTHING_SELECTED:
				if (vertexSelected(click)) {
					return;
				}
				if (edgeSelected(click)) {
					return;
				}
				moveEntireRectangle(click);
				break;
			case POINT_SELECTED:
				movePoint(click);
				break;
			case LINE_SELECTED:
				moveLine(click);
				break;
			}
			break;
		case DONE:
			break;
		}
	}

	private boolean vertexSelected(Point click) {
		AtomicBoolean selected = new AtomicBoolean(false);
		rectangle.acceptVertices(point -> {
			if (ImageUtils.inVicinity(point, click)) {
				setSelectedPoint(point);
				setMovingState(MovingState.POINT_SELECTED);
				selected.set(true);
			}
		});
		return selected.get();
	}

	private boolean edgeSelected(Point click) {
		AtomicBoolean selected = new AtomicBoolean(false);

		rectangle.acceptLines(line -> ImageUtils.linePoint(line, click).ifPresent(linePoint -> {
			setSelectedPoint(linePoint);
			setSelectedLine(line);
			setMovingState(MovingState.LINE_SELECTED);
			selected.set(true);
		}));
		return selected.get();
	}

	private void moveEntireRectangle(Point click) {
		int dx = click.x - rectangle.getPoint1().x;
		int dy = click.y - rectangle.getPoint1().y;

		rectangle.setPoint1(new Point(rectangle.getPoint1().x + dx, rectangle.getPoint1().y + dy));
		rectangle.setPoint2(new Point(rectangle.getPoint2().x + dx, rectangle.getPoint2().y + dy));
	}

	private void movePoint(Point click) {
		movingState = MovingState.NOTHING_SELECTED;

		if (selectedPoint.equals(rectangle.getPoint1())) {
			rectangle.setPoint1(click);
			return;
		}
		if (selectedPoint.equals(rectangle.getPoint2())) {
			rectangle.setPoint2(click);
			return;
		}

		Point point1 = new Point(rectangle.getPoint1());
		Point point2 = new Point(rectangle.getPoint2());
		if (selectedPoint.getX().equals(rectangle.getPoint1().getX())) {
			point1.setX(click.getX());
			point2.setY(click.getY());
		} else {
			point2.setX(click.getX());
			point1.setY(click.getY());
		}
		rectangle.setPoints(point1, point2);
	}

	private void moveLine(Point click) {
		movingState = MovingState.NOTHING_SELECTED;

		int x1 = rectangle.getPoint1().getX();
		int y1 = rectangle.getPoint1().getY();
		int x2 = rectangle.getPoint2().getX();
		int y2 = rectangle.getPoint2().getY();

		int dx = click.getX() - selectedPoint.getX();
		int dy = click.getY() - selectedPoint.getY();

		if (selectedLine.getPoint1().equals(rectangle.getPoint1())) {
			rectangle.setPoint1(new Point(x1, y1 + dy));
			return;
		}
		if (selectedLine.getPoint1().equals(rectangle.getPoint2())) {
			rectangle.setPoint2(new Point(x2, y2 + dy));
			return;
		}
		if (selectedLine.getPoint1().getX().equals(x1)) {
			rectangle.setPoint1(new Point(x1 + dx, y1));
			return;
		}
		rectangle.setPoint2(new Point(x2 + dx, y2));
	}

	@JsonIgnore
	@Override
	public Shape getShape() {
		return rectangle;
	}

	private enum MovingState {
		NOTHING_SELECTED, POINT_SELECTED, LINE_SELECTED
	}
}
