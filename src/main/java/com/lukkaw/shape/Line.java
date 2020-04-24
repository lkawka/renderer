package com.lukkaw.shape;

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
public class Line extends Shape {
    private PointPair pair;

    public Line(PointPair pair, Integer brush, Color color) {
        super(color, brush);
        this.pair = pair;
    }

    @JsonIgnore
    public Point getPoint1() {
        return pair.getPoint1();
    }

    public void setPoint1(Point point) {
        pair.setPoint1(point);
    }

    @JsonIgnore
    public Point getPoint2() {
        return pair.getPoint2();
    }

    public void setPoint2(Point point) {
        pair.setPoint2(point);
    }
}
