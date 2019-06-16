package com.comfywebdev.piccropper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PicCropperApplicationTests {

	@Autowired
    private MockMvc mockMvc;
	
	@ParameterizedTest
	@MethodSource("provideArguments")
    public void cropsProperly(
    		int widthParam, int heightParam,
    		int expectedWidth, int expectedHeight,
    		String inputFilename, String expectedFilename, 
    		String mediaType ) throws Exception {
    	
    	ClassLoader classLoader = getClass().getClassLoader();
    	File inputJpeg = new File(classLoader.getResource(inputFilename).getFile());
    	byte[] inputJpegBytes = Files.readAllBytes(inputJpeg.toPath());
    	File expectedJpeg = new File(classLoader.getResource(expectedFilename).getFile());
    	byte[] expectedJpegBytes = Files.readAllBytes(expectedJpeg.toPath());
    	
        MockMultipartFile firstFile = 
        		new MockMultipartFile("file", "pic",
        				mediaType, inputJpegBytes );
    	
    	MvcResult mvcResult = this.mockMvc.perform(
        		multipart("/crop")
        		.file(firstFile)
        		.param("size", widthParam + "x" + heightParam))
        	.andExpect(status().isOk())
        	.andExpect(content().contentType(mediaType))
        	.andReturn();
    	byte[] result = mvcResult.getResponse().getContentAsByteArray();
    	
    	ByteArrayInputStream actualStream = new ByteArrayInputStream(result);
    	ByteArrayInputStream expectedStream = new ByteArrayInputStream(expectedJpegBytes);
    	BufferedImage actualPic = ImageIO.read(actualStream);
    	BufferedImage expectedPic = ImageIO.read(expectedStream);
    	
    	int actualHeight = actualPic.getHeight();
    	int actualWidth = actualPic.getWidth();
    	assertEquals(expectedHeight, actualHeight);
    	assertEquals(expectedWidth, actualWidth);
        
    	long diff = 0;
        for (int y = 0; y < actualHeight; y++) {
            for (int x = 0; x < actualWidth; x++) {
                diff += pixelDiff(actualPic.getRGB(x, y), expectedPic.getRGB(x, y));
            }
        }
        long maxDiff = 3L * 255 * actualWidth * actualHeight;
 
        double diffPercent = 100.0 * diff / maxDiff;
    	assertTrue( diffPercent  < 2.0); // i messed up expected jpeg pic somehow
    	
    	actualPic.flush();
    	expectedPic.flush();
    	actualStream.close();
    	expectedStream.close();
    }
    
    private static int pixelDiff(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >>  8) & 0xff;
        int b1 =  rgb1        & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >>  8) & 0xff;
        int b2 =  rgb2        & 0xff;
        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }
    
    @SuppressWarnings("unused")
	private static Stream<Arguments> provideArguments() {
		return Stream.of(
				Arguments.of(256, 256, 256, 256, 
						"input/test-jpeg.jpeg", "expected/test-jpeg.jpeg", MediaType.IMAGE_JPEG_VALUE),
				Arguments.of(256, 256, 256, 256, 
						"input/test-png.png", "expected/test-png.png", MediaType.IMAGE_PNG_VALUE),
				Arguments.of(256, 256, 256, 256, 
						"input/test-gif.gif", "expected/test-gif.gif", MediaType.IMAGE_GIF_VALUE),
				Arguments.of(1000, 1000, 512, 512, 
						"input/test-png.png", "input/test-png.png", MediaType.IMAGE_PNG_VALUE),
				Arguments.of(1000, 1000, 512, 512, 
						"input/test-jpeg.jpeg", "input/test-jpeg.jpeg", MediaType.IMAGE_JPEG_VALUE),
				Arguments.of(1000, 1000, 512, 512, 
						"input/test-gif.gif", "input/test-gif.gif", MediaType.IMAGE_GIF_VALUE)
		);
	}
}
