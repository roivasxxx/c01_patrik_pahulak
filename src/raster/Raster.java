package raster;

public interface Raster <T> {
    void setPixel(int x, int y, T value);
    T getPixel(int x, int y);

    int getWidth();
    int getHeight();
    void clear();
}
