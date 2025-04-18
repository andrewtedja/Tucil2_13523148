import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import java.io.File;

import java.awt.Color;

public class Compressor {
    private ImageInfo imageInfo;
    private QuadTreeNode root;
    private int nodeCnt;
    private int maxDepth;
    private long executionTime;
    private String gifPath;
    private GifWriter gifWriter;

    private final int tolerance = 100;

    // * Ctor
    public Compressor(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
        this.gifPath = imageInfo.getGifPath();
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
    
    // * Compress
    public void compress() {
        if (imageInfo.isTargetPercentageMode()) {
            compressToTargetPercentage();
            return;
        }   
        // System.out.println("testafter");
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
        if (node.getWidth() < 2 || node.getHeight() < 2) {
            return false;
        } // stop kalau block sudah terlalu kecil

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
            nodeCnt += 4;
            maxDepth = Math.max(maxDepth, currDepth + 1);
 
            buildTree(node.getTopLeft(), currDepth + 1);
            buildTree(node.getTopRight(), currDepth + 1);
            buildTree(node.getBottomLeft(), currDepth + 1);
            buildTree(node.getBottomRight(), currDepth + 1);
        } else {
            setNodeColor(node);
            node.setIsLeaf(true); 
        }
    }


    // * Target Percentage Feature (BONUS)
    public void compressToTargetPercentage() {
        long startTime = System.nanoTime();

        BufferedImage image = imageInfo.getOriginalImage();
        long originalSize = imageInfo.getInputSize(imageInfo.getInputPath());

        double targetRatio = imageInfo.getTargetCompressionPercentage();
        long targetSize = (long) ((1 - targetRatio) * originalSize);

        System.out.println("Original size: " + originalSize + " bytes, Target size: " + targetSize + " bytes");
        System.out.println("\nPlease wait, this may take a while...");

        // initial threshold (based on effective range in error method divided by 2)
        double threshold;
        double minThreshold, maxThreshold;
        
        switch (imageInfo.getErrorMethod()) {
            case 1: // var
                threshold = 8192; 
                minThreshold = 1;
                maxThreshold = 100000; 
                break; 
            case 2: // mad
                threshold = 64; 
                minThreshold = 1;
                maxThreshold = 1000; 
                break; 
            case 3: // mpd
                threshold = 128; 
                minThreshold = 1;
                maxThreshold = 1000; 
                break; 
            case 4: // entropy
                threshold = 4; 
                minThreshold = 0.1;
                maxThreshold = 50; 
                break; 
            case 5: // ssim
                threshold = 0.5; 
                minThreshold = 0.01;
                maxThreshold = 1.0; 
                break; 
            default: 
                threshold = 8192; 
                minThreshold = 1;
                maxThreshold = 100000; 
                break;
        }
        
        long minSize = 0;
        long maxSize = 0;
        
        try {
            // Test minimum threshold & juga maximum threshold
            imageInfo.setThreshold(minThreshold);
            this.root = new QuadTreeNode(0, 0, image.getWidth(), image.getHeight());
            this.maxDepth = 1;
            this.nodeCnt = 1;
            buildTree(root, 1);
            BufferedImage minImage = createCompressedImage();
            minSize = getImageSize(minImage, imageInfo.getInputFormat());
            
            imageInfo.setThreshold(maxThreshold);
            this.root = new QuadTreeNode(0, 0, image.getWidth(), image.getHeight());
            this.maxDepth = 1;
            this.nodeCnt = 1;
            buildTree(root, 1);
            BufferedImage maxImage = createCompressedImage();
            maxSize = getImageSize(maxImage, imageInfo.getInputFormat());
            
            boolean isTargetAchievable = (minSize >= targetSize && maxSize <= targetSize) || 
                                        (minSize <= targetSize && maxSize >= targetSize);
            
            if (!isTargetAchievable) {
                // use closest threshold
                if (Math.abs(minSize - targetSize) < Math.abs(maxSize - targetSize)) {
                    threshold = minThreshold;
                } else {
                    threshold = maxThreshold;
                }
            } else {
                int maxIter = 30;
                int iter = 0;
                double currThreshold = threshold;
                
                while (iter < maxIter) {
                    imageInfo.setThreshold(currThreshold);
                    
                    // Reset tree
                    this.root = new QuadTreeNode(0, 0, image.getWidth(), image.getHeight());
                    this.maxDepth = 1;
                    this.nodeCnt = 1;
                    buildTree(root, 1);
                    
                    BufferedImage compressedImage = createCompressedImage();
                    long compressedSize = getImageSize(compressedImage, imageInfo.getInputFormat());
                    
                    if (Math.abs(compressedSize - targetSize) <= tolerance) {
                        threshold = currThreshold;
                        break;
                    }
                    
                    // binary search for best threshold
                    // ? size too large -> less compression -> more threshold
                    // ? size too small -> more compression -> less threshold
                    if (compressedSize > targetSize) {
                        minThreshold = currThreshold;
                        minSize = compressedSize;
                    } else {
                        maxThreshold = currThreshold;
                        maxSize = compressedSize;
                    }
                    currThreshold = (minThreshold + maxThreshold) / 2;
                    iter++;
                }
                
                threshold = currThreshold;
            }
            
            // Final compress (with best threshold)
            imageInfo.setThreshold(threshold);
            this.root = new QuadTreeNode(0, 0, image.getWidth(), image.getHeight());
            this.maxDepth = 1;
            this.nodeCnt = 1;
            buildTree(root, 1);

            System.out.println("Final threshold: " + threshold);
            
            long endTime = System.nanoTime();
            this.executionTime = (endTime - startTime) / 1000000;
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    
    // ? HELPER FUNCTIONS
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
            return; 
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
    
    // * Create GIF (bonus)
    public void createGif() {
        if (root == null || gifPath == null || gifPath.isEmpty()) {
            return;
        }
        
        try {
            ImageOutputStream output = new FileImageOutputStream(new File(gifPath));
            BufferedImage originalImage = imageInfo.getOriginalImage();
            GifWriter writer = new GifWriter(output, originalImage.getType(), 1000, true);
            
            setNodeColor(root);

            // tiap depth, buat frame
            for (int depth = 1; depth <= maxDepth; depth++) {
                BufferedImage frameImage = new BufferedImage(
                    originalImage.getWidth(), 
                    originalImage.getHeight(), 
                    originalImage.getType());
                
                createFrameEachDepth(root, frameImage, depth, 1);
                writer.writeToSequence(frameImage);
            }
            writer.writeToSequence(createCompressedImage());            
            writer.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Create gif frame (per depth of quadtree)
    private void createFrameEachDepth(QuadTreeNode node, BufferedImage frame, int maxDepth, int currentDepth) {
        if (node == null) {
            return;
        }
        
        // If leaf, set color with avg rgb
        if (currentDepth >= maxDepth || node.getIsLeaf()) {
            setNodeColor(node);
            Color color = new Color(
                Math.round((float)node.getMeanR()),
                Math.round((float)node.getMeanG()),
                Math.round((float)node.getMeanB())
            );
            
            for (int y = node.getY(); y < node.getY() + node.getHeight(); y++) {
                for (int x = node.getX(); x < node.getX() + node.getWidth(); x++) {
                    frame.setRGB(x, y, color.getRGB());
                }
            }
        } else {
            createFrameEachDepth(node.getTopLeft(), frame, maxDepth, currentDepth + 1);
            createFrameEachDepth(node.getTopRight(), frame, maxDepth, currentDepth + 1);
            createFrameEachDepth(node.getBottomLeft(), frame, maxDepth, currentDepth + 1);
            createFrameEachDepth(node.getBottomRight(), frame, maxDepth, currentDepth + 1);
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
            throw new IOException("Error: " + e.getMessage());
        }
    }   

    // getters
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