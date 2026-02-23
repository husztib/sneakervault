package com.sneakervault.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@RestController
public class OgImageController {

    private static byte[] cachedImage;

    @GetMapping(value = "/og-image.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getOgImage() throws Exception {
        if (cachedImage == null) {
            cachedImage = generateImage();
        }
        return ResponseEntity.ok()
                .header("Cache-Control", "public, max-age=86400")
                .body(cachedImage);
    }

    private byte[] generateImage() throws Exception {
        int w = 1200, h = 630;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background gradient
        GradientPaint bg = new GradientPaint(0, 0, new Color(10, 10, 10), w, h, new Color(26, 26, 26));
        g.setPaint(bg);
        g.fillRect(0, 0, w, h);

        // Accent glow
        g.setPaint(new RadialGradientPaint(
                w * 0.7f, h * 0.5f, 400,
                new float[]{0f, 1f},
                new Color[]{new Color(255, 77, 0, 30), new Color(255, 77, 0, 0)}
        ));
        g.fillRect(0, 0, w, h);

        // Accent bar at top
        g.setColor(new Color(255, 77, 0));
        g.fillRect(0, 0, w, 6);

        // "BX" large background watermark
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 300));
        g.setColor(new Color(255, 77, 0, 15));
        FontMetrics fmBig = g.getFontMetrics();
        g.drawString("BX", w - fmBig.stringWidth("BX") - 20, h - 40);

        // Main title
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 96));
        g.setColor(new Color(255, 77, 0));
        g.drawString("BotiX", 80, 260);

        // Subtitle
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 40));
        g.setColor(new Color(238, 238, 238));
        g.drawString("Premium Sneaker Store", 84, 330);

        // Brand pills
        String[] brands = {"Nike", "Jordan", "Adidas", "Vans"};
        Color[] brandColors = {
                new Color(243, 112, 33),
                new Color(201, 48, 44),
                new Color(100, 100, 100),
                new Color(193, 39, 45)
        };
        int pillX = 84;
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        for (int i = 0; i < brands.length; i++) {
            int textW = fm.stringWidth(brands[i]);
            int pillW = textW + 40;
            g.setColor(brandColors[i]);
            g.fill(new RoundRectangle2D.Float(pillX, 380, pillW, 50, 25, 25));
            g.setColor(Color.WHITE);
            g.drawString(brands[i], pillX + 20, 413);
            pillX += pillW + 16;
        }

        // Footer line
        g.setColor(new Color(255, 77, 0, 60));
        g.fillRect(0, h - 60, w, 1);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
        g.setColor(new Color(136, 146, 164));
        g.drawString("botix.hu  |  info@botix.hu  |  +36 30 414 9661", 80, h - 22);

        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        return out.toByteArray();
    }
}
