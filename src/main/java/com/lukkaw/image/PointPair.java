package com.lukkaw.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointPair {
	private Point point1;
	private Point point2;

	public PointPair(Point point) {
		point1 = point;
	}

	public List<Point> toList() {
		return Arrays.asList(point1, point2);
	}
}
