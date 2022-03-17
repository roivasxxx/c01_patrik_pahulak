package model;

import transforms.Col;
import transforms.Mat4;
import transforms.Mat4Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Solid {
    private List<Part> parts;
    private List<Vertex> vertices;
    private List<Integer> indices;
    private String solidId;
    private Mat4 model = new Mat4Identity();
// private Col lineColor = new Col(1., 1, 1);
    private Col color;
    
    public Mat4 getModel() {
        return model;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }

    

    public Col getColor() {
        return color;
    }

    public void setColor(Col color) {

        this.color = color;
    }

    //public String getSolidId(){return solidId;}
    public String getSolidId(){return solidId;}
    public void setSolidId(String id){
        this.solidId=id;
    }

    public Solid() {
        parts = new ArrayList<>();
        vertices = new ArrayList<>();
        indices = new ArrayList<>();
        solidId="0";

        if(color == null) {
            Random rColor = new Random();
            setColor(new Col(rColor.nextInt(256), rColor.nextInt(256), rColor.nextInt(256)));
        }
    }

    public List<Part> getParts() {
        return parts;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Integer> getIndices() {
        return indices;
    }
}
