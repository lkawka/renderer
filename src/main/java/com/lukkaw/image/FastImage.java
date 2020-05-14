package com.lukkaw.image;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lombok.Getter;

@Getter
public class FastImage extends AbstractImage {

	private final String filename;

	public FastImage(String filename) {
		this.filename = filename;

		File imageFile = new File(filename);
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(imageFile);
		} catch (IOException e) {
			throw new RuntimeException("Unable to read image");
		}

		width = bufferedImage.getWidth();
		height = bufferedImage.getHeight();

		WritableRaster raster = bufferedImage.getRaster();
		raw = raster.getPixels(0, 0, width, height, (int[]) null);
	}
}
