package com.lukkaw.shape;

import com.lukkaw.image.Color;
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

    public Circle(Point center, Integer radius, Integer brush, Color color) {
        super(color, brush);

        this.center = center;
        this.radius = radius;
    }
}
