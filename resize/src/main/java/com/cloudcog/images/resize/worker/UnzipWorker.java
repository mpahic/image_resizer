package com.cloudcog.images.resize.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudcog.images.resize.config.Config;

public class UnzipWorker implements Runnable {

	static Logger log = LoggerFactory.getLogger(UnzipWorker.class);
	
	private File zipFile;
	private String workFolder;
	private String inputFolder;

	public UnzipWorker(File inputfile) {
		this.zipFile = inputfile;
		this.inputFolder= Config.getInputFolder();
		workFolder = Config.getOriginalProcessedFolder();
	}
	
	@Override
	public void run() {
		try {
			waitForRelease();
			unZipIt();
			
		} catch (Exception e) {
			log.error("Unzipping file "+zipFile.getName()+" failed!\n");
		}

	}
	
	private synchronized void waitForRelease() throws InterruptedException {
		while(!copyToWorkingDirectory(zipFile)) {
			this.wait(1000);
		}
	}
	
    private boolean copyToWorkingDirectory(File file) {
    	try {
	    	StringBuilder newImageFileName = new StringBuilder(workFolder);
			newImageFileName.append(File.separator).append(file.getName());
			File newImageFile = new File(newImageFileName.toString());
			
			Files.move(zipFile.toPath(), Paths.get(newImageFileName.toString()), StandardCopyOption.REPLACE_EXISTING);
			zipFile = newImageFile;
			return true;
    	} catch (Exception e) {
    		return false;
    	}
	}
	
	public void unZipIt() throws IOException{
		 
		byte[] buffer = new byte[1024];

		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		// get the zipped file list entry
		ZipEntry ze = zis.getNextEntry();

		while (ze != null) {

			String fileName = ze.getName();
			try {
				if(!ze.isDirectory() && !fileName.contains("__MACOSX")) {
					StringBuilder outputPath = new StringBuilder(inputFolder);
					outputPath.append(fileName.substring(0, fileName.lastIndexOf(".")))
			    	.append(File.separator).append(fileName);
			    	
					File newFile = new File(outputPath.toString());
	
					log.info("file unzip : " + newFile.getAbsoluteFile());
	
					new File(newFile.getParent()).mkdirs();
				
					FileOutputStream fos = new FileOutputStream(newFile);
	
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
	
					fos.close();
				}
				ze = zis.getNextEntry();
			} catch (Exception e) {
				log.error("Unzip failed for: " + ze.getName(), e);
			}
			
		}

		zis.closeEntry();
		zis.close();

	}
	
}
