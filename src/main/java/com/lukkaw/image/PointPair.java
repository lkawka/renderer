package com.lukkaw.image;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointPair {
	public Point point1;
	public Point point2;

	public PointPair(Point point) {
		point1 = point;
	}

	public List<Point> toList() {
		return Arrays.asList(point1, point2);
	}
}
