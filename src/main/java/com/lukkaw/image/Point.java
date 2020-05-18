package com.lukkaw.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {
	public Integer x;
	public Integer y;

	public Point(Point point) {
		x = point.x;
		y = point.y;
	}
}
