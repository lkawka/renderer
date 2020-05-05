package com.lukkaw.drawable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lukkaw.image.Canvas;
import com.lukkaw.image.ImageUtils;
import com.lukkaw.image.Point;
import com.lukkaw.shape.PartCircle;
import com.lukkaw.shape.Shape;

public class PartCircleDrawable extends Drawable {
	private final PartCircle partCircle = new PartCircle();

	public PartCircleDrawable() {
		super(ShapeType.PART_CIRCLE);
	}

	public void draw(Canvas canvas) {
		switch (state) {
		case DRAWING:
			if (partCircle.getA() == null && partCircle.getB() == null && partCircle.getC() == null) {
				return;
			} else if (partCircle.getA() != null && partCircle.getB() != null) {
				canvas.drawPoint(partCircle.getA(), partCircle.getColor(), partCircle.getBrush());
				canvas.drawPoint(partCircle.getB(), partCircle.getColor(), partCircle.getBrush());
			} else if (partCircle.getA() != null) {
				canvas.drawPoint(partCircle.getA(), partCircle.getColor(), partCircle.getBrush());
			}
			break;
		case MOVING:
		case DONE:
			drawPartCircle(canvas);
			break;
		}
	}

	public void edit(Point click) {
		switch (state) {
		case DRAWING:
			if (partCircle.getA() == null) {
				partCircle.setA(click);
			} else if (partCircle.getB() == null) {
				partCircle.setB(click);
				partCircle.setRadius((int) ImageUtils.distance(partCircle.getA(), click));
			} else {
				partCircle.setC(click);
				state = DrawableState.DONE;
			}
		}
	}

	@JsonIgnore
	public Shape getShape() {
		return partCircle;
	}

	public void drawPartCircle(Canvas canvas) {
		int det = ImageUtils.det(partCircle.getA(), partCircle.getB(), partCircle.getC());
		if (det <= 0) {
			ImageUtils.acceptCirclePoints(partCircle.getA(), partCircle.getRadius(), point -> {
				if (ImageUtils.det(partCircle.getA(), partCircle.getB(), point) > 0 ||
						ImageUtils.det(partCircle.getA(), partCircle.getC(), point) < 0) {
					canvas.drawPoint(point, partCircle.getColor(), partCircle.getBrush());
				}
			});
		} else {
			ImageUtils.acceptCirclePoints(partCircle.getA(), partCircle.getRadius(), point -> {
				if (ImageUtils.det(partCircle.getA(), partCircle.getB(), point) > 0 &&
						ImageUtils.det(partCircle.getA(), partCircle.getC(), point) < 0) {
					canvas.drawPoint(point, partCircle.getColor(), partCircle.getBrush());
				}
			});
		}
	}

}
