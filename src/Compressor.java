import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.Color;

public class Compressor {
    private ImageInfo imageInfo;
    private QuadTreeNode root;
    private int nodeCnt;
    private int maxDepth;
    private long executionTime;

    private final int tolerance = 1000;

    // * Ctor
    public Compressor(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }
    
    // * Methods
    public BufferedImage createCompressedImage() {
        if (root == null) {
            throw new IllegalStateException("Error: Compression tree is not built yet.");
        }
        BufferedImage compressedImage = new BufferedImage(imageInfo.getOriginalImage().getWidth(), imageInfo.getOriginalImage().getHeight(), imageInfo.getOriginalImage().getType());
        colorImage(root, compressedImage);
        return compressedImage;
    }
    
    // main compression method
    public void compress() {
        if (imageInfo.isTargetPercentageMode()) {
            compressToTargetPercentage();
            return;
        }   

        long startTime = System.nanoTime();

        BufferedImage image = imageInfo.getOriginalImage();
        this.root = new QuadTreeNode(0, 0, image.getWidth(), image.getHeight());
        this.maxDepth = 1;
        this.nodeCnt = 1;

        buildTree(root, 1);

        long endTime = System.nanoTime();
        this.executionTime = (endTime - startTime) / 1000000;
    }

    // Syarat split check
    private boolean shouldSplit(QuadTreeNode node) {
        double error = ErrorCalculation.getError(
            node, 
            imageInfo.getOriginalImage(), 
            imageInfo.getErrorMethod()
        );

        return error > imageInfo.getThreshold() && 
        ((node.getHeight()/2) * (node.getWidth()/2) >= imageInfo.getMinBlockSize());
    }

    // DnC algorithm
    private void buildTree(QuadTreeNode node, int currDepth) {

        if (shouldSplit(node)) {
            node.split();
            nodeCnt += 4; // asumsi node count adalah semua node
            maxDepth = Math.max(maxDepth, currDepth + 1);
 
            buildTree(node.getTopLeft(), currDepth + 1);
            buildTree(node.getTopRight(), currDepth + 1);
            buildTree(node.getBottomLeft(), currDepth + 1);
            buildTree(node.getBottomRight(), currDepth + 1);
        } else {
            setNodeColor(node);
        }
    }

    public void compressToTargetPercentage() {
        long startTime = System.nanoTime();
        BufferedImage image = imageInfo.getOriginalImage();
        long originalSize = imageInfo.getInputSize(imageInfo.getInputPath());
        double targetRatio = imageInfo.getTargetCompressionPercentage();
        long targetSize = (long) ((1 - targetRatio) * originalSize);
        System.out.println("Original size: " + originalSize + ", Target size: " + targetSize);
        
        double threshold = 5.0;
        double step = 2.5;  
        double direction = 1.0;  
        boolean crossedTarget = false;  
        
        int maxIterations = 50;
        int iteration = 0;
        long lastSize = 0;
        long lastDiff = Long.MAX_VALUE;
        
        try {
            while (iteration < maxIterations) {
                imageInfo.setThreshold(threshold);
                
                // Reset and build tree
                this.root = new QuadTreeNode(0, 0, image.getWidth(), image.getHeight());
                this.maxDepth = 1;
                this.nodeCnt = 1;
                buildTree(root, 1);
                
                BufferedImage compressedImage = createCompressedImage();
                long compressedSize = getImageSize(compressedImage, imageInfo.getInputFormat());
                long currentDiff = Math.abs(compressedSize - targetSize);
                
                // DEBUG
                System.out.println("Iteration " + iteration + 
                                 ": Size=" + compressedSize + 
                                 ", Target=" + targetSize + 
                                 ", Threshold=" + threshold +
                                 ", Step=" + step);
                
                // break if close to target
                if (currentDiff <= tolerance) {
                    break;
                }
                
                // cross -> reverse and step becomes half
                if (iteration > 0) {
                    if (currentDiff > lastDiff) {
                        direction *= -1;
                        step *= 0.5;
                        crossedTarget = true;
                    } else if (compressedSize == lastSize) {
                        step *= 2.0;
                    }
                }
                
                threshold += direction * step;
                threshold = Math.max(0.1, Math.min(255.0, threshold));
                
                lastSize = compressedSize;
                lastDiff = currentDiff;
                iteration++;
            }
            
            long endTime = System.nanoTime();
            this.executionTime = (endTime - startTime) / 1000000;
        } catch (IOException e) {
            System.err.println("Error during compression: " + e.getMessage());
        }
    }
    
    
    // ? HELPER
    // Set avg rgb for each node
    public void setNodeColor(QuadTreeNode node) {
        BufferedImage image = imageInfo.getOriginalImage();
        int height = node.getHeight();
        int width = node.getWidth();
        int x = node.getX();
        int y = node.getY();

        double[] meansList = ChannelUtil.calculateAllMeans(image, x, y, width, height);
        node.setMeanValues(meansList[0], meansList[1], meansList[2]);
    }

    // fill the new image with color
    public void colorImage(QuadTreeNode node, BufferedImage target) {
        if (node == null) {
            return; // Skip null nodes
        }
        if (node.getIsLeaf()) {
            Color color = new Color(
                Math.round((float)node.getMeanR()),
                Math.round((float)node.getMeanG()),
                Math.round((float)node.getMeanB())
            );
            for (int y = node.getY(); y < node.getY() + node.getHeight(); y++) {
                for (int x = node.getX(); x < node.getX() + node.getWidth(); x++) {
                    target.setRGB(x, y, color.getRGB());
                }
            }
        } else {
            colorImage(node.getTopLeft(), target);
            colorImage(node.getTopRight(), target);
            colorImage(node.getBottomLeft(), target);
            colorImage(node.getBottomRight(), target);
        }
    }
    
    // BAOS
    public static long getImageSize(BufferedImage image , String format) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, format, baos);
            baos.flush();
            long size = baos.size();
            baos.close();
            return size;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error calculating image size: " + e.getMessage());
        }
    }   

    public int getMaxDepth() {
        return maxDepth;
    }
    
    public int getNodeCount() {
        return nodeCnt;
    }

    public long getExecutionTime() {
        return executionTime;
    }

}   