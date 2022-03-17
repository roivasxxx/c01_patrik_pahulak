package model;

import transforms.Col;

import java.util.ArrayList;
import java.util.List;

public class Part {
    public enum Topology {POINTS, LINES, TRIANGLES, TRIANGLES_STRIP,AXIS,TRIANGLE_FAN}
    private int start;
    private int count;
    private Topology topology;
    private Col c;
    private List<Col> colors;
    private boolean outline=false;

    public Part(Topology topology,int start, int count,Col color) {
        this.start = start;
        this.count = count;
        this.topology = topology;
        this.c=color;
        this.colors=new ArrayList<>();
    }

    public Part(Topology topology,int start, int count,List<Col> cols) {
        this.start = start;
        this.count = count;
        this.topology = topology;
        this.colors=cols;
    }

    @Override
    public String toString() {
        return "Part{" +
                "start=" + start +
                ", count=" + count +
                ", topology=" + topology +
                '}';
    }

    public int getStart() {
        return start;
    }

    public int getCount() {
        return count;
    }

    public Topology getTopology() {
        return topology;
    }

    public Col getColor()
    {
        return c;
    }

    public void setColors(List<Col>cols){
        colors=cols;
    }
    public List<Col> getColors(){return colors;}
    public int getColorsLength() { return colors.size();}

    public Col getColorAt(int index){return colors.get(index);}

    public boolean getOutline(){return this.outline;}
    public void setOutline(boolean b){outline=b;}
}
