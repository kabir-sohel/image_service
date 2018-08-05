package com.bijenkorf.imageservice.util;

import com.bijenkorf.imageservice.enums.EncodingType;

public class CommonUtils {
    public static String getFileNameFromReference(String reference){
        String fileName = reference.replaceAll("/", "_");
        return fileName;
    }

    public static String getBucketName(String predefinedType, String fileName){
        StringBuilder stringBuilder = new StringBuilder();
        String[] words = fileName.split(".");
        String fileNameWithoutExtension = words[0];

        if(fileNameWithoutExtension.length() > 8){
            return predefinedType + fileName.substring(0, 4) + "/" + fileName.substring(4, 8);
        }
        if(fileNameWithoutExtension.length() > 4){
            return predefinedType + fileName.substring(0, 4);
        }
        return predefinedType;
    }

    /*
     * This should be properly managed by configuration, each encoding type should be mapped to proper string type.
     */
    public static String getFileEncodingString(EncodingType encodingType){
        if(encodingType == EncodingType.JPG)return "jpg";
        if(encodingType == EncodingType.PNG)return "png";
        return null;
    }
}
