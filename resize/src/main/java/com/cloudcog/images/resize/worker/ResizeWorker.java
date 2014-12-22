package com.cloudcog.images.resize.worker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudcog.images.resize.config.Config;
import com.cloudcog.images.resize.image.IOResizer;
import com.cloudcog.images.resize.image.ImageMagicImageResizer;
import com.cloudcog.images.resize.image.Rendition;

/**
 * 
 * @author mpahic
 *
 */
public class ResizeWorker implements Runnable {

	static Logger log = LoggerFactory.getLogger(ResizeWorker.class);
	
	private File imageFile;
	private final String inputFolder;
	private final String outputFolder;
	private final String workFolder;
	private final List<Rendition> renditions;
	private final Boolean useIM;
	
    public ResizeWorker(File inputFile) {
		imageFile = inputFile;
		inputFolder = Config.getInputFolder();
		outputFolder = Config.getOutputFolder();
		renditions = Config.getOutputRendition();
		workFolder = Config.getOriginalProcessedFolder();
		useIM = Config.getUseImageMagick();
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
    	for(Rendition rendition : renditions) {
    		StringBuilder outputFolderName = new StringBuilder(this.imageFile.getPath());
    		outputFolderName.replace(0, workFolder.length(), "");
    		outputFolderName.insert(0, outputFolder + File.separator + rendition.toString() + File.separator);
    		
        	File outputFile = new File(outputFolderName.toString());
        	outputFile.getParentFile().mkdirs();
	        try {
	        	if(useIM) {
	                ImageMagicImageResizer resizer = new ImageMagicImageResizer(imageFile, outputFile);
	                resizer.resizeTo(rendition.getWidth(), rendition.getHeight());
	        	} else {
	        		IOResizer resizer = new IOResizer(imageFile, outputFile);
	        		resizer.resizeTo(rendition.getWidth(), rendition.getHeight());
	        	}
	        	
	        } catch (Exception e) {
	        	log.info("File "+imageFile.getName()+" not resized to " + rendition.getWidth() + "x" + rendition.getHeight());
	        	log.error(e.getMessage(), e);
	        	
	        }
    	}
    }

	@Override
    public String toString(){
        return this.imageFile.getAbsolutePath();
    }

}
