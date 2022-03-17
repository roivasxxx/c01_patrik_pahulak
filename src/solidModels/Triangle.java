package solidModels;

import model.Part;
import model.Solid;
import model.Vertex;
import transforms.Col;

public class Triangle extends Solid {
    Vertex a,b,c;

    public Triangle(Vertex a, Vertex b, Vertex c,Col col) {
        setSolidId("0");
        this.a = a;
        this.b = b;
        this.c = c;

        getVertices().add(a);getVertices().add(b);getVertices().add(c);

        getIndices().add(0);getIndices().add(1);getIndices().add(2);

        getParts().add(new Part(Part.Topology.TRIANGLES, 0,1,col));
    }

    public Vertex getA() {
        return a;
    }

    public Vertex getB() {
        return b;
    }

    public Vertex getC() {
        return c;
    }


}
