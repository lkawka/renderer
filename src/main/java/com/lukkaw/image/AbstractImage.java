package com.lukkaw.image;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class AbstractImage {
	protected Integer width;
	protected Integer height;
	protected int[] raw;

	protected AbstractImage(int width, int height) {
		this.width = width;
		this.height = height;
		this.raw = new int[width * height * 3];
	}

	public Color getPixel(int x, int y) {
		int index = getIndex(x, y);
		if (x >= 0 && x < width && y >= 0 && y < height) {
			return new Color(raw[index], raw[index + 1], raw[index + 2]);
		}
		return null;
	}

	public Color getPixel(Point point) {
		return getPixel(point.getX(), point.getY());
	}

	public void setPixel(int x, int y, Color color) {
		int i = getIndex(x, y);
		if (x >= 0 && x < width && y >= 0 && y < height) {
			raw[i] = color.getR();
			raw[i + 1] = color.getG();
			raw[i + 2] = color.getB();
		}
	}

	public void setPixel(Point point, Color color) {
		setPixel(point.getX(), point.getY(), color);
	}

	protected int getIndex(int x, int y) {
		return y * 3 * width + x * 3;
	}
}
