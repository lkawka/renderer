package com.lukkaw.shape;

import com.lukkaw.image.Color;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class Shape {
    protected Color color;
    protected Integer brush;
    protected Long priority = 1L;

    protected Shape(Color color, Integer brush) {
        this.color = color;
        this.brush = brush;
    }
}
