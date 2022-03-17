package model;


import transforms.*;

import java.util.Optional;

public class Vertex implements Vectorizable<Vertex>{
    private Point3D position;
    private Col color;
    private Vec2D tc;
    private double one=1;
    private static final Col DEFAULT_COLOR=new Col(255,255,0);

    public Vertex(Point3D point3D, Col color) {
        this.position = point3D;
        this.color = new Col(color);
    }

    public double getOne(){return this.one;}
    public void setOne(Double one){this.one=one;}

    public Vertex(Vec3D vec3D, Col color) {
        this.position = new Point3D(vec3D.getX(), vec3D.getY(), vec3D.getZ());
        this.color = color;
    }
    public Vertex(Point3D position,Vec2D tc){
        this.position=position;
        this.tc=tc;
    }
    public Vertex(Point3D position) {
        this.position = position;
        color=DEFAULT_COLOR;
    }

    public Col getColor() {
        return color;
    }

    public void setColor(Col color) {
        this.color = color;
    }

    public Point3D getPosition() {
        return position;
    }
    public Vec2D getTex(){
        return tc;
    }
    public void setTex(Vec2D newTc){this.tc=newTc;}

    public Vertex mul(double t){
        return new Vertex(this.position.mul(t));
    }
    public Vec2D mul2(double t){return tc.mul(t);}
    public Vertex add(Vertex v){
        return new Vertex(this.position.add(v.getPosition()));
    }
    public Vec2D add2(Vec2D t){return tc.add(t);}

    public Vertex mul(Mat4 trans){
        /*Vertex res = new Vertex();
        res.position = position.mul(trans);
        return res;*/
        return new Vertex(position.mul(trans));
        /*position = position.mul(trans);
        return this;*/
    }

    public Optional<Vec3D> dehomog() {
        return position.dehomog();
    }
    
    public Vertex transform(Mat4 model) {
        return new Vertex(position.mul(model));
    }

    public boolean isInView() {
        return (-position.getW() <= position.getX() &&
                position.getY() <= position.getW() &&
                0 <= position.getZ() && 0<= position.getW());
    }
}

