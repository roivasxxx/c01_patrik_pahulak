package view;

import raster.RasterBufferedImage;
import transforms.Col;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    private RasterBufferedImage raster;

    public RasterBufferedImage getRaster() {
        return raster;
    }

    public static final int WIDTH = 800, HEIGHT = 600;

    Panel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        raster = new RasterBufferedImage(WIDTH, HEIGHT);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        raster.repaint(g);
    }


    public void resize() {
        if (this.getWidth() < 1 || this.getHeight() < 1)
            return;
        RasterBufferedImage newRaster = new RasterBufferedImage(this.getWidth(), this.getHeight());
        newRaster.draw(raster);
        raster = newRaster;
    }

    public void clear() {
        raster.clear();
    }
}
