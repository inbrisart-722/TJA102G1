package com.eventra.member.fileupload;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageCropper {

    // 從 MultipartFile 或 InputStream 裁切
    public static BufferedImage cropCenterSquare(InputStream in) throws IOException {
        BufferedImage originalImage = ImageIO.read(in);
        if (originalImage == null) {
            throw new IllegalArgumentException("無效的圖片檔案");
        }

        int size = Math.min(originalImage.getWidth(), originalImage.getHeight());
        int x = (originalImage.getWidth() - size) / 2;
        int y = (originalImage.getHeight() - size) / 2;

        return originalImage.getSubimage(x, y, size, size);
    }

    // 指定座標裁切
    public static BufferedImage crop(InputStream in, int x, int y, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(in);
        if (originalImage == null) {
            throw new IllegalArgumentException("無效的圖片檔案");
        }
        width = Math.min(width, originalImage.getWidth() - x);
        height = Math.min(height, originalImage.getHeight() - y);
        return originalImage.getSubimage(x, y, width, height);
    }
}
