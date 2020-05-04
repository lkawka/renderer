package com.lukkaw.image;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import com.lukkaw.Config;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class Canvas {
	private Integer width;
	private Integer height;
	private boolean useAntiAliasing;

	private int[] raw;

	public Canvas(Config config, boolean useAntiAliasing) {
		this.width = config.getCanvasWidth();
		this.height = config.getCanvasHeight();
		this.useAntiAliasing = useAntiAliasing;

		raw = new int[width * height * 3];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				setPixel(i, j, Color.BACKGROUND);
			}
		}
	}

	public void setPixel(Point point, Color color) {
		setPixel(point.getX(), point.getY(), color);
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
		if (!useAntiAliasing) {
			ImageUtils.linePoints(line, linePoint -> drawPoint(linePoint, color, brush));
		} else {
			drawAntiAliasedLine(line, color, brush);
		}
	}

	public void drawCircle(Point center, int radius, Color color, int brush) {
		ImageUtils.circlePoints(center, radius, point -> drawPoint(point, color, brush));
	}

	public Image getImage() {
		BufferedImage bImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = (WritableRaster) bImg.getData();
		raster.setPixels(0, 0, width, height, raw);
		bImg.setData(raster);
		return SwingFXUtils.toFXImage(bImg, null);
	}

	private void drawAntiAliasedLine(PointPair line, Color color, int thickness) {
		LineTransform lineTransform = new LineTransform(line);
		PointPair transformedLine = lineTransform.getLineInZone1();

		int x1 = transformedLine.getPoint1().getX();
		int y1 = transformedLine.getPoint1().getY();
		int x2 = transformedLine.getPoint2().getX();
		int y2 = transformedLine.getPoint2().getY();
		int dx = x2 - x1;
		int dy = y2 - y1;

		int d = 2 * dy - dx;
		int dE = 2 * dy;
		int dNE = 2 * (dy - dx);

		int twoVDx = 0;
		double invDenom = 1 / (2 * sqrt(dx * dx + dy * dy));
		double twoDxInvDenom = 2 * dx * invDenom;

		intensifyPixels(x1, y1, lineTransform, color, thickness, twoVDx, invDenom, twoDxInvDenom);
		while (x1 < x2) {
			++x1;
			if (d < 0) {
				twoVDx = d + dx;
				d += dE;
			} else {
				twoVDx = d - dx;
				d += dNE;
				++y1;
			}
			intensifyPixels(x1, y1, lineTransform, color, thickness, twoVDx, invDenom, twoDxInvDenom);
		}
	}

	private void intensifyPixels(int x, int y, LineTransform lineTransform, Color color, int thickness, int twoVDx,
			double invDenom, double twoDxInvDenom) {
		intensifyPixel(x, y, lineTransform, color, thickness, twoVDx * invDenom);
		for (int i = 1;
			 intensifyPixel(x, y + i, lineTransform, color, thickness, i * twoDxInvDenom - twoVDx * invDenom) > 0; ++i)
			;
		for (int i = 1;
			 intensifyPixel(x, y - i, lineTransform, color, thickness, i * twoDxInvDenom + twoVDx * invDenom) > 0; ++i)
			;
	}

	private double intensifyPixel(int x, int y, LineTransform lineTransform, Color color, int thickness,
			double distance) {
		double cov = coverage(thickness, abs(distance), 0.5);
		if (cov > 0)
			setPixel(lineTransform.convertToOriginalZone(new Point(x, y)),
					ImageUtils.lerp(Color.BACKGROUND, color, cov));
		return cov;
	}

	private int getIndex(int x, int y) {
		return y * 3 * width + x * 3;
	}

	private double coverage(double w, double D, double r) {
		if (w >= r) {
			if (w <= D) {
				return cov(D - w, r);
			} else {
				return 1 - cov(w - D, r);
			}
		} else {
			if (0 <= D && D <= w) {
				return 1 - cov(w - D, r) - cov(w + D, r);
			} else if (w <= D && D <= r - w) {
				return cov(D - w, r) - cov(D + w, r);
			} else {
				return cov(D - w, r);
			}
		}
	}

	private double cov(double d, double r) {
		if (d >= r) {
			return 0;
		}
		return acos(d / r) / PI - d * sqrt(pow(r, 2) - pow(d, 2)) / (PI * pow(r, 2));
	}
}
