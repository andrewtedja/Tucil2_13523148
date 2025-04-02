import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class ReadInput {
    public static ImageInfo readInput(String filePath)  {
        try (Scanner scanner = new Scanner(System.in)) {

            // Input image
            // System.out.println("\n> Enter image file path: ");
            // String filePath = scanner.nextLine();

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
            System.out.print("> Enter target compression percentage (floating number, 1.0 = 100%): ");
            double targetCompressionPercentage = scanner.nextDouble();
            System.out.print("> Enter minimum block size: ");
            int minBlockSize = scanner.nextInt();
            System.out.print("> Enter threshold: ");
            double threshold = scanner.nextDouble();
            System.out.print("> Enter output Image folder path: ");
            String outputFolderPath = scanner.next();
            System.out.print("> Enter output GIF folder path: ");
            String gifFolderPath = scanner.next();
                        
            return new ImageInfo(image, filePath, outputFolderPath, errorMethod, threshold, minBlockSize, targetCompressionPercentage, gifFolderPath);

            // Show (TESTING)
            // System.out.println("Image name: " + fileName);
            // System.out.println("Width: " + image.getWidth());
            // System.out.println("Height: " + image.getHeight());
            // System.out.println("Error Method: " + errorMethod);
            // System.out.println("Threshold: " + threshold);
            // System.out.println("Minimum block size: " + minBlockSize);
            // System.out.println("Target compression percentage: " + targetCompressionPercentage);
            // System.out.println("Output folder path: " + outputFolderPath);
            // System.out.println("Gif folder path: " + gifFolderPath);

        }
    }
}
