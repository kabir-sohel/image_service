package com.bijenkorf.imageservice.configuration;


import com.bijenkorf.imageservice.enums.EncodingType;
import com.bijenkorf.imageservice.enums.ScaleType;
import lombok.Data;

@Data
public class ImageType {

    private int height;
    private int width;
    private String predefinedTypeName;
    private int quality;
    private EncodingType encodingType;
    private ScaleType scaleType;
    private String sourceName;
}
