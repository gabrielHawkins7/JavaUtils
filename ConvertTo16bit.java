package com.controlstest.Utils;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Converts an 8bit buffered image to a 16bit buffered image with only 3 channels
 */
public class ConvertTo16Bit {

    static BufferedImage convertTo16Bit(BufferedImage image){
        ColorSpace cs = image.getColorModel().getColorSpace();
        ComponentColorModel cm = new ComponentColorModel(cs, false, false, BufferedImage.OPAQUE, DataBuffer.TYPE_USHORT);
        WritableRaster wr = Raster.createInterleavedRaster(DataBuffer.TYPE_USHORT, image.getWidth(), image.getHeight(), image.getWidth() * 3,3,new int[]{0,1,2}, null);
        BufferedImage out = new BufferedImage(cm, wr, false, null);
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                int[] pixel = new int[image.getColorModel().getNumComponents()];
                image.getRaster().getPixel(x, y, pixel);
                int[] outPix = {pixel[0] << 8, pixel[1] << 8, pixel[2] << 8};

                out.getRaster().setPixel(x, y, outPix);

            }
        }
        return out;
    }
}
