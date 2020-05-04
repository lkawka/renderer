package com.lukkaw.shape;

import com.lukkaw.image.Point;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Circle extends Shape {
	private Point center;
	private Integer radius;
}
