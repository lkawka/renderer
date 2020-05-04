package com.lukkaw.drawable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lukkaw.image.Canvas;
import com.lukkaw.image.ImageUtils;
import com.lukkaw.image.Point;
import com.lukkaw.shape.Circle;
import com.lukkaw.shape.Shape;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CircleDrawable extends Drawable {

	private Circle circle = new Circle();
	private boolean changingRadius = false;

	public CircleDrawable() {
		super(ShapeType.CIRCLE);
	}

	@Override
	public void draw(Canvas canvas) {
		switch (state) {
		case DRAWING:
			if (circle.getCenter() != null) {
				canvas.drawPoint(circle.getCenter(), circle.getColor(), circle.getBrush());
			}
			break;
		case MOVING:
		case DONE:
			canvas.drawCircle(circle.getCenter(), circle.getRadius(), circle.getColor(), circle.getBrush());
			break;
		}
	}

	@Override
	public void edit(Point click) {
		if (state != DrawableState.MOVING) {
			changingRadius = false;
		}
		switch (state) {
		case DRAWING:
			if (circle.getCenter() == null) {
				circle.setCenter(click);
			} else {
				circle.setRadius((int) ImageUtils.distance(circle.getCenter(), click));
				state = DrawableState.DONE;
			}
			break;
		case MOVING:
			if (changingRadius) {
				circle.setRadius((int) ImageUtils.distance(circle.getCenter(), click));
				changingRadius = false;
				return;
			}
			if (ImageUtils.inVicinity(circle.getCenter(), circle.getRadius(), click)) {
				changingRadius = true;
				return;
			}
			circle.setCenter(click);
			break;
		case DONE:
			break;
		}
	}

	@JsonIgnore
	@Override
	public Shape getShape() {
		return circle;
	}
}
