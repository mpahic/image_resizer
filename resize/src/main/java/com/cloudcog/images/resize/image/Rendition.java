package com.cloudcog.images.resize.image;

/**
 * 
 * @author mpahic
 *
 */
public class Rendition {
	
	private Integer width;
	private Integer height;

	public Rendition(String rendition) {
		StringBuilder builder = new StringBuilder(rendition);
		int index = builder.indexOf("x");
		String width = builder.substring(0, index);
		String height = builder.substring(index + 1);

		this.width = Integer.valueOf(width);
		this.height = Integer.valueOf(height);
	}
	
	public Integer getWidth() {
		return width;
	}
	
	public Integer getHeight() {
		return height;
	}
	
	@Override
	public String toString() {
		return width + "x" + height;
	}
}
