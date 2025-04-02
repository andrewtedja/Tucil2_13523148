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
    private String gifFolderPath;
    private String inputFormat;
    // private String outputFormat;

    // private int area;
    
    // ctor
    public ImageInfo(BufferedImage image, String inputPath, String outputPath,  int errorMethod, double threshold, int minBlockSize, double targetCompressionPercentage, String gifFolderPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;

        this.errorMethod = errorMethod;
        this.threshold = threshold;
        this.minBlockSize = minBlockSize;

        this.originalImage = image;
        this.targetCompressionPercentage = targetCompressionPercentage;
        this.gifFolderPath = gifFolderPath;

        this.inputFormat = getExtension(new File(inputPath));
        // this.outputFormat = getExtension(new File(outputPath));
    }

    public boolean isTargetPercentageMode() {
        return targetCompressionPercentage > 0 && targetCompressionPercentage < 1;
    }

    // public int getArea() { return area; }
    
    // getter setters
    public String getInputPath() { return inputPath; }
    public String getOutputPath() { return outputPath; }
    public int getErrorMethod() { return errorMethod; }
    public double getThreshold() { return threshold; }
    public int getMinBlockSize() { return minBlockSize; }
    public BufferedImage getOriginalImage() { return originalImage; }
    public void setOriginalImage(BufferedImage image) { 
        this.originalImage = image; 
        // this.area = image.getWidth() * image.getHeight();
    }
    
    public double getTargetCompressionPercentage() { return targetCompressionPercentage; }
    public String getGifFolderPath() { return gifFolderPath; }

    public String getInputFormat() { return inputFormat; }
    public void setInputFormat(String inputFormat) { this.inputFormat = inputFormat; }

    // public String getOutputFormat() { return outputFormat; }
    // public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }

    private static String getExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
    

}
