package com.comfywebdev.piccropper.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.comfywebdev.piccropper.exceptions.InvalidSizeException;
import com.comfywebdev.piccropper.exceptions.PicParsingException;
import com.comfywebdev.piccropper.service.CropperService;

@Controller
public class PicCropperController {
	
	@Autowired
	CropperService cropperService;

	/**
	 * curl -F file@file.jpeg -F size=200x200 http://localhost:9693/crop
	 * @param file
	 * @param size : widthxheight
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/crop")
	public @ResponseBody ResponseEntity<?> crop (
			@RequestParam("file") MultipartFile file,
			@RequestParam String size ){ 
		try {
			String contentType = file.getContentType();
			byte[] croppedPic = cropperService.crop(file.getInputStream(), size, contentType);
			
			return ResponseEntity.ok()
					.contentLength(file.getSize())
					.contentType(MediaType.valueOf(contentType))
					.contentLength(croppedPic.length)
					.body(croppedPic);
			
		} catch (InvalidSizeException e) {
			//e.printStackTrace();
			try {
				return ResponseEntity.ok()
					.contentLength(file.getSize())
					.contentType(MediaType.valueOf(file.getContentType()))
					.contentLength(file.getSize())
					.body(file.getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
				return ResponseEntity.status(500).build();
			}
			
		} catch (IOException e) {
			throw new PicParsingException(e);
		}
		
	}
}
