package raster;

import java.util.Arrays;

public class RasterDepthBuffer implements Raster<Double>{
//    private double[][] array;
//    private HashMap<Integer, Double>; //index pole, hodnota
//    private HashMap<Pair<Integer,Integer>, Double>;
//    private List<Double> list;
    private double[][] zBuffer;
    private int width, height;
    

    public RasterDepthBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.zBuffer = new double[width][height];
        clear();
    }

    @Override
    public void setPixel(int x, int y, Double value) {
//        array[x][y] = value.doubleValue();
        if (checkOutOfBounds(x,y))zBuffer[x][y]=value;
        
    }

    @Override
    public Double getPixel(int x, int y) {
        if(!checkOutOfBounds(x,y)) {
        	
        	return null;
        	}
        	return zBuffer[x][y];
    }
    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void clear() {
        for(double[] nums : zBuffer){
            Arrays.fill(nums, 1.0);
        }
    }
    
    private boolean checkOutOfBounds(int x, int y) {
        return (x < getWidth() && y < getHeight() && x > -1 && y > -1);
    }


}
