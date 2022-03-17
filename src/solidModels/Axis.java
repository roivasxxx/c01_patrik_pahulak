package solidModels;

import model.Part;
import model.Solid;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;

public class Axis extends Solid {
    public Axis(char axis){
        Point3D origin=new Point3D(0,0,0);
        Point3D endPoint;
        Col axisCol;
        switch (axis){
            case 'x':
                endPoint=new Point3D(0.5,0,0);
                axisCol=new Col(255,0,0);
                break;
            case 'y':
                endPoint=new Point3D(0,0.5,0);
                axisCol=new Col(0,255,0);
                break;
            case 'z':
                endPoint=new Point3D(0,0,0.5);
                axisCol=new Col(0,0,255);
                break;
            default:endPoint=new Point3D(0,0,0);axisCol=new Col(0,0,0,0);
        }
        getVertices().add(new Vertex(origin));
        getVertices().add(new Vertex(endPoint));
        getIndices().add(0);getIndices().add(1);
        getParts().add(new Part(Part.Topology.AXIS,0,1,axisCol));
        getParts().add(new Part(Part.Topology.POINTS,0,1,new Col(255,255,255)));
    }
}
