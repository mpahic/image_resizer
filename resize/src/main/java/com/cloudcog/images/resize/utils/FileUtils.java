package com.cloudcog.images.resize.utils;

import java.io.File;

import javax.activation.MimetypesFileTypeMap;

/**
 * 
 * @author mpahic
 *
 */
public class FileUtils {

	private static MimetypesFileTypeMap mtftp = new MimetypesFileTypeMap();
	static {
		mtftp.addMimeTypes("image png tif jpg jpeg bmp");
	}

	public static boolean isFileImage(String fileName) {

		String mimetype = mtftp.getContentType(fileName);
        String type = mimetype.split("/")[0];
        return type.equals("image");
	}

	public static boolean isFileZip(String string) {
		return string.endsWith("zip");
	}

	public static boolean isDirectory(String fileName) {
		return new File(fileName).isDirectory();
	}

}
