package model;

public interface Vectorizable<T> {
    T mul(double t);
    T add(Vertex v);
}
