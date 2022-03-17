package raster;

import java.awt.image.BufferedImage;

import transforms.Col;
import java.awt.*;

public class RasterBufferedImage implements Raster<Col>{
    private int width,height;
    private final BufferedImage img;
    private Col col=new Col(0,0,0);

    public RasterBufferedImage(int w,int h){
        width=w;
        height=h;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void setPixel(int x, int y, Col value) {
        img.setRGB(x,y,value.getRGB());
    }

    @Override
    public Col getPixel(int x, int y) {
        return new Col(this.img.getRGB(x,y));
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }
    
    public BufferedImage getImg() {
        return img;
    }

    public void draw(RasterBufferedImage raster) {
        Graphics graphics = getGraphics();
        graphics.setColor(new Color(col.getRGB()));
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.drawImage(raster.img, 0, 0, null);
    }

    public void repaint(Graphics graphics) {
        graphics.drawImage(img, 0, 0, null);
    }

    public Graphics getGraphics() {
        return img.getGraphics();
    }

    @Override
    public void clear() {
        Graphics g = img.getGraphics();
        g.setColor(new Color(col.getRGB()));
        g.clearRect(0, 0, img.getWidth() , img.getHeight() );
    }

    
    public void setClearValue(Col value) {
        this.col = value;
    }

}
