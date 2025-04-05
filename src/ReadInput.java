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

            System.out.println("\nError methods: ");
            System.out.println("[1] Variance");
            System.out.println("[2] MAD");
            System.out.println("[3] MaxPixelDifference");
            System.out.println("[4] Entropy");
            System.out.println("[5] SSIM (BONUS)");
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

            // inputs
            Main.clearScreen();
            System.out.println("\nYour chosen method: [" + errorMethod + "] ");
            System.out.println("\nIdeal threshold ranges for error methods:");
            System.out.println("[1] Variance: 0 - 16834.0");
            System.out.println("[2] MAD: 0 - 127.5");
            System.out.println("[3] MaxPixelDifference: 0 - 255.0");
            System.out.println("[4] Entropy: 0 - 8.0");
            System.out.println("[5] SSIM (BONUS): 0 - 1");
            

            double threshold;
            System.out.print("\n> Enter threshold: ");
            threshold = scanner.nextDouble();

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

            double targetCompressionPercentage;
            while (true) {
                try {
                    System.out.print("> Enter target compression percentage (floating number, 1.0 = 100%, 0 if want to turn off this mode): ");
                    targetCompressionPercentage = scanner.nextDouble();
                    if (targetCompressionPercentage >= 0 && targetCompressionPercentage <= 1) {
                        break;
                    }
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                }
                System.err.println("Error: Invalid target compression percentage. Please reenter.\n");
            }

            System.out.print("> Enter output file path [ABSOLUTE]: ");
            String outputPath = scanner.next();
            System.out.print("> Enter output GIF file path [ABSOLUTE]: ");
            String gifPath = scanner.next();
                        
            return new ImageInfo(image, filePath, outputPath, errorMethod, threshold, minBlockSize, targetCompressionPercentage, gifPath);
        }
    }
}

