package JavaUtils;
/**
 * Equalizes a 16bit BufferedImage
 */
public class EqualizeHistogram16bit {
    static BufferedImage equalizeHist16bit(BufferedImage image){
        int histR[] = new int[65536];
        int histG[] = new int[65536];
        int histB[] = new int[65536];
        //create histogram
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                int[] pixel = new int[3];
                image.getRaster().getPixel(x, y, pixel);
                histR[pixel[0]] = histR[pixel[0]] +1; 
                histG[pixel[1]] = histG[pixel[1]] +1; 
                histB[pixel[2]] = histB[pixel[2]] +1; 
            }
        }
        //create CDF in place
       for ( int i = 1; i < 65536; ++i ){
                histR[i] =  histR[i-1] + histR[i];
                histG[i] =  histG[i-1] + histG[i];
                histB[i] =  histB[i-1] + histB[i];
        }
        //scale hist back to 16bit
        int max = histR[histR.length -1];
        for(int i = 0; i < histR.length; i++){
            histR[i] = (int) Math.round(histR[i] * (65536.0) / max);
            histG[i] = (int) Math.round(histG[i] * (65536.0) / max);
            histB[i] = (int) Math.round(histB[i] * (65536.0) / max);
        }
        //apply hist
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                int[] oldPixel = new int[3];
                image.getRaster().getPixel(x, y, oldPixel);

                int[] newPixel = {
                    histR[oldPixel[0]],
                    histG[oldPixel[1]],
                    histB[oldPixel[2]]
                };

                image.getRaster().setPixel(x, y, newPixel);
            }   
        }
        return image;
    }
    
}