package com.bijenkorf.imageservice.service;

import com.bijenkorf.imageservice.component.ImageResizer;
import com.bijenkorf.imageservice.component.S3Client;
import com.bijenkorf.imageservice.configuration.ImageType;
import com.bijenkorf.imageservice.configuration.ImageTypes;
import com.bijenkorf.imageservice.util.CommonUtils;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import sun.plugin2.gluegen.runtime.BufferFactory;


@Service
public class ImageService {

    @Autowired
    private S3Client s3Client;
    @Autowired
    private ImageResizer imageResizer;

    private ImageTypes imageTypes;
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    public ImageService(S3Client s3Client, ImageResizer imageResizer, ImageTypes imageTypes){
        this.s3Client = s3Client;
        this.imageResizer = imageResizer;
        this.imageTypes = imageTypes;
    }


    public InputStream getImageAsInputStream(String typeName, String seoName, String refeference){

        String fileName = CommonUtils.getFileNameFromReference(refeference);
        ImageType imageType = imageTypes.getImageType(typeName);

        /*
         * This should ideally be done by validators
         */

        if(null == imageType){
           logger.error("Predefined type is unknown");
           return null;
        }



        InputStream existingOptimizedImage = s3Client.getFileAsInputStream(imageType.getPredefinedTypeName(), fileName);

        /*
         * Need to resize
         */

        if (null == existingOptimizedImage) {
            ImageType sourceImage = imageTypes.getImageType(ImageTypes.IMAGE_TYPE_SOURCE);
            if(null == sourceImage){
                logger.error("Source image type is not found");
                return null;
            }
            InputStream sourceImageInputStream = s3Client.getFileAsInputStream(sourceImage.getPredefinedTypeName(), fileName);
            if(null == sourceImageInputStream){
                logger.error("Source image type is not found in remote container");
                return null;
            }
            BufferedImage originalImage = null;
            BufferedImage resizedImage = null;
            try {
                originalImage = ImageIO.read(sourceImageInputStream);
                resizedImage = imageResizer.resizeImage(originalImage, imageType.getHeight(), imageType.getWidth());
            }catch (Exception e){
                logger.error("Image Resizing Failed");
            }

            /*
             * Now that we have got resized image, we need to upload it into s3
             */
            File outputfile = new File(fileName);
            String fileEncodingString = CommonUtils.getFileEncodingString(imageType.getEncodingType());
            try{
                ImageIO.write(resizedImage, fileEncodingString, outputfile);
            }catch (IOException ioe){
                logger.error("Couldn't create file from BufferedImage object");
                logger.error(ioe.getMessage());
            }

            s3Client.uploadFileTos3bucket(imageType.getPredefinedTypeName(), fileName, outputfile);
            InputStream fileInputStream = null;
            try{
                fileInputStream = new FileInputStream(outputfile);
            }catch (IOException ioe){

                logger.error("Couldn't create Input Stream from file");
                logger.error(ioe.getMessage());
            }

            return fileInputStream;
        }

        return existingOptimizedImage;
        /*
        InputStream inputStream = null;
        ClassPathResource imgFile = new ClassPathResource("static/image/bird.jpg");
        try {
            inputStream = imgFile.getInputStream();
        }catch (IOException e){
            //
        }
        return inputStream;
        */
    }

}
