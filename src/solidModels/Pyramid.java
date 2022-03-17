package solidModels;

import model.Part;
import model.Solid;
import model.Vertex;
import transforms.Col;

import java.util.Arrays;

public class Pyramid extends Solid {
    Col colA=new Col(128,0,128);
    Col colB=new Col(0, 128, 128);
    Col colC=new Col(128, 128, 0);

    public Pyramid(Vertex a, Vertex b, Vertex c, Vertex d, Vertex e){
        super();
        getVertices().add(a);
        getVertices().add(b);
        getVertices().add(c);
        getVertices().add(d);
        getVertices().add(e);
        getIndices().add(0);getIndices().add(1);getIndices().add(2);getIndices().add(3);getIndices().add(4);
        getParts().add(new Part(Part.Topology.TRIANGLE_FAN,0,5,Arrays.asList(colA,colB,colC)));
        getParts().add(new Part(Part.Topology.TRIANGLE_FAN,1,4,Arrays.asList(new Col(0,128,64),new Col(0,128,64))));
    }

}
