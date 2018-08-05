package com.bijenkorf.imageservice.configuration;

import com.bijenkorf.imageservice.enums.EncodingType;
import com.bijenkorf.imageservice.enums.ScaleType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.context.annotation.Bean;


@Data
public class ImageTypes {
    private List<ImageType> imageTypeList;
    private Map<String, ImageType> imageTypeMap = new HashMap<>();

    public static final String IMAGE_TYPE_SOURCE = "source_image";


    /*
     * This should be ideally populated from configurations. For simplicity, providing this statically
     */
    public List<ImageType> imageTypes(){
        if(null != imageTypeList)return imageTypeList;
        imageTypeList = new ArrayList<>();

        ImageType imageTypeSource = createImageType("source_image", 900, 600);
        imageTypeList.add(imageTypeSource);
        imageTypeMap.put(imageTypeSource.getPredefinedTypeName(), imageTypeSource);

        ImageType imageType1 = createImageType("mobile_small", 150, 100);
        imageTypeList.add(imageType1);
        imageTypeMap.put(imageType1.getPredefinedTypeName(), imageType1);


        ImageType imageType2 = createImageType("desktop_large", 900, 600);
        imageTypeList.add(imageType2);
        imageTypeMap.put(imageType2.getPredefinedTypeName(), imageType2);

        return imageTypeList;
    }

    public ImageType getImageType(String predefinedTypeName){
        if(!imageTypeMap.containsKey(predefinedTypeName)){
            return null;
        }

        return imageTypeMap.get(predefinedTypeName);
    }

    public ImageType createImageType(String predefinedType, int height, int width){
        ImageType imageType = new ImageType();
        imageType.setPredefinedTypeName(predefinedType);
        imageType.setScaleType(ScaleType.CROP);
        imageType.setEncodingType(EncodingType.JPG);
        imageType.setHeight(height);
        imageType.setWidth(width);
        return imageType;
    }
}
