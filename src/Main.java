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

        BufferedImage image = imageInfo.getOriginalImage();

        // try {
        //     int originalSizeBytes2 = Compressor.getImageSizeInBytes(image, imageInfo.getInputFormat());
        //     System.out.println("Image size (BAOS method): " + originalSizeBytes2 + " bytes");
        // } catch (IOException e) {
        //     System.err.println("Error calculating image size: " + e.getMessage());
        // }
        

        Compressor compressor = new Compressor(imageInfo);
        compressor.compress();
        BufferedImage compressedImage = compressor.createCompressedImage();
        long executionTime = compressor.getExecutionTime();
        System.out.println("Execution time: " + executionTime + " ms");

        // * SAVE
        try {
            ImageIO.write(compressedImage, "jpg", new File(imageInfo.getOutputPath() + "/compressed.jpg"));
            System.out.println("Compressed image saved successfully!");
        } catch (Exception e) {
            System.out.println("Error saving compressed image: " + e.getMessage());
        }

        

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