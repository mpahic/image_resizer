package com.cloudcog.images.resize.worker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudcog.images.resize.config.Config;
import com.cloudcog.images.resize.image.ImageResizer;
import com.cloudcog.images.resize.image.Rendition;

public class ResizeWorker implements Runnable {

	static Logger log = LoggerFactory.getLogger(ResizeWorker.class);
	
	private File imageFile;
	private final String inputFolder;
	private final String outputFolder;
	private final String workFolder;
	private final Rendition rendition;
	
	
    public ResizeWorker(File inputFile) {
		imageFile = inputFile;
		inputFolder = Config.getInputFolder();
		outputFolder = Config.getOutputFolder();
		rendition = Config.getOutputRendition();
		workFolder = Config.getOriginalProcessedFolder();
	}

	public void run() {
        if(imageFile.exists()) {
        	try {
        		waitForRelease();
				processImage();
				
			} catch (Exception e) {
				log.error("Image file "+imageFile.getName()+" not processed correctly\n", e);
			}
        } else {
			log.error("File "+imageFile.getAbsolutePath()+" does not exist\n");
        }
    }

	private synchronized void waitForRelease() throws InterruptedException {
		while(!copyToWorkingDirectory(imageFile)) {
			this.wait(50);
		}
	}
	
    private boolean copyToWorkingDirectory(File file) {
    	try {
    		StringBuilder newImageFileName = new StringBuilder(file.getPath());
    		newImageFileName.replace(0, inputFolder.length(), workFolder);
    		
			File newImageFile = new File(newImageFileName.toString());
			newImageFile.mkdirs();
			Files.move(imageFile.toPath(), Paths.get(newImageFileName.toString()), StandardCopyOption.REPLACE_EXISTING);
			imageFile = newImageFile;
			return true;
    	} catch (Exception e) {
    		return false;
    	}
	}

	private void processImage() throws IOException {
		StringBuilder outputFolderName = new StringBuilder(this.imageFile.getPath());
		outputFolderName.replace(0, workFolder.length(), "");
		outputFolderName.insert(0, outputFolder + File.separator + this.rendition.toString() + File.separator);
		
    	File outputFile = new File(outputFolderName.toString());
    	outputFile.getParentFile().mkdirs();
        try {
            ImageResizer resizer = new ImageResizer(imageFile, outputFile);
            resizer.resizeTo(this.rendition.getWidth(), this.rendition.getHeight());
        	
        } catch (Exception e) {
        	log.info("File "+imageFile.getName()+" not resized");
        	log.error(e.getMessage(), e);
        	
        }
    }

	@Override
    public String toString(){
        return this.imageFile.getAbsolutePath();
    }

}
