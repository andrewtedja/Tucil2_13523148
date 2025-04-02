import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import java.awt.Color;

public class Compressor {
    private ImageInfo imageInfo;
    private QuadTreeNode root;
    private int nodeCnt;
    private int maxDepth;
    private long executionTime;
    private int originalSizeBytes;
    private int compressedSizeBytes;
    private final int tolerance = 100;


    
    public Compressor(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }
    
    public BufferedImage createCompressedImage() {
        System.out.println("TESTCOMPRESS");
        BufferedImage compressedImage = new BufferedImage(imageInfo.getOriginalImage().getWidth(), imageInfo.getOriginalImage().getHeight(), imageInfo.getOriginalImage().getType());
        colorImage(root, compressedImage);
        return compressedImage;
    }
    
    public void compress() {
        long startTime = System.nanoTime();
        // System.out.println("TEST1");

        BufferedImage image = imageInfo.getOriginalImage();
        this.root = new QuadTreeNode(0, 0, image.getWidth(), image.getHeight());
        this.maxDepth = 1;
        this.nodeCnt = 1;
        buildTree(root, 1);
        // System.out.println("TEST3");

        long endTime = System.nanoTime();
        this.executionTime = (endTime - startTime) / 1000000;
    }


    private boolean shouldSplit(QuadTreeNode node) {
        double error = ErrorCalculation.getError(
            node, 
            imageInfo.getOriginalImage(), 
            imageInfo.getErrorMethod()
        );
        return error > imageInfo.getThreshold() && 
        ((node.getHeight()/2) * (node.getWidth()/2) >= imageInfo.getMinBlockSize());
    }

    private void buildTree(QuadTreeNode node, int currDepth) {

        if (shouldSplit(node)) {
            node.split();
            nodeCnt += 4; // asumsi node count adalah semua node
            maxDepth = Math.max(maxDepth, currDepth + 1);

            // * RECURSSIVE
            buildTree(node.getTopLeft(), currDepth + 1);
            buildTree(node.getTopRight(), currDepth + 1);
            buildTree(node.getBottomLeft(), currDepth + 1);
            buildTree(node.getBottomRight(), currDepth + 1);
        } else {
            setNodeColor(node);
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
        if (node.getIsLeaf()) {
            Color color = new Color(
                (int) node.getMeanR(),
                (int) node.getMeanG(),
                (int) node.getMeanB()
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
    public static int getImageSizeInBytes(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);  
        baos.flush();
        int size = baos.size(); 
        baos.close();
        return size;
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