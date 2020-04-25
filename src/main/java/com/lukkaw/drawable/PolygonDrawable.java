package com.lukkaw.drawable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lukkaw.image.Canvas;
import com.lukkaw.image.Color;
import com.lukkaw.image.ImageUtils;
import com.lukkaw.image.Point;
import com.lukkaw.image.PointPair;
import com.lukkaw.shape.Polygon;
import com.lukkaw.shape.Shape;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PolygonDrawable extends Drawable {

	private Polygon polygon;
	private MovingState movingState;
	private Point selectedPoint;
	private PointPair selectedLine;

	public PolygonDrawable() {
		super(ShapeType.POLYGON, "Polygon");
		polygon = new Polygon(new ArrayList<>(), 1, Color.BLACK);
	}

	@Override
	public void draw(Canvas canvas) {
		List<Point> points = polygon.getPoints();
		switch (state) {
		case DRAWING:
			if (points.size() == 1) {
				canvas.drawPoint(points.get(0), polygon.getColor(), polygon.getBrush());
			} else {
				polygon.getLinesWithoutLast().forEach(line ->
						canvas.drawLine(line, polygon.getColor(), polygon.getBrush()));
			}
			break;
		case MOVING:
			if (points.size() == 1) {
				canvas.drawPoint(points.get(0), polygon.getColor(), polygon.getBrush());
			} else {
				polygon.getLines().forEach(line ->
						canvas.drawLine(line, polygon.getColor(), polygon.getBrush()));
			}
			break;
		case DONE:
			polygon.getLines().forEach(line -> canvas.drawLine(line, polygon.getColor(), polygon.getBrush()));
		}
	}

	@Override
	public void edit(Point click) {
		if (state != DrawableState.MOVING) {
			movingState = MovingState.NOTHING_SELECTED;
		}

		switch (state) {
		case DRAWING:
			if (!polygon.getPoints().isEmpty() &&
					ImageUtils.inVicinity(polygon.getPoints().get(0), click)) {
				state = DrawableState.DONE;
			}
			polygon.add(click);
			break;
		case MOVING:
			switch (movingState) {
			case NOTHING_SELECTED:
				if (vertexSelected(click)) {
					return;
				}
				if (lineSelected(click)) {
					return;
				}
				moveEntirePolygon(click);
				break;
			case POINT_SELECTED:
				moveVertex(click);
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
		for (Point point : polygon.getPoints()) {
			if (ImageUtils.inVicinity(point, click)) {
				movingState = MovingState.POINT_SELECTED;
				selectedPoint = point;
				return true;
			}
		}
		return false;
	}

	private boolean lineSelected(Point click) {
		return polygon.getLines().stream().anyMatch(line -> lineSelected(line, click));
	}

	private boolean lineSelected(PointPair line, Point click) {
		Optional<? extends Point> pointOnLine = ImageUtils.linePoint(line, click);
		if (pointOnLine.isPresent()) {
			movingState = MovingState.LINE_SELECTED;
			selectedLine = line;
			selectedPoint = pointOnLine.get();
			return true;
		}
		return false;
	}

	private void moveEntirePolygon(Point click) {
		Point center = polygon.getCenter();
		int dx = click.getX() - center.getX();
		int dy = click.getY() - center.getY();

		polygon.getPoints().forEach(point -> {
			point.setX(point.getX() + dx);
			point.setY(point.getY() + dy);
		});
	}

	private void moveVertex(Point click) {
		polygon.setPoint(selectedPoint, click);
		movingState = MovingState.NOTHING_SELECTED;
	}

	private void moveLine(Point click) {
		int dx = click.getX() - selectedPoint.getX();
		int dy = click.getY() - selectedPoint.getY();

		for (var point : selectedLine.toList()) {
			polygon.setPoint(point, new Point(point.getX() + dx, point.getY() + dy));
		}

		movingState = MovingState.NOTHING_SELECTED;
	}

	@JsonIgnore
	@Override
	public Shape getShape() {
		return polygon;
	}

	private enum MovingState {
		NOTHING_SELECTED, POINT_SELECTED, LINE_SELECTED
	}
}
