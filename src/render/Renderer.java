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
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Mat4Transl;
import transforms.Col;

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
                            //test jestli se jedná o osu x/y/z
                            if(a.getPosition().getX()==0&& a.getPosition().getY()==0&&a.getPosition().getZ()==0){

                                if(b.getPosition().getX()==0.5&&b.getPosition().getY()==0&&b.getPosition().getZ()==0){
                                    rasterizer.rasterizeLine(aV,bV,part.getColor(),'x');
                                }else if(b.getPosition().getX()==0&&b.getPosition().getY()==0.5&&b.getPosition().getZ()==0){
                                    rasterizer.rasterizeLine(aV,bV,part.getColor(),'y');
                                }else if(b.getPosition().getX()==0&&b.getPosition().getY()==0&&b.getPosition().getZ()==0.5){
                                    rasterizer.rasterizeLine(aV,bV,part.getColor(),'z');
                                }
                            }
                            else renderLine(aV, bV);

                        }else{
                            return;
                        }
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
                        renderTriangle(a,b,c,part.getColorsLength()>0?part.getColorAt(i):part.getColor());
                        
                    }
                    break;

            }
        }

    }

    private void renderPoint(Vertex v){

    }

    private void renderLine(Vertex a, Vertex b){
        if(a.getZ()<b.getZ()){
            Vertex temp=a;
            a=b;
            b=a;
        }
        if(a.getZ()<=0)return;
        if(b.getZ()<=0){
            double t = a.getPosition().getZ() / (a.getPosition().getZ() - b.getPosition().getZ());
            Vertex v1 = a.mul(1 - t).add(b.mul(t));
            rasterizer.rasterizeLine(a,v1);
        }
        rasterizer.rasterizeLine(a,b);
    }

    private void renderTriangle(Vertex a, Vertex b, Vertex c,Col col){
    	
        List<Vertex> vcs = Arrays.asList(a, b, c); // triangle vertices 
        //transformace done in prev step
        
        //fast clip
        //TODO

        //orezani z>=0 tedy w>=zn, tedy w>0
        //TODO sort a.z >= b.z >= c.z
        vcs.sort(Comparator.comparingDouble(v -> v.getPosition().getZ()));

        Vertex newA=vcs.get(0);
        Vertex newB=vcs.get(1);
        Vertex newC=vcs.get(2);
        
        if (newA.getPosition().getZ()<=0)
            return;
        if (newB.getPosition().getZ()<=0){
        	/*
            double s1 = (0 - vcs.get(0).getPosition().getZ())/(vcs.get(0).getPosition().getZ() - vcs.get(1).getPosition().getZ());
            Vertex ab = vcs.get(1).mul(1-s1).add(vcs.get(0).mul(s1));

            double s2=(0 - vcs.get(0).getPosition().getZ())/(vcs.get(0).getPosition().getZ() - vcs.get(2).getPosition().getZ());
            Vertex ac = vcs.get(2).mul(1-s2).add(vcs.get(0).mul(s2)); //TODO
			*/
            
            // rasterizer.rasterizePoint(a);
            // rasterizer.rasterizePoint(ab);
            // rasterizer.rasterizePoint(ac);
            double t = newA.getPosition().getZ() / (newA.getPosition().getZ() - newB.getPosition().getZ());
            Vertex v1 = newA.mul(1 - t).add(newB.mul(t));

            t = newA.getPosition().getZ() / (newA.getPosition().getZ() - newC.getPosition().getZ());
            Vertex v2 = newA.mul(1 - t).add(newB.mul(t));
            if (a.getTex() != null) {
                rasterizer.rasterizeTriangle(newA, v1, v2, texture);
            } else {
                rasterizer.rasterizeTriangle(newA, v1, v2, col);
            }
        }
        //dva trojuhelniky
        if(vcs.get(2).getPosition().getZ()<=0){
            /*double s1=(0-vcs.get(0).getPosition().getZ())/(vcs.get(0).getPosition().getZ()-vcs.get(2).getPosition().getZ());
            Vertex ac=vcs.get(2).mul(1-s1).add(vcs.get(0).mul(s1));

            double s2=(0-vcs.get(1).getPosition().getZ())/(vcs.get(1).getPosition().getZ()-vcs.get(2).getPosition().getZ());
            Vertex bc=vcs.get(2).mul(1-s2).add(vcs.get(1).mul(s2));
*/
            
            
            double t = newA.getPosition().getZ() / (newA.getPosition().getZ() - newC.getPosition().getZ());
            Vertex v1 = newA.mul(1 - t).add(newC.mul(t));

            t = newB.getPosition().getZ() / (newB.getPosition().getZ() - newC.getPosition().getZ());
            Vertex v2 = newB.mul(1 - t).add(newC.mul(t));
            if (a.getTex() != null) {
                rasterizer.rasterizeTriangle(newA, newB, v1, texture);
            } else {
                rasterizer.rasterizeTriangle(newA, newB, v1, col);
            }
            if (a.getTex() != null) {
                rasterizer.rasterizeTriangle(newA, v1, v2, texture);
            } else {
                rasterizer.rasterizeTriangle(newA, v1, v2, col);
            }

        }
        if (a.getTex() != null) {
            rasterizer.rasterizeTriangle(newA, newB, newC, texture);
        } else {
            rasterizer.rasterizeTriangle(newA, newB, newC, col);
        }
        //TODO
    }
}
