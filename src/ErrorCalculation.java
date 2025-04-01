import java.awt.image.BufferedImage;
import java.util.List;

class ErrorCalculation {
    // * Error calculation methods (per channel)
    // Variance per channel
    private static double calculateVarianceForChannel(List<Integer> values, double mean) {
        double squaredDiff = 0;
        
        for (int value: values) {
            double diff = value - mean;
            squaredDiff += diff * diff;
        }
        return squaredDiff / values.size();
    }   

    // MAD per channel
    public static double calculateMADForChannel(List<Integer> values, double mean) {
        double absDiff = 0;
        for (int value: values) {
            absDiff += Math.abs(value - mean);
        }
        return absDiff / values.size();
    }

    // Max Pixel Difference per channel
    public static double calculateMPDForChannel(double[] minMax) {
        return minMax[1] - minMax[0];
    }

    // Entropi per channel
    private static double calculateEntropyForChannel(List<Integer> values) {
        int[] freq = new int[256];
        if (values.size() == 0) {return 0.0;}   
        for (int value : values) {
            freq[value]++;
        }

        double entropy = 0.0;
        for (int count : freq) {
            if (count > 0) {
                double p = (double) count / values.size();
                entropy += p * (Math.log(p) / Math.log(2)); // log2(p) = ln(p)/ln(2)
            }
        }
        return -entropy;
    }



    // * Get error value based on input
    public static double getError(ImageInfo imageInfo) {
        BufferedImage image = imageInfo.getOriginalImage();
        double error = 0;

        List<Integer> redValues = ChannelUtil.getChannelValues(image, 0, 0, image.getWidth(), image.getHeight(), ChannelUtil.redChannel);
        List<Integer> greenValues = ChannelUtil.getChannelValues(image, 0, 0, image.getWidth(), image.getHeight(), ChannelUtil.greenChannel);
        List<Integer> blueValues = ChannelUtil.getChannelValues(image, 0, 0, image.getWidth(), image.getHeight(), ChannelUtil.blueChannel);

        switch (imageInfo.getErrorMethod()) {
            case 1: 
            // Variance
                double[] meansList = ChannelUtil.calculateAllMeans(image, 0, 0, image.getWidth(), image.getHeight());
                double meanR = meansList[0];
                double meanG = meansList[1];
                double meanB = meansList[2];
                double varR = calculateVarianceForChannel(redValues, meanR);
                double varG = calculateVarianceForChannel(greenValues, meanG);
                double varB = calculateVarianceForChannel(blueValues, meanB);
                
                error = (varR + varG + varB) / 3.0;
                break;
    
            case 2: 
            // MAD
                meansList = ChannelUtil.calculateAllMeans(image, 0, 0, image.getWidth(), image.getHeight());
                meanR = meansList[0];
                meanG = meansList[1];
                meanB = meansList[2];

                double madR = calculateMADForChannel(redValues, meanR);
                double madG = calculateMADForChannel(greenValues, meanG);
                double madB = calculateMADForChannel(blueValues, meanB);
                
                error = (madR + madG + madB) / 3.0;
                break;
    
            case 3: 
            // Max Pixel Difference
                double[] minMaxR = ChannelUtil.getChannelMinMax(image, 0, 0, image.getWidth(), image.getHeight(), ChannelUtil.redChannel);
                double[] minMaxG = ChannelUtil.getChannelMinMax(image, 0, 0, image.getWidth(), image.getHeight(), ChannelUtil.greenChannel);
                double[] minMaxB = ChannelUtil.getChannelMinMax(image, 0, 0, image.getWidth(), image.getHeight(), ChannelUtil.blueChannel);

                double mpdR = calculateMPDForChannel(minMaxR);
                double mpdG = calculateMPDForChannel(minMaxG);
                double mpdB = calculateMPDForChannel(minMaxB);
                error = (mpdR + mpdG + mpdB) / 3.0;
                break;
    
            case 4: 
            // Entropy
                double entropyR = calculateEntropyForChannel(redValues);
                double entropyG = calculateEntropyForChannel(greenValues);
                double entropyB = calculateEntropyForChannel(blueValues);
                error = (entropyR + entropyG + entropyB) / 3.0;
                break;
    
            case 5: 
            // SSIM (Simplified)
                meansList = ChannelUtil.calculateAllMeans(image, 0, 0, image.getWidth(), image.getHeight());
                meanR = meansList[0];
                meanG = meansList[1];
                meanB = meansList[2];
                varR = calculateVarianceForChannel(redValues, meanR);
                varG = calculateVarianceForChannel(greenValues, meanG);
                varB = calculateVarianceForChannel(blueValues, meanB);

                final int L = 255;
                final double K2 = 0.03;
                final double wR = 0.299;
                final double wG = 0.587;
                final double wB = 0.114;

                double C2 = Math.pow(K2 * L, 2);

                double ssimR = C2 / (varR + C2);
                double ssimG = C2 / (varG + C2);
                double ssimB = C2 / (varB + C2);
                double ssimTotal = wR * ssimR + wG * ssimG + wB * ssimB;
                error = 1 - ssimTotal;
                break;
        }
        
        return error;
    }   



}
