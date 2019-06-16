package com.comfywebdev.piccropper.exceptions;

import com.comfywebdev.piccropper.service.PicSize;

public class InvalidSizeException extends RuntimeException {
	
	public InvalidSizeException ( PicSize og, PicSize target ) {
		super ( "Invalid picture size : could not crop " +og + " into "+target);
	}
}
