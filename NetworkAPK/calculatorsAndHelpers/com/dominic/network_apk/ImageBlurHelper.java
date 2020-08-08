package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PImage;

public class ImageBlurHelper {
	private float v = (float) (1.0 / 9.0);
	private float[][] kernel = { { v, v, v }, { v, v, v }, { v, v, v } };
	private PImage result, endImg;
	private PApplet p;

	public ImageBlurHelper(PApplet p) {
		this.p = p;
	}

	public PImage blur(PImage img, int fac) {
		endImg = img;
		for (int i = 0; i < fac; i++) {
			endImg = blurImage(endImg);
		}
		return endImg;
	}

	private PImage blurImage(PImage img) {
		img.loadPixels();

		// Create an opaque image of the same size as the original
		result = p.createImage(img.width, img.height, p.RGB);

		// Loop through every pixel in the image
		for (int y = 1; y < img.height - 1; y++) { // Skip top and bottom edges
			for (int x = 1; x < img.width - 1; x++) { // Skip left and right edges
				float sumRed = 0; // Kernel sum for this pixel
				float sumGreen = 0;
				float sumBlue = 0;
				for (int ky = -1; ky <= 1; ky++) {
					for (int kx = -1; kx <= 1; kx++) {
						// Calculate the adjacent pixel for this kernel point
						int pos = (y + ky) * img.width + (x + kx);
						float valRed = p.red(img.pixels[pos]);
						float valGreen = p.green(img.pixels[pos]);
						float valBlue = p.blue(img.pixels[pos]);

						// Multiply adjacent pixels based on the kernel values
						sumRed += kernel[ky + 1][kx + 1] * valRed;
						sumGreen += kernel[ky + 1][kx + 1] * valGreen;
						sumBlue += kernel[ky + 1][kx + 1] * valBlue;
					}
				}
				// For this pixel in the new image, set the gray value
				// based on the sum from the kernel
				result.pixels[y * img.width + x] = p.color(sumRed, sumGreen, sumBlue);
			}
		}
		// State that there are changes to edgeImg.pixels[]
		result.updatePixels();
		return result;
	}
}
