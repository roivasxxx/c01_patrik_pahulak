package solidModels;

import model.Part;
import model.Solid;
import model.Vertex;
import transforms.Bicubic;
import transforms.Col;
import transforms.Cubic;
import transforms.Point3D;



public class BicubicSurface extends Solid {

    public BicubicSurface() {
        super();
        Bicubic bicubic = new Bicubic(Cubic.FERGUSON,
                new Point3D(1.25,-2,0),new Point3D(0.5,-2,0.5),new Point3D(0.5,-2,0.5),new Point3D(1.25,-2,0),
                new Point3D(1,-2,0.5),new Point3D(0.5,-2,1),new Point3D(2,-2,1),new Point3D(1,-2,0.5),
                new Point3D(1,-2,0.5),new Point3D(0.5,-2,1),new Point3D(2,-2,1),new Point3D(1.5,-2,0.5),
                new Point3D(1.25,-2,0),new Point3D(0.5,-2,0.5),new Point3D(2,-2,0.5),new Point3D(-2,1.25,0)
        );
        for(int i = 0; i < 4;i++) {
            for(double j = 0; j < 1; j += 0.1){
                getVertices().add(new Vertex(bicubic.compute(j,j)));
            }
        }

        for (int i = 0; i < getVertices().size() - 1; i++) {
            getIndices().add(i);
            getIndices().add(i + 1);
        }

        getParts().add(new Part(Part.Topology.LINES, 0, getVertices().size()-1,new Col(255,255,255)));
    }
}