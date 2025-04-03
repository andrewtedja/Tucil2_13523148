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
    public static double getError(QuadTreeNode node, BufferedImage image, int errorMethod) {
        double error = 0;
        int height = node.getHeight();
        int width = node.getWidth();
        int x = node.getX();
        int y = node.getY();

        if (height == 0 || width == 0) {
            return 0;
        }

        List<Integer> redValues = ChannelUtil.getChannelValues(image, x, y, width, height, ChannelUtil.redChannel);
        List<Integer> greenValues = ChannelUtil.getChannelValues(image, x, y, width, height, ChannelUtil.greenChannel);
        List<Integer> blueValues = ChannelUtil.getChannelValues(image, x, y, width, height, ChannelUtil.blueChannel);

        switch (errorMethod) {
            case 1: 
            // Variance
                double[] meansList = ChannelUtil.calculateAllMeans(image, x, y, width, height);
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
                meansList = ChannelUtil.calculateAllMeans(image, x, y, width, height);
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
                double[] minMaxR = ChannelUtil.getChannelMinMax(image, x, y, width, height, ChannelUtil.redChannel);
                double[] minMaxG = ChannelUtil.getChannelMinMax(image, x, y, width, height, ChannelUtil.greenChannel);
                double[] minMaxB = ChannelUtil.getChannelMinMax(image, x, y, width, height, ChannelUtil.blueChannel);

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
                meansList = ChannelUtil.calculateAllMeans(image, x, y, width, height);
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
