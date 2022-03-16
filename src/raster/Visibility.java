package raster;

import transforms.Col;

public class Visibility {
    private RasterDepthBuffer rDB;
    private RasterBufferedImage rBI;

    public Visibility(int width, int height) {
        this(new RasterBufferedImage(width, height));
    }

    public Visibility(RasterBufferedImage imageBuffer) {
        rBI = imageBuffer;
        rDB = new RasterDepthBuffer(rBI.getWidth(), rBI.getHeight());
    }

    // public void drawVisiblePixel(int x, int y, double z, int color){

    // }

    // public int getColorOfVisiblePixel(int x, int y){
    //     return 0;
    // }

    public RasterBufferedImage getImage() {
        return rBI;
    }

	public void setVisiblePixelWithZtest(int x,int y,double z,Col color){
		
        if (rDB.getPixel(x, y) != null && z < rDB.getPixel(x, y)) {
        	//ystem.out.println("Z: "+z);
            rDB.setPixel(x,y,z);
            rBI.setPixel(x,y,color);
        }
    }

    public RasterBufferedImage getzBufferVisibility() {
        return rBI;
    }
    
    public void clear(){
        rDB.clear();
    }
}
