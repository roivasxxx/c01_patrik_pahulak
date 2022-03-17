package solidModels;

import model.Part;
import model.Solid;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;

public class Line extends Solid {
    public Line(Vertex a,Vertex b){
        getVertices().add(new Vertex(a));
        getVertices().add(new Vertex(b));
        getIndices().add(0);getIndices().add(1);
        getParts().add(new Part(Part.Topology.LINES,0,1,new Col(255,255,255)));
    }
}
