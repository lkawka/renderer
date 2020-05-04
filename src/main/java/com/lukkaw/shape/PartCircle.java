package com.lukkaw.shape;

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
}
