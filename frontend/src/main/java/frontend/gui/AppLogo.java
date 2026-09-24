package frontend.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public final class AppLogo {

    private static BufferedImage fullLogo;
    private static BufferedImage iconOnly;
    private static boolean fullLoadAttempted = false;
    private static boolean iconLoadAttempted = false;
    private static final Map<String, ImageIcon> cache = new HashMap<>();

    private AppLogo() { }

    // Logo completo (icona + scritta "BugBoard26")
    public static ImageIcon full(int height) {
        return scaled("full", loadFull(), height);
    }

    // Solo l'icona del bug (senza scritta)
    public static ImageIcon icon(int height) {
        return scaled("icon", loadIcon(), height);
    }

    private static synchronized BufferedImage loadFull() {
        if (!fullLoadAttempted) {
            fullLoadAttempted = true;
            fullLogo = readImage("/frontend/gui/images/logo.png");
        }
        return fullLogo;
    }

    private static synchronized BufferedImage loadIcon() {
        if (!iconLoadAttempted) {
            iconLoadAttempted = true;
            iconOnly = readImage("/frontend/gui/images/logo-icon.png");
        }
        return iconOnly;
    }

    private static BufferedImage readImage(String resource) {
        try (InputStream in = AppLogo.class.getResourceAsStream(resource)) {
            if (in == null) return null;
            return ImageIO.read(in);
        } catch (IOException e) {
            return null;
        }
    }

    private static synchronized ImageIcon scaled(String key, BufferedImage src, int height) {
        if (src == null) return null;
        String cacheKey = key + ":" + height;
        ImageIcon cached = cache.get(cacheKey);
        if (cached != null) return cached;

        int width = Math.max(1, Math.round(src.getWidth() * (height / (float) src.getHeight())));
        Image scaledImg = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon result = new ImageIcon(scaledImg);
        cache.put(cacheKey, result);
        return result;
    }
}
