package com.lukkaw.image;

import java.util.Objects;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Color {
	private static final Random random = new Random();
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color BACKGROUND = new Color(255, 255, 255);

	private int r;
	private int g;
	private int b;

	public static Color random() {
		return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
	}

	public Color(javafx.scene.paint.Color color) {
		r = (int) (color.getRed() * 255);
		g = (int) (color.getGreen() * 255);
		b = (int) (color.getBlue() * 255);
	}

	public javafx.scene.paint.Color cast() {
		return new javafx.scene.paint.Color((float) r / 255, (float) g / 255, (float) b / 255, 1);
	}

	public Color inverse() {
		return new Color(255 - r, 255 - g, 255 - b);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Color color = (Color) o;
		return r == color.r &&
				g == color.g &&
				b == color.b;
	}

	@Override
	public int hashCode() {
		return Objects.hash(r, g, b);
	}
}
