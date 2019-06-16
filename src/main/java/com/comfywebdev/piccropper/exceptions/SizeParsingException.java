package com.comfywebdev.piccropper.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SizeParsingException extends RuntimeException {
	
	public SizeParsingException ( String inputSize){
		super("Couldn't parse size : " + inputSize);
	}
}
