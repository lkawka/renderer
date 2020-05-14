package com.lukkaw.drawable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lukkaw.image.Canvas;
import com.lukkaw.image.Color;
import com.lukkaw.image.FastImage;
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

	private final Polygon polygon = new Polygon();
	private MovingState movingState = MovingState.NOTHING_SELECTED;
	private Point selectedPoint;
	private PointPair selectedLine;

	private FastImage selectedImage;

	public PolygonDrawable() {
		super(ShapeType.POLYGON);
	}

	@Override
	public void draw(Canvas canvas) {
		List<Point> points = polygon.getPoints();
		switch (state) {
		case DRAWING:
			if (points.size() == 1) {
				canvas.drawPoint(points.get(0), polygon.getColor(), polygon.getBrush());
			} else {
				polygon.acceptLinesWithoutLast(line -> canvas.drawLine(line, polygon.getColor(), polygon.getBrush()));
			}
			break;
		case MOVING:
		case DONE:
			if (polygon.getFillType() == Polygon.FillType.COLOR) {
				drawColorFill(canvas);
			} else if (polygon.getFillType() == Polygon.FillType.IMAGE && polygon.getFillImagePath() != null) {
				drawImageFill(canvas);
			}

			if (polygon.getClippedRectangle() != null) {
				drawClippedEdges(canvas);
			} else {
				polygon.acceptLines(line -> canvas.drawLine(line, polygon.getColor(), polygon.getBrush()));
			}
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
					break;
				}
				if (edgeSelected(click)) {
					break;
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

	private void drawColorFill(Canvas canvas) {
		ImageUtils.acceptFillPoints(polygon.getPoints(),
				point -> canvas.drawPoint(point, polygon.getColor(), polygon.getBrush()));
	}

	private void drawImageFill(Canvas canvas) {
		if (selectedImage == null || !selectedImage.getFilename().equals(polygon.getFillImagePath())) {
			selectedImage = new FastImage(polygon.getFillImagePath());
		}

		int dx = polygon.getPoints().stream().mapToInt(Point::getX).min().getAsInt();
		int dy = polygon.getPoints().stream().mapToInt(Point::getY).min().getAsInt();

		ImageUtils.acceptFillPoints(polygon.getPoints(), point -> {
			Color color = selectedImage.getPixel(point.getX() - dx, point.getY() - dy);
			if (color != null) {
				canvas.drawPoint(point, color, 1);
			}
		});
	}

	private void drawClippedEdges(Canvas canvas) {
		ImageUtils.acceptClippingPoints(polygon.getPoints(), polygon.getClippedRectangle().getPoints(),
				outsidePoint -> canvas.drawPoint(outsidePoint, polygon.getColor(), polygon.getBrush()),
				insidePoint -> canvas.drawPoint(insidePoint, polygon.getColor().inverse(), polygon.getBrush()));
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

	private boolean edgeSelected(Point click) {
		AtomicBoolean selected = new AtomicBoolean(false);
		polygon.acceptLines(line -> {
			if (edgeSelected(line, click)) {
				selected.set(true);
			}
		});
		return selected.get();
	}

	private boolean edgeSelected(PointPair line, Point click) {
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
