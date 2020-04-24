package com.lukkaw.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Color {
	public static final Color BLACK = new Color(0, 0, 0);

	private int r;
	private int g;
	private int b;

	public Color(javafx.scene.paint.Color color) {
		r = (int) (color.getRed() * 255);
		g = (int) (color.getGreen() * 255);
		b = (int) (color.getBlue() * 255);
	}

	public javafx.scene.paint.Color cast() {
		return new javafx.scene.paint.Color((float) r / 255, (float) g / 255, (float) b / 255, 1);
	}
}
