package com.cloudcog.images.resize.image;

import java.io.File;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageResizer {
	static Logger log = LoggerFactory.getLogger(ImageResizer.class);

	File inputFile;
	File outputFile;

	public ImageResizer(File inputFile, File outputFile) throws Exception {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}

	public void resizeTo(int width, int height) throws Exception {

		ConvertCmd cmd = new ConvertCmd();

		IMOperation op = new IMOperation();
		op.addImage(inputFile.getAbsolutePath());
		op.resize(width,height);
		op.addImage(outputFile.getAbsolutePath());

		cmd.run(op);
		
		log.info("Image "+outputFile.getName()+" resized");
		
	}

}
