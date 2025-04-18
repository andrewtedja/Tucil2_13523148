import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class ReadInput {
    public static ImageInfo readInput(String filePath)  {
        try (Scanner scanner = new Scanner(System.in)) {
            File file = new File(filePath);
            while (!file.isAbsolute()) {
                System.out.println("ERROR! Please provide an absolute file path:");
                filePath = scanner.nextLine();
                file = new File(filePath);
            }
            String fileName = file.getName();
            
            // Validation
            if (fileName.isEmpty()) {
                System.err.println("Error: File path is empty");
                return null;
            }

            if (!file.exists()) {
                System.err.println("Error: File does not exist (path invalid)");
                return null;
            }

            if (!fileName.endsWith(".jpg") && !fileName.endsWith(".png") && !fileName.endsWith(".jpeg")) {
                System.err.println("Error: only jpg, png and jpeg files are supported");
                return null;
            }

            // read file
            BufferedImage image;
            try {
                image = ImageIO.read(file); 
            } catch (IOException e) {
                System.err.println("Error loading image");
                return null;
            } catch (OutOfMemoryError e) {
                System.err.println("Error: Image too large to load into memory");
                return null;
            }

            if (image == null) {
                System.err.println("Error reading file");
                return null;
            }

            // Input error
            Main.clearScreen();
            System.out.println("\nImage loaded successfully!");

            System.out.println("\n+------------------------------------------+");
            System.out.println("|               ERROR METHODS              |");
            System.out.println("+------------------------------------------+");
            System.out.println("|  1. Variance                             |");
            System.out.println("|  2. Mean Absolute Difference (MAD)       |");
            System.out.println("|  3. Maximum Pixel Difference             |");
            System.out.println("|  4. Entropy                              |");
            System.out.println("|  5. SSIM (Structural Similarity)         |");
            System.out.println("+------------------------------------------+");

            int errorMethod;
            while (true) {
                try {
                    System.out.print("\n> Enter error method [1-5]: ");
                    errorMethod = scanner.nextInt();
                    if (errorMethod >= 1 && errorMethod <= 5) {
                        break;
                    }
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                }
                System.err.println("Error: Invalid error method selected. Please reenter.\n");
            }
            

            double targetCompressionPercentage;
            while (true) {
                try {
                    System.out.print("> Enter target compression percentage (0.0 to 1.0, where 1 = 100%, or enter 0 to disable mode): ");
                    targetCompressionPercentage = scanner.nextDouble();
                    if (targetCompressionPercentage >= 0 && targetCompressionPercentage <= 1) {
                        break;
                    }
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                }
                System.err.println("Error: Invalid target compression percentage. Please reenter.\n");
            }
            
            double threshold = 0;
            if (targetCompressionPercentage == 0) {
                // inputs
                Main.clearScreen();
                System.out.println("\n+-----------------------------------------------+");
                System.out.println("|               COMPRESSION SETTINGS            |");
                System.out.println("+-----------------------------------------------+");
                System.out.println("|  Selected Method: [" + errorMethod + "]                         |");
                System.out.println("+-----------------------------------------------+");
                System.out.println("|  IDEAL THRESHOLD RANGES:                      |");
                System.out.println("|  [1] Variance: 0 - 16834.0                    |");
                System.out.println("|  [2] MAD: 0 - 127.5                           |");
                System.out.println("|  [3] MaxPixelDifference: 0 - 255.0            |");
                System.out.println("|  [4] Entropy: 0 - 8.0                         |");
                System.out.println("|  [5] SSIM: 0 - 1.0                            |");
                System.out.println("+-----------------------------------------------+");
                
                while (true) {
                    try {
                        System.out.print("\n> Enter threshold: ");
                        threshold = scanner.nextDouble();
                        if (threshold >= 0) {
                            break;
                        }
                    }
                    catch (InputMismatchException e) {
                        scanner.nextLine();
                    }
                    System.err.println("Error: Invalid threshold. Please reenter.");
                }
            } 
            
            int minBlockSize;
            while (true) {
                try {
                    System.out.print("> Enter minimum block size: ");
                    minBlockSize = scanner.nextInt();
                    if (minBlockSize > 0) {
                        break;
                    }
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                }
                System.err.println("Error: Invalid minimum block size. Please reenter.\n");
            }

            System.out.print("\n");
            String outputPath;
            while (true) {
                System.out.print("> Enter output file path [ABSOLUTE]: ");
                outputPath = scanner.next();
                File f = new File(outputPath);
                if (!f.isAbsolute()) {
                    System.err.println("Error: Output file path must be absolute. Please reenter.\n");
                    continue;
                }
                if (!outputPath.endsWith(".jpg") && !outputPath.endsWith(".png") && !outputPath.endsWith(".jpeg")) {
                    System.err.println("Error: Output file must end with .jpg, .png or .jpeg. Please reenter.\n");
                    continue;
                }
                break;
            }

            String gifPath;
            while (true) {
                System.out.print("> Enter output GIF file path [ABSOLUTE]: ");
                gifPath = scanner.next();
                File f = new File(gifPath);
                if (!f.isAbsolute()) {
                    System.err.println("Error: Output GIF file path must be absolute. Please reenter.\n");
                    continue;
                }
                if (!gifPath.endsWith(".gif")) {
                    System.err.println("Error: Output GIF file must end with .gif. Please reenter.\n");
                    continue;
                }
                break;
            }
                        
            return new ImageInfo(image, filePath, outputPath, errorMethod, threshold, minBlockSize, targetCompressionPercentage, gifPath); 
        }
    }
}


