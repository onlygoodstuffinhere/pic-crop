package com.comfywebdev.piccropper.service;

import com.comfywebdev.piccropper.exceptions.SizeParsingException;

public class PicSize {

	private int width;
	private int height;
	
	/**
	 * widthxheight -> picsize
	 * @param unparsedSize
	 */
	public PicSize ( String unparsedSize ) throws SizeParsingException{
		String[] splitInput = unparsedSize.split("x");
		if ( splitInput.length != 2 ) {
			throw new SizeParsingException(unparsedSize);
		}
		try {
			this.width = Integer.parseInt(splitInput[0]);
			this.height = Integer.parseInt(splitInput[1]);
		}
		catch ( NumberFormatException e ) {
			throw new SizeParsingException(unparsedSize);
		}
	}
	
	public PicSize(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PicSize other = (PicSize) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.width+"x"+this.height;
	}
	
}
