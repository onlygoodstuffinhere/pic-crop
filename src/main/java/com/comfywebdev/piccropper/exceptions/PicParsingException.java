package com.comfywebdev.piccropper.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PicParsingException extends RuntimeException {
	public PicParsingException ( Throwable t) {
		super(t);
	}
}
