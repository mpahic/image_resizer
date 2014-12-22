package com.cloudcog.images.resize.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author mpahic
 *
 */
public class IOResizer {
	static Logger log = LoggerFactory.getLogger(IOResizer.class);

	private File inputFile = null;
	private File outputFile = null;
	private int width;
	private int height;

	public IOResizer(File inputFile, File outputFile) throws Exception {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}

	public void resizeTo(int width, int height) throws Exception {
		
		FileInputStream fis = new FileInputStream(inputFile);
		BufferedImage img = ImageIO.read(fis);
		fis.close();
		
		determineImageScale(img.getWidth(), img.getHeight(), width, height);

		BufferedImage bImg = new BufferedImage(this.width, this.height,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D g = bImg.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.drawImage(img, 0, 0, this.width, this.height, null);
		g.dispose();
		FileOutputStream fos = new FileOutputStream(outputFile);
		ImageIO.write(bImg, inputFile.getName().substring(inputFile.getName().lastIndexOf(".") + 1), fos);
		fos.close();

		log.info("Image "+outputFile.getName()+" resized to " + width + "x" + height);
	}

	private void determineImageScale(int sourceWidth, int sourceHeight,
			int targetWidth, int targetHeight) {

		double scalex = (double) targetWidth / sourceWidth;
		double scaley = (double) targetHeight / sourceHeight;
		double scale = Math.min(scalex, scaley);

		this.width = (int) (scale * sourceWidth);
		this.height = (int) (scale * sourceHeight);
	}
}
