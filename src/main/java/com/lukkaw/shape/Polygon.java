package com.lukkaw.shape;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lukkaw.image.Point;
import com.lukkaw.image.PointPair;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Polygon extends Shape {
	private List<Point> points = new ArrayList<>();
	private FillType fillType = FillType.NONE;
	private String fillImagePath;

	public void add(Point point) {
		points.add(point);
	}

	public void setPoint(Point from, Point to) {
		int index = points.indexOf(from);
		if (index >= 0) {
			points.set(index, to);
		}
	}

	public void acceptLinesWithoutLast(Consumer<PointPair> consumer) {
		if (points.size() < 2) {
			return;
		}
		for (int i = 1; i < points.size(); i++) {
			consumer.accept(new PointPair(points.get(i - 1), points.get(i)));
		}
	}

	public void acceptLines(Consumer<PointPair> consumer) {
		acceptLinesWithoutLast(consumer);
		consumer.accept(new PointPair(points.get(points.size() - 1), points.get(0)));
	}

	@JsonIgnore
	public Point getCenter() {
		return points.get(0);
	}

	public void setFill(FillType fillType, String fillImagePath) {
		this.fillType = fillType;
		this.fillImagePath = fillImagePath;
	}

	public void clip(Rectangle rectangle) {

	}

	public enum FillType {
		NONE, COLOR, IMAGE
	}
}
