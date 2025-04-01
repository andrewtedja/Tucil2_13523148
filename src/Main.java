/*
    todo
    * INPUT:
    ? - inputFilePath: string for image path
    ? - errorMethod: use numbers for input, method of error calculation
    ? - threshold: finding error, range according to errorMethod
    ? - minBlockSize: minimum block size
    ! - targetCompressionPercentage: maximum block size (BONUS)
    ? - outputFolderPath: string for output folder path
    ! - gifFolderPath: string for output gif folder path (BONUS)

    * OUTPUT:
    ? - executionTime: execution time
    ? - originalImageSize: size of image before compression
    ? - compressedImageSize: size of image after compression
    ? - compressionPercentage: compression percentage
    ? - quadtreeDepth: depth of quadtree
    ? - nodeCount: number of nodes in quadtree
    ? - compressedImage: number of leaves in quadtree
    ! - gifImage: gif image of quadtree (BONUS)

    * Classes:
    ? - Main: main for CLI & testing
    ? - ImageInfo: store data
    ? - QuadTreeNode: every node in quadtree
    ? - Compressor: compressor main class, DnC algorithm
    ? - ErrorCalculation: Variance, MAD, MaxPixelDifference, Entropy, 
    ? - Statistics: calculate statistics
    ! SSIM (BONUS)
    ? - ImageHandler: image load, save
    ! - GifWriter: gif maker (BONUS)
    
    ! BONUS: TargetCompressionPercentage, SSIM, GifWriter
    * FLOW: 
    1. Load
    2. Create root node
    3. For each node: 
        - calculate error
        - if (Error > threshold && size(width*height) > minBlockSize) -> split into 4 children, recursively process each child
        - else -> calculate average rgb for node, mark as leaf node
    4. generate compressed from quadtree -> for each leaf node, fill area with avg rgb
    5. calculate statistics (execTime, size, compression %, depth, node count)
    6. Save compressed image
*/

import java.awt.image.BufferedImage;
import java.util.Scanner;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        clearScreen();
        // Input image
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n> Enter image file path: ");
        String filePath = scanner.nextLine();


        // READ
        ImageInfo imageInfo = ReadInput.readInput(filePath);

        // TEST
        BufferedImage image = imageInfo.getOriginalImage();


        QuadTreeNode root = new QuadTreeNode(0, 0, image.getWidth(), image.getHeight());
        double error = ErrorCalculation.getError(root, image, imageInfo.getErrorMethod());
        System.out.println("Error: " + error);

        Compressor compressor = new Compressor(imageInfo);
        // System.out.println("TEST1");
        compressor.compress();
        // System.out.println("TEST1");
        BufferedImage compressedImage = compressor.createCompressedImage();
        // System.out.println("TEST1");
        try {
            ImageIO.write(compressedImage, "jpg", new File(imageInfo.getOutputPath() + "/compressed.jpg"));
        } catch (Exception e) {
            System.out.println("Error saving compressed image: " + e.getMessage());
        }
        // ! TESTING
        // BufferedImage image = imageInfo.getOriginalImage();
        // int x = 0;
        // int y = 0;
        // int width = image.getWidth();
        // int height = image.getHeight();
        // double[] redMinMax = ChannelUtil.getChannelMinMax(image, x, y, width, height, ChannelUtil.redChannel);
        // double redMin = redMinMax[0]; // Min value for Red channel
        // double redMax = redMinMax[1]; // Max value for Red channel
        // System.out.println(imageInfo.getArea());
        // System.out.println("Min block size:");
        // System.out.println(imageInfo.getMinBlockSize());
        // System.out.println("Width: ");
        // System.out.println(imageInfo.getOriginalImage().getWidth());
        // System.out.println("Height: ");
        // System.out.println(imageInfo.getOriginalImage().getHeight());

        scanner.close();
    }

    public static void clearScreen() {
        try {
            // For Windows
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

}