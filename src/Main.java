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
    ? - QuadTreeNode: every node in quadtree
    ? - QuadTreeCompressor: compressor main class, DnC algorithm
    ? - ErrorCalculation: Variance, MAD, MaxPixelDifference, Entropy, 
    ? - Statistics: calculate statistics
    ! SSIM (BONUS)
    ? - ImageHandler: image load, save
    ! - GifWriter: gif maker (BONUS)
    
    BONUS: TargetCompressionPercentage, SSIM, GifWriter
*/

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter image file path: ");
            String filePath = scanner.nextLine();

            File file = new File(filePath);
            String fileName = file.getName();

            // Check image type
            if (!fileName.endsWith(".jpg") && !fileName.endsWith(".png") && !fileName.endsWith(".jpeg")) {
                System.err.println("Error: only jpg, png and jpeg files are supported");
                return;
            }

            BufferedImage image;
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
                System.err.println("Error loading image");
                return;
            }

            if (image != null) {
                System.out.println("Image loaded successfully!");
                System.out.println("Width " + image.getWidth());
                System.out.println("Height " + image.getHeight());
            } else {
                System.out.println("Error reading file");
            }
        }
    }
}

