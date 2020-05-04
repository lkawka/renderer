package com.lukkaw.drawable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lukkaw.image.Canvas;
import com.lukkaw.image.ImageUtils;
import com.lukkaw.image.Point;
import com.lukkaw.shape.Line;
import com.lukkaw.shape.Shape;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LineDrawable extends Drawable {

	private final Line line = new Line();
	private MovingState movingState = MovingState.NO_POINT_SELECTED;

	public LineDrawable() {
		super(ShapeType.LINE);
	}

	@Override
	public void draw(Canvas canvas) {
		switch (state) {
		case DRAWING:
			if (line.getPoint1() != null) {
				canvas.drawPoint(line.getPoint1(), line.getColor(), line.getBrush());
			}
			break;
		case MOVING:
		case DONE:
			canvas.drawLine(line.getPair(), line.getColor(), line.getBrush());
			break;
		}
	}

	@Override
	public void edit(Point click) {
		if (state != DrawableState.MOVING) {
			movingState = MovingState.NO_POINT_SELECTED;
		}

		switch (state) {
		case DRAWING:
			if (line.getPoint1() != null) {
				line.setPoint2(click);
				state = DrawableState.DONE;
			} else {
				line.setPoint1(click);
			}
			break;
		case MOVING:
			if (movingState == MovingState.NO_POINT_SELECTED) {
				if (ImageUtils.inVicinity(line.getPoint1(), click)) {
					movingState = MovingState.POINT_1_SELECTED;
				} else if (ImageUtils.inVicinity(line.getPoint2(), click)) {
					movingState = MovingState.POINT_2_SELECTED;
				}
			} else {
				if (movingState == MovingState.POINT_1_SELECTED) {
					line.setPoint1(click);
				} else {
					line.setPoint2(click);
				}
				movingState = MovingState.NO_POINT_SELECTED;
			}
			break;
		case DONE:
			break;
		}
	}

	@JsonIgnore
	@Override
	public Shape getShape() {
		return line;
	}

	private enum MovingState {
		NO_POINT_SELECTED, POINT_1_SELECTED, POINT_2_SELECTED
	}
}
