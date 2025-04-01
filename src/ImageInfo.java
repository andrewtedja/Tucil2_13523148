import java.awt.image.BufferedImage;
class ImageInfo {
    private String inputPath;
    private String outputPath;
    private int errorMethod;
    private double threshold;
    private int minBlockSize;
    private BufferedImage originalImage;
    private double targetCompressionPercentage;
    private String gifFolderPath;
    
    // ctor
    public ImageInfo(BufferedImage image, String inputPath, String outputPath, int errorMethod, double threshold, int minBlockSize, double targetCompressionPercentage, String gifFolderPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;

        this.errorMethod = errorMethod;
        this.threshold = threshold;
        this.minBlockSize = minBlockSize;

        this.originalImage = image;
        this.targetCompressionPercentage = targetCompressionPercentage;
        this.gifFolderPath = gifFolderPath;
    }
    
    // getter setters
    public String getInputPath() { return inputPath; }
    public String getOutputPath() { return outputPath; }
    public int getErrorMethod() { return errorMethod; }
    public double getThreshold() { return threshold; }
    public int getMinBlockSize() { return minBlockSize; }
    public BufferedImage getOriginalImage() { return originalImage; }
    public void setOriginalImage(BufferedImage image) { this.originalImage = image; }
    public double getTargetCompressionPercentage() { return targetCompressionPercentage; }
    public String getGifFolderPath() { return gifFolderPath; }
}