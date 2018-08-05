package com.bijenkorf.imageservice.component;

import com.mortennobel.imagescaling.ResampleOp;
import java.awt.image.BufferedImage;
import org.springframework.stereotype.Component;

@Component
public class ImageResizer {
    public BufferedImage resizeImage(BufferedImage bufferedImage, int sizeX, int sizeY) {
        ResampleOp resamOp = new ResampleOp(50, 40);
        BufferedImage modifiedImage = resamOp.filter(bufferedImage, null);
        return modifiedImage;
    }
}
