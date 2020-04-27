package com.lukkaw.shape;

import com.lukkaw.image.Color;
import com.lukkaw.image.Point;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PartCircle extends Shape {
	private Point a;
	private Point b;
	private Point c;
	private Integer radius;

	public PartCircle(Point a, Point b, Point c, Integer radius, Integer brush, Color color) {
		super(color, brush);

		this.a = a;
		this.b = b;
		this.c = c;
		this.radius = radius;
	}
}
