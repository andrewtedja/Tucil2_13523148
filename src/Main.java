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
import java.nio.file.Files;

import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        clearScreen();

        // * INPUT AND READ
        // Input image
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n");
        System.out.println("                ,@@@@@@@,     @@@@@,");
        System.out.println("        ,,,.   ,@@@@@@/@@,  .oo8888o. .#o44##.");
        System.out.println("     ,&%%&%&&%,@@@@@/@@@@@@,8888\\88/8o#/848/80");
        System.out.println("    ,%&\\%&&%&&%,@@@\\@@@/@@@88\\88888/##84#880#8@");
        System.out.println("    %&&%&%&/%&&%@@\\@@/ /@@@88888\\8884#0884880888");
        System.out.println("    %&&%/ %&%%&&@@\\ V /@@' `88\\8 `/88'%&&%/ %&'");
        System.out.println("    `&%\\ ` /%&'    | |        \\   /     \\  /");
        System.out.println("        | |        | |         | |      | |    ");
        System.out.println("        | |        | |         | |      | |    ");
        System.out.println("\n Q U A D T R E E   I M A G E   C O M P R E S S I O N");

        System.out.println("\n> Enter image file path [ABSOLUTE]: ");
        String filePath = scanner.nextLine();

        if (filePath.isEmpty()) {
            System.err.println("Error: Input file path is empty.");
            scanner.close();
            return;
        }

        // READ
        ImageInfo imageInfo = ReadInput.readInput(filePath);


        // validations
        if (imageInfo == null) {
            System.err.println("Error: Failed to read image.");
            scanner.close();
            return;
        }
        BufferedImage image = imageInfo.getOriginalImage();

        if (image == null) {
            System.err.println("Error: Failed to read image.");
            scanner.close();
            return;
        }

        // Get size
        long oriSize = imageInfo.getInputSize(filePath);
        if (oriSize <= 0) {
            System.err.println("Error: Invalid original file size.");
            scanner.close();
            return;
        }
        
        // * COMPRESSION
        Compressor compressor = new Compressor(imageInfo);
        compressor.compress();
        BufferedImage compressedImage = compressor.createCompressedImage();

        clearScreen();
        // * OUTPUT
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("|                              COMPRESSION RESULTS                             |");
        System.out.println("|       IF2211 Strategi Algoritma - Kompresi Gambar Dengan Metode Quadtree     |");
        System.out.println("--------------------------------------------------------------------------------");

        try {
            
            String outputPath = imageInfo.getOutputPath();
            File outputFile = new File(outputPath);
            ImageIO.write(compressedImage, imageInfo.getInputFormat(), outputFile);

            long compressedSize = Compressor.getImageSize(compressedImage, imageInfo.getInputFormat());
            // Display results
            System.out.println("Compressed image saved successfully at: [" + outputPath + "]!\n");

            System.out.println("<< Execution time: " + compressor.getExecutionTime() + " ms >>\n");
            System.out.println("Original file size: " + oriSize + " bytes");
            System.out.println("Compressed file size: " + compressedSize + " bytes");
            
            float compressionPercentage = (float) (1 - ((float) compressedSize / (float) oriSize)) * 100;
            System.out.println(String.format("Compression percentage: %.3f%%", compressionPercentage));
            System.out.println("Quadtree depth: " + compressor.getMaxDepth());
            System.out.println("Node count: " + compressor.getNodeCount());
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
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
