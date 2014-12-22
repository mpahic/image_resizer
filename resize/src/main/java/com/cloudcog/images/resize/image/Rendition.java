package com.cloudcog.images.resize.image;

public class Rendition {
	
	private Integer width;
	private Integer height;

	public Rendition(String rendition) {
		StringBuilder builder = new StringBuilder(rendition);
		String width = builder.substring(0, builder.indexOf("x"));
		String height = builder.substring(builder.indexOf("x") + 1);

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
