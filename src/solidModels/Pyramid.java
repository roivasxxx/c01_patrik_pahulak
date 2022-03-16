package solidModels;

import model.Part;
import model.Solid;
import model.Vertex;
import transforms.Col;

public class Pyramid extends Solid{
	Vertex a,b,c,d,e;
	public Pyramid(Vertex a,Vertex b,Vertex c,Vertex d,Vertex e) {
		getVertices().add(a);getVertices().add(b);getVertices().add(c);getVertices().add(d);getVertices().add(e);
		getIndices().add(0);getIndices().add(1);getIndices().add(2);getIndices().add(3);getIndices().add(4);
		getParts().add(new Part(Part.Topology.TRIANGLES, 0,1,new Col(255,0,0)));
		getParts().add(new Part(Part.Topology.TRIANGLES, 1,1,new Col(0,255,0)));
		getParts().add(new Part(Part.Topology.TRIANGLES, 2,1,new Col(0,0,255)));



	}
}
