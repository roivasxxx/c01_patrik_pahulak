package solidModels;

import model.Part;
import model.Solid;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;

import java.util.Arrays;

public class TriangleStrip extends Solid {
    Col a=new Col(128,0,128);
    Col b=new Col(0, 128, 128);
    Col c=new Col(128, 128, 0);

    public TriangleStrip(double x,double y,double z){
        super();
        getVertices().add(new Vertex(new Point3D(0+x,0+y,0.1+z))); //0
        getVertices().add(new Vertex(new Point3D(-0.1+x,0.1+y,0.1+z))); //1
        getVertices().add(new Vertex(new Point3D(0.1+x,0.1+y,0.1+z))); //2
        getVertices().add(new Vertex(new Point3D(-0.1+x,-0.1+y,0.1+z))); //3
        getVertices().add(new Vertex(new Point3D(0.1+x,-0.1+y,0.1+z))); //4
        getVertices().add(new Vertex(new Point3D(0.2+x,0+y,0.1+z))); //5
        getVertices().add(new Vertex(new Point3D(0.3+x,0.1+y,0.1+z))); //6
        getVertices().add(new Vertex(new Point3D(0.3+x,-0.1+y,0.1+z))); //7
        getVertices().add(new Vertex(new Point3D(0.4+x,0+y,0.1+z))); //8
        getIndices().add(1);getIndices().add(0);getIndices().add(2);getIndices().add(5);getIndices().add(6);getIndices().add(8);
        getParts().add(new Part(Part.Topology.TRIANGLES_STRIP, 0, 6, Arrays.asList(a,b,c)));
    }

}
