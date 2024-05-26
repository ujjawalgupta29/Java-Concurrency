package Performance;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessing {

    public static final String SOURCE_FILE = "./resources/many-flowers.jpg";
    public static final String DESTINATION_FILE_1 = "./resources/many-flowers_new_single1.jpg";
    public static final String DESTINATION_FILE_2 = "./resources/many-flowers_new_single2.jpg";
    public static void main(String[] args) throws IOException {
        File inputFile= new File(SOURCE_FILE);
        System.out.println(inputFile.getAbsolutePath());
        System.out.println(inputFile.exists());
        BufferedImage orgImg = ImageIO.read(inputFile);
        BufferedImage resImg = new BufferedImage(orgImg.getWidth(), orgImg.getHeight(), BufferedImage.TYPE_INT_RGB);

        long startTime = System.currentTimeMillis();
        multiThreadedRecolor(orgImg, resImg, 8);
//        singleThreadedRecolor(orgImg, resImg);
        long endTime = System.currentTimeMillis();

        System.out.println("Time Taken: " + (endTime - startTime));

        File output = new File(DESTINATION_FILE_2);
        ImageIO.write(resImg, "jpg", output);
    }

    public static void multiThreadedRecolor(BufferedImage orgImg, BufferedImage resImg, int numThreads) {
        List<Thread> threads = new ArrayList<>();
        int width = orgImg.getWidth();
        int height = orgImg.getHeight();

        for(int i=0; i<numThreads; i++) {
            final int multiplier = i;
            Thread thread = new Thread(() -> {
                int leftCorner = 0;
                int topCorner = height * multiplier;
                recolorImage(orgImg, resImg, leftCorner, topCorner, orgImg.getWidth(), orgImg.getHeight());
            });
            threads.add(thread);
        }

        for(Thread t : threads) {
            t.start();
        }

        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void singleThreadedRecolor(BufferedImage orgImg, BufferedImage resImg) {
        recolorImage(orgImg, resImg, 0, 0, orgImg.getWidth(), orgImg.getHeight());
    }

    public static void recolorImage(BufferedImage orgImg, BufferedImage resImg, int leftCorner,
                                    int topCorner, int width, int height) {
        for(int x=leftCorner; x<topCorner+width && x < orgImg.getWidth(); x++) {
            for(int y=topCorner; y < topCorner + height && y < orgImg.getHeight(); y++) {
                recolorPixel(orgImg, resImg, x, y);
            }
        }
    }

    public static void recolorPixel(BufferedImage orgImag, BufferedImage resImg, int x, int y) {
        int rgb = orgImag.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;

        if(isShadeOfGrey(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        }
        else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }

        int newRgb = createRGB(newRed, newGreen, newBlue);
        setRGB(resImg, x, y, newRgb);
    }

    public static void setRGB(BufferedImage img, int x, int y, int rgb) {
        img.getRaster().setDataElements(x, y, img.getColorModel().getDataElements(rgb, null));
    }

    public static boolean isShadeOfGrey(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(blue - green) < 30;
    }

    public static int createRGB(int red, int green , int blue) {
        int rgb = 0;
        rgb |= blue;
        rgb |= (green << 8);
        rgb |= (red << 16);

        rgb |= 0xFF000000;

        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return (rgb & 0x000000FF);
    }
}
