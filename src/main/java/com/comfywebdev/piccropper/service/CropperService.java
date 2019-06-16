package com.comfywebdev.piccropper.service;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.comfywebdev.piccropper.exceptions.InvalidSizeException;
import com.comfywebdev.piccropper.exceptions.PicParsingException;
import com.comfywebdev.piccropper.exceptions.PictureFormatException;
import com.comfywebdev.piccropper.exceptions.SizeParsingException;

@Service
public class CropperService {

	public byte[] crop(InputStream originalPic, String sizeString, String contentType) 
			throws InvalidSizeException {
		
		try {
			PicSize size = new PicSize ( sizeString );
			MediaType mediaType = MediaType.valueOf(contentType);
			BufferedImage ogParsedImage = ImageIO.read(originalPic);
			
			
			int ogHeight = ogParsedImage.getHeight();
			int ogWidth = ogParsedImage.getWidth();
			if( ! (ogHeight >= size.getHeight() && ogWidth >= size.getWidth())){
				throw new InvalidSizeException(new PicSize(ogWidth, ogHeight), size);
			}
			int offsetX = (int) ((ogWidth - size.getWidth() ) / 2);
			int offsetY = (int) ((ogHeight - size.getHeight()) / 2 );
			BufferedImage img = ogParsedImage.getSubimage(
					offsetX, 
					offsetY, 
					size.getWidth(), 
					size.getHeight());
			BufferedImage copyOfImage = new BufferedImage(
					img.getWidth(), 
					img.getHeight(), 
					BufferedImage.TYPE_INT_RGB);
			Graphics g = copyOfImage.createGraphics();
			g.drawImage(img, 0, 0, null);
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			String fileFormat;
			if ( mediaType.equals(MediaType.IMAGE_GIF)) {
				fileFormat = "gif";
			}
			else if ( mediaType.equals(MediaType.IMAGE_JPEG)) {
				fileFormat = "jpeg";
			}
			else if ( mediaType.equals(MediaType.IMAGE_PNG)) {
				fileFormat = "png";
			}
			else {
				throw new PictureFormatException("Only supports GIF JPEG PNG");
			}
			copyOfImage.flush();
			img.flush();
			ImageIO.write(copyOfImage, fileFormat, outputStream);
			g.dispose();
			
			byte[] result = outputStream.toByteArray();
			outputStream.close();
			originalPic.close();
			return result;
			
		} catch (IOException | SizeParsingException e) {
			
			e.printStackTrace();
			throw new PicParsingException ( e );
		}
	}
	
	
}
