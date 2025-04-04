import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class ReadInput {
    public static ImageInfo readInput(String filePath)  {
        try (Scanner scanner = new Scanner(System.in)) {
            File file = new File(filePath);
            String fileName = file.getName();
            
            // Validation
            if (!fileName.isEmpty()) {
                if (!file.exists()) {
                    throw new IllegalArgumentException("Error: File does not exist");
                }
                if (!fileName.endsWith(".jpg") && !fileName.endsWith(".png") && !fileName.endsWith(".jpeg")) {
                    throw new IllegalArgumentException("Error: only jpg, png and jpeg files are supported");
                }
            } else {
                throw new IllegalArgumentException("Error: File path is empty");

                
            }
            // read file
            BufferedImage image;
            try {
                image = ImageIO.read(file); 
            } catch (IOException e) {
                throw new IllegalArgumentException("Error loading image");
            } catch (OutOfMemoryError e) {
                throw new IllegalArgumentException("Error: Image too large to load into memory");
            }

            if (image != null) {
                System.out.println("\nImage loaded successfully!");
            } else {
                throw new IllegalArgumentException("Error reading file");
            }

            // Input error
            System.out.println("\nError methods: ");
            System.out.println("[1] Variance");
            System.out.println("[2] MAD");
            System.out.println("[3] MaxPixelDifference");
            System.out.println("[4] Entropy");
            System.out.println("[5] SSIM (BONUS)");
            System.out.print("\n> Enter error method [1-5]: ");
            int errorMethod = scanner.nextInt();
            
            // Validate error method
            if (errorMethod < 1 || errorMethod > 5) {
                throw new IllegalArgumentException("Error: Invalid error method selected");
            }

            // inputs
            Main.clearScreen();
            System.out.println("\nIdeal threshold ranges for error methods:");
            System.out.println("[1] Variance: 0 - 16834.0");
            System.out.println("[2] MAD: 0 - 127.5");
            System.out.println("[3] MaxPixelDifference: 0 - 255.0");
            System.out.println("[4] Entropy: 0 - 8.0");
            System.out.println("[5] SSIM (BONUS): 0 - 1");


            System.out.print("\n> Enter threshold: ");
            double threshold = scanner.nextDouble();
            System.out.print("> Enter minimum block size: ");
            int minBlockSize = scanner.nextInt();

            
            System.out.print("> Enter target compression percentage (floating number, 1.0 = 100%, 0 if want to turn of this mode): ");
            double targetCompressionPercentage = scanner.nextDouble();

            System.out.print("> Enter output file path [ABSOLUTE]: ");
            String outputPath = scanner.next();
            System.out.print("> Enter output GIF folder path: ");
            String gifFolderPath = scanner.next();
                        
            return new ImageInfo(image, filePath, outputPath, errorMethod, threshold, minBlockSize, targetCompressionPercentage, gifFolderPath);
        }
    }
}
