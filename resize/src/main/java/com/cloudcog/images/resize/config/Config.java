package com.cloudcog.images.resize.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.im4java.process.ProcessStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudcog.images.resize.image.Rendition;

/**
 * 
 * @author mpahic
 *
 */
public class Config {
	static Logger log = LoggerFactory.getLogger(Config.class);
	
	private static final String CONFIG_FILENAME = "config.properties";
	
	private static final String MAX_THREADS = "max_threads";
	private static final String OUTPUT_RENDITION = "output_rendition";
	private static final String INPUT_FOLDER = "input_folder";
	private static final String ORIGINAL_PROCESSED_FOLDER = "processed_folder";
	private static final String OUTPUT_FOLDER = "output_folder";

	private static final String USE_IMAGE_MAGICK = "use_image_magic";
	private static final String IMAGE_MAGICK_PATH = "image_magic_path";
	
	private static final Integer DEFAULT_MAX_THREADS = 8;
	private static final String DEFAULT_OUTPUT_RENDITION = "800x600";
	private static final String DEFAULT_INPUT_FOLDER = "input";
	private static final String DEFAULT_ORIGINAL_PROCESSED_FOLDER = "processed/original";
	private static final String DEFAULT_OUTPUT_FOLDER = "processed";
	private static final Boolean DEFAULT_USE_IMAGE_MAGICK = false;
	
	private static Properties properties = new Properties();
	private static List<Rendition> renditions;
	private static Long lastModifiedConfig;


	public static void constructProperties() {

		File configFile = new File(CONFIG_FILENAME);
		if(configFile.exists()) {
			readPropertiesFile(configFile);
		} else {
			constructConfigFile(configFile);
		}
		
		lastModifiedConfig = configFile.lastModified();

	}
	
	public static void updateConfigs() {
		File configFile = new File(CONFIG_FILENAME);
		if(configFile.exists() && configFile.lastModified() > lastModifiedConfig) {
			readPropertiesFile(configFile);
			lastModifiedConfig = configFile.lastModified();
		}
	}

	private static void constructConfigFile(File configFile) {
		OutputStream output = null;
		try {
			output = new FileOutputStream(configFile);
	 
			properties.setProperty(MAX_THREADS, DEFAULT_MAX_THREADS.toString());
			
			properties.setProperty(INPUT_FOLDER, DEFAULT_INPUT_FOLDER);
			properties.setProperty(OUTPUT_FOLDER, DEFAULT_OUTPUT_FOLDER);
			properties.setProperty(ORIGINAL_PROCESSED_FOLDER, DEFAULT_ORIGINAL_PROCESSED_FOLDER);
			
			properties.setProperty(OUTPUT_RENDITION, DEFAULT_OUTPUT_RENDITION);
			properties.setProperty(USE_IMAGE_MAGICK, DEFAULT_USE_IMAGE_MAGICK.toString());
	 
			properties.store(output, null);
			generateData();
	 
		} catch (IOException io) {
			log.error(io.getMessage(), io);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private static void readPropertiesFile(File configFile) {
		InputStream input = null;
		
		try {
			input = new FileInputStream(configFile);
			properties.load(input);
			generateData();
			
		} catch (IOException ex) {
			log.error(ex.getMessage(), ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private static void generateData() {
		createFolder(getInputFolder());
		createFolder(getOutputFolder());
		createFolder(getOriginalProcessedFolder());

		generateRenditions();
		if(Config.getImageMagickPath() != null) {
			ProcessStarter.setGlobalSearchPath(Config.getImageMagickPath());
		}
	}
	
	private static void generateRenditions() {
		renditions = new ArrayList<Rendition>();
		String renditionString = properties.getProperty(OUTPUT_RENDITION);
		if(renditionString != null) {
			String[] renditionsArray = renditionString.split(",");
			for (int i = 0; i < renditionsArray.length; i++) {
				try {
					renditions.add(new Rendition(renditionsArray[i]));
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		} else {
			renditions.add(new Rendition(DEFAULT_OUTPUT_RENDITION));
		}
	}
	
	private static void createFolder(String folderPath) {
		File folder = new File(folderPath);
		if(!folder.exists()) {
			folder.mkdirs();
		}
	}

	public static Integer getMaxThreads() {
		try {
			Integer maxThreads = Integer.valueOf(properties.getProperty(MAX_THREADS));
			if(maxThreads != null) {
				return maxThreads;
			}
			return DEFAULT_MAX_THREADS;
		} catch (Exception e) {
			return DEFAULT_MAX_THREADS;
		}
	}
	
	public static String getInputFolder() {
		String folder = properties.getProperty(INPUT_FOLDER);
		if(folder != null) {
			return folder;
		}
		return DEFAULT_INPUT_FOLDER;
	}
	
	public static String getOriginalProcessedFolder() {
		String folder = properties.getProperty(ORIGINAL_PROCESSED_FOLDER);
		if(folder != null) {
			return folder;
		}
		return DEFAULT_ORIGINAL_PROCESSED_FOLDER;
	}
	
	public static String getOutputFolder() {
		String folder = properties.getProperty(OUTPUT_FOLDER);
		if(folder != null) {
			return folder;
		}
		return DEFAULT_OUTPUT_FOLDER;
	}

	public static List<Rendition> getOutputRendition() {
		return renditions;
	}

	public static String getImageMagickPath() {
		return properties.getProperty(IMAGE_MAGICK_PATH);
	}
	
	public static Boolean getUseImageMagick() {
		String value = properties.getProperty(USE_IMAGE_MAGICK);
		if(value != null) {
			try {
				Boolean userImageMagick = Boolean.valueOf(value);
				if(userImageMagick != null) {
					return userImageMagick;
				}
				return DEFAULT_USE_IMAGE_MAGICK;
			} catch (Exception e){
				return DEFAULT_USE_IMAGE_MAGICK;
			}
		} else {
			return DEFAULT_USE_IMAGE_MAGICK;
		}
	}
}
