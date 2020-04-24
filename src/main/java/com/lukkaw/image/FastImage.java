package com.lukkaw.image;

import com.lukkaw.Config;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

public class FastImage {
	private Integer width;
	private Integer height;

	private int[] raw;

	public FastImage(Config config) {
		this.width = config.getCanvasWidth();
		this.height = config.getCanvasHeight();

		raw = new int[width * height * 3];
		Arrays.fill(raw, 255);
	}

	public void setPixel(int x, int y, Color color) {
		int i = getIndex(x, y);
		if (x >= 0 && x < width && y >= 0 && y < height) {
			raw[i] = color.getR();
			raw[i + 1] = color.getG();
			raw[i + 2] = color.getB();
		}
	}

	public void drawPoint(Point point, Color color, int brush) {
		drawPoint(point.getX(), point.getY(), color, brush);
	}

	public void drawPoint(int x, int y, Color color, int brush) {
		for (int j = -brush; j <= brush; j++) {
			for (int i = -brush; i <= brush; i++) {
				if (i * i + j * j < brush * brush) {
					setPixel(x + i, y + j, color);
				}
			}
		}
	}

	public void drawLine(PointPair line, Color color, int brush) {
		ImageUtils.linePoints(line).forEach(linePoint -> drawPoint(linePoint, color, brush));
	}

	public void drawCircle(Point center, int radius, Color color, int brush) {
		ImageUtils.circlePoints(center, radius).forEach(point -> drawPoint(point, color, brush));
	}

	public Image getImage() {
		BufferedImage bImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = (WritableRaster) bImg.getData();
		raster.setPixels(0, 0, width, height, raw);
		bImg.setData(raster);
		return SwingFXUtils.toFXImage(bImg, null);
	}

	private int getIndex(int x, int y) {
		return y * 3 * width + x * 3;
	}
}
