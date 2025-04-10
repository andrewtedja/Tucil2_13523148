import java.awt.image.BufferedImage;
import java.io.File;

class ImageInfo {
    private String inputPath;
    private String outputPath;
    private int errorMethod;
    private double threshold;
    private int minBlockSize;
    private BufferedImage originalImage;
    private double targetCompressionPercentage;
    private String gifPath;
    private String inputFormat;

    
    // ctor
    public ImageInfo(BufferedImage image, String inputPath, String outputPath,  int errorMethod, double threshold, int minBlockSize, double targetCompressionPercentage, String gifPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;

        this.errorMethod = errorMethod;
        this.threshold = threshold;
        this.minBlockSize = minBlockSize;

        this.originalImage = image;
        this.targetCompressionPercentage = targetCompressionPercentage;
        this.gifPath = gifPath;

        this.inputFormat = getExtension(new File(inputPath));
        // this.outputFormat = getExtension(new File(outputPath));
    }

    public boolean isTargetPercentageMode() {
        return targetCompressionPercentage > 0 && targetCompressionPercentage <= 1;
    }
    
    public long getInputSize(String filePath) {
        File oriFile = new File(filePath);
        if (!oriFile.exists()) {
            System.err.println("Error: file doesn't exist");
            return -1; 
        }
        return oriFile.length();
    }

    // getter setters
    public String getInputPath() { return inputPath; }
    public String getOutputPath() { return outputPath; }

    public int getErrorMethod() { return errorMethod; }
    public double getThreshold() { return threshold; }
    public int getMinBlockSize() { return minBlockSize; }
    public void setThreshold(double threshold) { this.threshold = threshold; }

    public BufferedImage getOriginalImage() { return originalImage; }
    public void setOriginalImage(BufferedImage image) { 
        this.originalImage = image; 
    }

    public double getTargetCompressionPercentage() { return targetCompressionPercentage; }
    public String getGifPath() { return gifPath; }

    public String getInputFormat() { return inputFormat; }
    public void setInputFormat(String inputFormat) { this.inputFormat = inputFormat; }

    private static String getExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
    

}
