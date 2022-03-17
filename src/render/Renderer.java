package render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import com.sun.xml.internal.bind.v2.TODO;
import model.Part;
import model.Scene;
import model.Solid;
import model.Vertex;
import raster.RasterBufferedImage;
import raster.Visibility;
import transforms.*;

import javax.imageio.ImageIO;

public class Renderer {
    //Mat4 model, view, projection;
    private RasterBufferedImage raster;
    private Visibility visibility;
    private Rasterizer rasterizer;
    private int width, height;
    private Col [][] texture;
    public Renderer(Rasterizer rasterizer) {
        this.rasterizer = rasterizer;
        this.visibility=rasterizer.getVisibility();
        this.width = visibility.getImage().getWidth();
        this.height = visibility.getImage().getHeight();
        BufferedImage img = null;
        try {
            File file = new File("doge.jpg");

            img = ImageIO.read(file);
            texture=new Col[img.getWidth()][img.getHeight()];
            for(int i = 0; i < img.getWidth(); i++)
                for(int j = 0; j < img.getHeight(); j++){
                    texture[i][j] = new Col(img.getRGB(i, j));
                }

        } catch (FileNotFoundException e) {
            System.out.println("EXCEPTION WITH IMG");
        }catch(IOException ioe){

        }

    }

    


    public void render(Solid solid,Mat4 mat){
        for(Part part:solid.getParts()){
            switch (part.getTopology()){
                case POINTS:
                    for(int i=0; i < part.getCount() ; i++) {
                        int indexA = part.getStart() + i ;
                        Vertex a = solid.getVertices().get(solid.getIndices().get(indexA)).transform(mat);
                        rasterizer.rasterizePoint(a,part.getColor(),"(0,0,0)");
                    }

                    break;
                case LINES:
                    for(int i=0; i < part.getCount() ; i++) {
                        int indexA = part.getStart() + i ;
                        int indexB = part.getStart() + i + 1;

                        Vertex a=solid.getVertices().get(solid.getIndices().get(indexA));
                        Vertex b=solid.getVertices().get(solid.getIndices().get(indexB));

                        Vertex aV = a.transform(mat);
                        Vertex bV = b.transform(mat);

                        if(aV.isInView() && bV.isInView()){
                           renderLine(aV, bV,part.getColor());
                        }else{
                            return;
                        }
                    }
                    break;
                case AXIS:
                    for(int i=0; i < part.getCount() ; i++) {
                        int indexA = part.getStart() + i ;
                        int indexB = part.getStart() + i + 1;

                        Vertex a=solid.getVertices().get(solid.getIndices().get(indexA));
                        Vertex b=solid.getVertices().get(solid.getIndices().get(indexB));

                        Vertex aV = a.transform(mat);
                        Vertex bV = b.transform(mat);
                        if(aV.isInView() && bV.isInView()){
                                if (part.getColor().eEquals(new Col(255,0,0))  ) {
                                    rasterizer.rasterizeLine(aV, bV, part.getColor(), 'x');
                                } else if (part.getColor().eEquals(new Col(0, 255, 0))) {
                                    rasterizer.rasterizeLine(aV, bV, part.getColor(), 'y');
                                } else if (part.getColor().eEquals(new Col(0, 0, 255))) {
                                    rasterizer.rasterizeLine(aV, bV, part.getColor(), 'z');
                                }
                        }else return;
                    }
                    break;
                case TRIANGLES:
                    for(int i = 0; i< part.getCount(); i++){
                    	int indexA = part.getStart() + i*3;
                        int indexB = part.getStart() + i*3+1;
                        int indexC = part.getStart() + i*3+2;

                        Vertex a = solid.getVertices().get(solid.getIndices().get(indexA)).transform(mat);
                        a.setTex(solid.getVertices().get(solid.getIndices().get(indexA)).getTex());
                        Vertex b = solid.getVertices().get(solid.getIndices().get(indexB)).transform(mat);
                        b.setTex(solid.getVertices().get(solid.getIndices().get(indexB)).getTex());
                        Vertex c = solid.getVertices().get(solid.getIndices().get(indexC)).transform(mat);
                        c.setTex(solid.getVertices().get(solid.getIndices().get(indexC)).getTex());
                        if (part.getOutline()) {
                            renderTriangle(a, b, c, part.getColorsLength() > 0 ? part.getColorAt(i) : part.getColor(), true);
                        } else {
                            renderTriangle(a, b, c, part.getColorsLength() > 0 ? part.getColorAt(i) : part.getColor(),false);
                        }

                    }
                    break;
                case TRIANGLES_STRIP:
                    for (int i = 0; i < part.getCount()-2; i++) {
                        int indexA = part.getStart() + i;
                        int indexB = part.getStart() + i + 1;
                        int indexC = part.getStart() + i + 2;

                        Vertex a = solid.getVertices().get(solid.getIndices().get(indexA)).transform(mat);
                        Vertex b = solid.getVertices().get(solid.getIndices().get(indexB)).transform(mat);
                        Vertex c = solid.getVertices().get(solid.getIndices().get(indexC)).transform(mat);

                        renderTriangle(a,b,c,part.getColors().get(i%3),false);
                    }break;

            }
        }

    }

