import java.awt.Color;
import java.util.ArrayList;
import java.util.List; 
import java.awt.image.BufferedImage;
    
class ChannelUtil {
    public static final int redChannel = 0;
    public static final int greenChannel = 1;
    public static final int blueChannel = 2;

    // * Calculate Mean for specific channel in a block (hasil -> MIUr MIUg MIUb)
    public static double[] calculateAllMeans(BufferedImage image, int x, int y, int width, int height) {
        double[] meansList = new double[3];
        long[] sums = {0, 0, 0};
        int N = width * height;
    
        for (int j = y; j < height + y; j++) {
            for (int i = x; i < width + x; i++) {
                int rgb = image.getRGB(i, j);
                Color color = new Color(rgb);
                
                sums[redChannel] += color.getRed();
                sums[greenChannel] += color.getGreen();    
                sums[blueChannel] += color.getBlue();
            }
        }
        for (int i = 0; i < 3; i++) {
            meansList[i] = (double) sums[i] / N;
        }
        return meansList;
    }
    // * Get pixel intensity
    public static List<Integer> getChannelValues(BufferedImage image, int x, int y, int width, int height, int channel) {
        List<Integer> values = new ArrayList<>(width * height);

        for (int j = y; j < height + y; j++) {
            for (int i = x; i < width + x; i++) {
                int rgb = image.getRGB(i, j);
                Color color = new Color(rgb);
                
                switch (channel) {
                    case redChannel:
                        values.add(color.getRed());
                        break;
                    case greenChannel:
                        values.add(color.getGreen());
                        break;
                    case blueChannel:
                        values.add(color.getBlue());
                        break;
                }
            }
        }
        return values;
    }

    // * Get min max for MAD error calculation
    public static double[] getChannelMinMax(BufferedImage image, int x, int y, int width, int height, int channel) {
        int min = 255;
        int max = 0;

        for (int j = y; j < height + y; j++) {
            for (int i = x; i < width + x; i++) {
                int rgb = image.getRGB(i, j);
                Color color = new Color(rgb);
                
                int value = 0;
                switch (channel) {
                    case redChannel:
                        value = color.getRed();
                        break;    
                    case greenChannel:
                        value = color.getGreen();
                        break;
                    case blueChannel:
                        value = color.getBlue();
                        break;
                }

                if (value < min) {
                    min = value;
                }

                if (value > max) {
                    max = value;
                }
            }
        }
        return new double[] {min, max};
    }
}

