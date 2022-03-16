package solidModels;

import model.Part;
import model.Solid;
import model.Vertex;
import transforms.Col;
import solidModels.Triangle;
import transforms.Point3D;

import java.util.Arrays;
import java.util.List;

public class Cube extends Solid{

    public Cube(Vertex a,double size){
        Point3D aPosition=a.getPosition();
        getVertices().add(a); //0
        getVertices().add(new Vertex(
                new Point3D(aPosition.getX()+size, aPosition.getY(), aPosition.getZ()))
        ); //1    +x
        getVertices().add(new Vertex(
                new Point3D(aPosition.getX(), aPosition.getY()+size, aPosition.getZ()))
        ); //2    +y
        getVertices().add(new Vertex(
                new Point3D(aPosition.getX()+size, aPosition.getY()+size, aPosition.getZ()))
        ); //3    +x, +y

        getVertices().add(new Vertex(
                new Point3D(aPosition.getX(), aPosition.getY(), aPosition.getZ()-size))
        );//4
        getVertices().add(new Vertex(
                new Point3D(aPosition.getX()+size, aPosition.getY(), aPosition.getZ()-size))
        ); //5    +x
        getVertices().add(new Vertex(
                new Point3D(aPosition.getX(), aPosition.getY()+size, aPosition.getZ()-size))
        ); //6    +y
        getVertices().add(new Vertex(
                new Point3D(aPosition.getX()+size, aPosition.getY()+size, aPosition.getZ()-size))
        ); //7    +x, +y

        //top
        getIndices().add(0); getIndices().add(1);getIndices().add(3);
        getIndices().add(0); getIndices().add(2);getIndices().add(3);

        getIndices().add(0); getIndices().add(1);getIndices().add(4);
        getIndices().add(1); getIndices().add(5);getIndices().add(4);

        getIndices().add(2); getIndices().add(3);getIndices().add(6);
        getIndices().add(3); getIndices().add(6);getIndices().add(7);

        getIndices().add(0); getIndices().add(6);getIndices().add(4);
        getIndices().add(0); getIndices().add(2);getIndices().add(6);

        getIndices().add(1); getIndices().add(5);getIndices().add(7);
        getIndices().add(1); getIndices().add(3);getIndices().add(7);
        //bottom
        getIndices().add(4); getIndices().add(5);getIndices().add(6);
        getIndices().add(5); getIndices().add(6);getIndices().add(7);

        List<Col> cols=Arrays.asList(
                new Col(229,156,156),new Col(233,201,229),new Col(173,182,220),new Col(171,242,211),
                new Col(204,242,171),new Col(232,242,171),new Col(242,220,171),new Col(255,202,148),new Col(255,148,148),
                new Col(198,148,255),new Col(148,152,255),new Col(148,255,180),new Col(148,255,249)
        );
        getParts().add(new Part(Part.Topology.TRIANGLES, 0, 12, cols));
    }
}