    private void renderPoint(Vertex v){

    }

    private void renderLine(Vertex a, Vertex b,Col col){
        rasterizer.rasterizeLine(a,b,col);
    }

    private void renderTriangle(Vertex a, Vertex b, Vertex c,Col col,boolean outline){
    	
        List<Vertex> vcs = Arrays.asList(a, b, c); // triangle vertices 

        vcs.sort(Comparator.comparingDouble(v -> v.getPosition().getZ()));

        Vertex newA=vcs.get(0);
        Vertex newB=vcs.get(1);
        Vertex newC=vcs.get(2);
        
        if (newA.getPosition().getZ()<=0)
            return;
        if (newB.getPosition().getZ()<=0){

            double t = newA.getPosition().getZ() / (newA.getPosition().getZ() - newB.getPosition().getZ());
            Vertex v1 = newA.mul(1 - t).add(newB.mul(t));

            if(a.texturedVertex()) {
               Vec2D v1tex = newA.getTex().mul(1 - t).add(newB.getTex().mul(t));
                v1.setTex(v1tex);
            }

            t = newA.getPosition().getZ() / (newA.getPosition().getZ() - newC.getPosition().getZ());
            Vertex v2 = newA.mul(1 - t).add(newB.mul(t));

            if(a.texturedVertex()) {
                Vec2D v2tex = newA.getTex().mul(1 - t).add(newB.getTex().mul(t));
                v2.setTex(v2tex);
            }

            if (a.texturedVertex()) {
                rasterizer.rasterizeTriangle(newA, v1, v2, texture);
            } else if(outline){rasterizer.rasterizeTriangleOutline(newA,v1,v2,col);}else {
                rasterizer.rasterizeTriangle(newA, v1, v2, col);
            }
        }

        if(newC.getPosition().getZ()<=0){

            double t = newA.getPosition().getZ() / (newA.getPosition().getZ() - newC.getPosition().getZ());
            Vertex v1 = newA.mul(1 - t).add(newC.mul(t));

            if(a.texturedVertex()){
                Vec2D v1tex=newA.getTex().mul(1-t).add(newC.getTex().mul(t));
                v1.setTex(v1tex);
            }

            t = newB.getPosition().getZ() / (newB.getPosition().getZ() - newC.getPosition().getZ());
            Vertex v2 = newB.mul(1 - t).add(newC.mul(t));

            if(a.texturedVertex()) {
                Vec2D v2tex=newB.getTex().mul(1-t).add(newC.getTex().mul(t));
                v2.setTex(v2tex);
            }


            if (a.texturedVertex()) {
                rasterizer.rasterizeTriangle(newA, newB, v1, texture);
                rasterizer.rasterizeTriangle(newA, v1, v2, texture);
            }else if(outline){
                rasterizer.rasterizeTriangleOutline(newA, newB, v1,col);
                rasterizer.rasterizeTriangleOutline(newA, v1, v2,col);
            }
            else {
                rasterizer.rasterizeTriangle(newA, newB, v1, col);
                rasterizer.rasterizeTriangle(newA, v1, v2, col);
            }

        }
        if (a.texturedVertex()) {
            rasterizer.rasterizeTriangle(newA, newB, newC, texture);
        }else if(outline){rasterizer.rasterizeTriangleOutline(newA, newB, newC,col);}
        else {
            rasterizer.rasterizeTriangle(newA, newB, newC, col);
        }

    }
}
