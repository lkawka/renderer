package com.lukkaw.shape;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lukkaw.image.Color;
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

	public Polygon(List<Point> points, Integer brush, Color color) {
		super(color, brush);
		this.points.addAll(points);
	}

	public void add(Point point) {
		points.add(point);
	}

	public void setPoint(Point from, Point to) {
		int index = points.indexOf(from);
		if (index >= 0) {
			points.set(index, to);
		}
	}

	@JsonIgnore
	public List<PointPair> getLinesWithoutLast() {
		List<PointPair> lines = new ArrayList<>();
		if (points.size() < 2) {
			return lines;
		}

		for (int i = 1; i < points.size(); i++) {
			lines.add(new PointPair(points.get(i - 1), points.get(i)));
		}

		return lines;
	}

	@JsonIgnore
	public List<PointPair> getLines() {
		List<PointPair> lines = getLinesWithoutLast();

		lines.add(new PointPair(points.get(points.size() - 1), points.get(0)));

		return lines;
	}

	@JsonIgnore
	public Point getCenter() {
		return points.get(0);
	}
}
