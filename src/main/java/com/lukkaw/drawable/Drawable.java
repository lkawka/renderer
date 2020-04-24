package com.lukkaw.drawable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lukkaw.image.FastImage;
import com.lukkaw.image.Point;
import com.lukkaw.shape.Shape;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LineDrawable.class, name = "LINE"),
        @JsonSubTypes.Type(value = CircleDrawable.class, name = "CIRCLE"),
        @JsonSubTypes.Type(value = PolygonDrawable.class, name = "POLYGON"),
})
public abstract class Drawable {

    private String name;
    protected ShapeType type;
    protected DrawableState state = DrawableState.DRAWING;
    @JsonIgnore
    private Boolean isActive = false;

    protected Drawable(ShapeType type, String name) {
        this.type = type;
        this.name = name;
    }

    public void draw(FastImage fastImage) {
        throw new RuntimeException("Not implemented");
    }

    public void edit(Point click) {
        throw new RuntimeException("Not implemented");
    }

    public Shape getShape() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String toString() {
        return name;
    }
}