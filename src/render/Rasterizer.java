package render;
import transforms.Col;
import model.Vertex;
import raster.Visibility;
import transforms.Vec2D;
import transforms.Vec3D;

import java.awt.*;
import java.util.*;
import java.util.List;

public class
Rasterizer {
    private Visibility visibility;
    private int width;
    private int height;

    public Rasterizer(Visibility visibility) {
        this.visibility = visibility;
        this.height=visibility.getImage().getHeight();
        this.width=visibility.getImage().getWidth();
    }

    public Visibility getVisibility(){
        return visibility;
    }

    public void rasterizeTriangle(Vertex aa, Vertex bb, Vertex cc,Col [][]texture){
        //aa.setTex(new Vec2D(aa.getTex().getX()/aa.getPosition().getZ(),aa.getTex().getY()/aa.getPosition().getZ()));
        //bb.setTex(new Vec2D(bb.getTex().getX()/bb.getPosition().getZ(),bb.getTex().getY()/bb.getPosition().getZ()));
        //cc.setTex(new Vec2D(cc.getTex().getX()/cc.getPosition().getZ(),cc.getTex().getY()/cc.getPosition().getZ()));


        Vec3D a = aa.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));
        Vec3D b = bb.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));
        Vec3D c = cc.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));
        //Outline
        class TextureVec{
            public Vec3D position;
            public Vec2D textureVec;
            public double w;
            public TextureVec(Vec3D p,Vec2D t,double ww){
                position=p;
                textureVec=t;
                w=ww;
            }
            public double getY(){
                return position.getY();
            }
        }
        TextureVec helperA=new TextureVec(a,aa.mul2(1/aa.getPosition().getW()),aa.getOne()/aa.getPosition().getW());
        TextureVec helperB=new TextureVec(b,bb.mul2(1/bb.getPosition().getW()),bb.getOne()/bb.getPosition().getW());
        TextureVec helperC=new TextureVec(c,cc.mul2(1/cc.getPosition().getW()),cc.getOne()/cc.getPosition().getW());

        List<TextureVec> vec3DList = Arrays.asList(helperA,helperB,helperC);
        Collections.sort(vec3DList,Comparator.comparingDouble(TextureVec::getY));

        a = vec3DList.get(0).position;
        b = vec3DList.get(1).position;
        c = vec3DList.get(2).position;
        double wa=vec3DList.get(0).w;
        double wb=vec3DList.get(1).w;
        double wc=vec3DList.get(2).w;
        Vec2D tcA=vec3DList.get(0).textureVec;
        Vec2D tcB=vec3DList.get(1).textureVec;
        Vec2D tcC=vec3DList.get(2).textureVec;


        int texw=texture[0].length-1;
        int texh=texture.length-1;


        //flatbottom
        for (int y = Math.max((int) a.getY()+1, 0); y < Math.min(b.getY(), height - 1);
             y++
        ) {

            double s1 = (y - a.getY()) / (b.getY() - a.getY());
            double s2 = (y - a.getY()) / (c.getY() - a.getY());
            Vec3D ab = a.mul(1 - s1).add(b.mul(s1));
            Vec3D ac = a.mul(1 - s2).add(c.mul(s2));

            Vec2D abt=tcA.mul(1-s1).add(tcB.mul(s1));
            Vec2D act=tcA.mul(1-s2).add(tcC.mul(s2));
            double interpolatedW=wb*(1-s1)+wc*s1;

            if (ab.getX() > ac.getX()) {
                Vec3D tmp = ab;
                ab = ac;
                ac = tmp;
                Vec2D temp=abt;
                abt=act;
                act=temp;
            }

            double tcx=Math.abs((act.getX()-abt.getX())/(ac.getX()-ab.getX()));
            double tcy=Math.abs((act.getY()-abt.getY())/(ac.getX()-ab.getX()));

            Vec2D tc=abt.add(new Vec2D(tcx,tcy));

            for (int x = Math.max((int) ab.getX()+1, 0); x < Math.min(ac.getX(), width-1); x++,
            tc=tc.add(new Vec2D(tcx,tcy))
            ) {
                double t = (x - ab.getX()) / (ac.getX() - ab.getX());
                double z = ab.mul(1 - t).add(ac.mul(t)).getZ();

                visibility.setVisiblePixelWithZtest(x, y, z, texture
                        [(int)Math.min(Math.abs((1/interpolatedW)*tc.getX()*texw),texw)]
                        [(int)Math.min(Math.abs((1/interpolatedW)*tc.getY()*texh),texh)]
                );

            }

        }


        //flat top
        for (int y = Math.max((int) b.getY()+1, 0); y < Math.min(c.getY(), height - 1); y++){

            double s1 = (y - b.getY()) / (c.getY() - b.getY());
            double s2 = (y - a.getY()) / (c.getY() - a.getY());
            Vec3D bc = b.mul(1 - s1).add(c.mul(s1));
            Vec3D ac = a.mul(1 - s2).add(c.mul(s2));

            Vec2D bct=tcB.mul(1-s1).add(tcC.mul(s1));
            Vec2D act=tcA.mul(1-s2).add(tcC.mul(s2));

             double interpolatedW=wa*(1-s1)+wc*s1;

            if (bc.getX() > ac.getX()) {
                Vec3D tmp = bc;
                bc = ac;
                ac = tmp;
                Vec2D temp=bct;
                bct=act;
                act=temp;
            }

             double tcx=Math.abs((act.getX()-bct.getX())/(ac.getX()-bc.getX()));
             double tcy=Math.abs((act.getY()-bct.getY())/(ac.getX()-bc.getX()));

             Vec2D tc=bct.add(new Vec2D(tcx,-tcy));



            for (int x = Math.max((int) bc.getX()+1, 0); x < Math.min(ac.getX(), width-1); x++,
            tc=tc.add(new Vec2D(tcx,-tcy))
            ) {

                double t = (x - bc.getX()) / (ac.getX() - bc.getX());
                double z = bc.mul(1 - t).add(ac.mul(t)).getZ();

                int u=(int)Math.min(Math.abs((1/interpolatedW)*tc.getX()*texw),texw);
                int vv=(int)((1/interpolatedW)*tc.getY()*texh);
                int v=(int)Math.min(Math.abs((1/interpolatedW)*tc.getY()*texh),texh);
                visibility.setVisiblePixelWithZtest(x, y, z, texture
                        [u]
                        [v]
                );

            }

        }
    }
    public void rasterizeTriangle(Vertex aa, Vertex bb, Vertex cc,Col col) {

        Vec3D a = aa.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));
        Vec3D b = bb.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));
        Vec3D c = cc.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));


        List<Vec3D> vec3DList = Arrays.asList(a,b,c);
        Collections.sort(vec3DList,Comparator.comparingDouble(Vec3D::getY));

        a = vec3DList.get(0);
        b = vec3DList.get(1);
        c = vec3DList.get(2);

        for (int y = Math.max((int) a.getY()+1, 0); y < Math.min(b.getY(), height - 1); y++) {
            double s1 = (y - a.getY()) / (b.getY() - a.getY());
            double s2 = (y - a.getY()) / (c.getY() - a.getY());
            Vec3D ab = a.mul(1 - s1).add(b.mul(s1));
            Vec3D ac = a.mul(1 - s2).add(c.mul(s2));
            if (ab.getX() > ac.getX()) {
                Vec3D tmp = ab;
                ab = ac;
                ac = tmp;
            }
            for (int x = Math.max((int) ab.getX()+1, 0); x < Math.min(ac.getX(), width-1); x++) {
                double t = (x - ab.getX()) / (ac.getX() - ab.getX());
                double z = ab.mul(1 - t).add(ac.mul(t)).getZ();

                visibility.setVisiblePixelWithZtest(x, y, z, col);
            }
        }


        for (int y = Math.max((int) b.getY()+1, 0); y < Math.min(c.getY(), height - 1); y++) {
            double s1 = (y - b.getY()) / (c.getY() - b.getY());
            double s2 = (y - a.getY()) / (c.getY() - a.getY());
            Vec3D bc = b.mul(1 - s1).add(c.mul(s1));
            Vec3D ac = a.mul(1 - s2).add(c.mul(s2));

            if (bc.getX() > ac.getX()) {
                Vec3D tmp = bc;
                bc = ac;
                ac = tmp;
            }
            for (int x = Math.max((int) bc.getX()+1, 0); x < Math.min(ac.getX(), width-1); x++) {
                double t = (x - bc.getX()) / (ac.getX() - bc.getX());
                double z = bc.mul(1 - t).add(ac.mul(t)).getZ();

                visibility.setVisiblePixelWithZtest(x, y, z, col);
            }
        }

    }
    public void rasterizeTriangleOutline(Vertex aa, Vertex bb, Vertex cc,Col col) {
        col=new Col(255,255,255);
        Vec3D a = aa.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));
        Vec3D b = bb.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));
        Vec3D c = cc.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));


        List<Vec3D> vec3DList = Arrays.asList(a,b,c);
        Collections.sort(vec3DList,Comparator.comparingDouble(Vec3D::getY));

        a = vec3DList.get(0);
        b = vec3DList.get(1);
        c = vec3DList.get(2);

        for (int y = Math.max((int) a.getY()+1, 0); y < Math.min(b.getY(), height - 1); y++) {
            double s1 = (y - a.getY()) / (b.getY() - a.getY());
            double s2 = (y - a.getY()) / (c.getY() - a.getY());
            Vec3D ab = a.mul(1 - s1).add(b.mul(s1));
            Vec3D ac = a.mul(1 - s2).add(c.mul(s2));
            if (ab.getX() > ac.getX()) {
                Vec3D tmp = ab;
                ab = ac;
                ac = tmp;
            }
            int xMin = Math.max((int) ab.getX()+1, 0);
            int xMax= Math.min((int)ac.getX(), width-1);
            double t = (xMin - ab.getX()) / (ac.getX() - ab.getX());
            double z = ab.mul(1 - t).add(ac.mul(t)).getZ();
            visibility.setVisiblePixelWithZtest(xMin, y, z, col);
            t = (xMax - ab.getX()) / (ac.getX() - ab.getX());
            z = ab.mul(1 - t).add(ac.mul(t)).getZ();
            visibility.setVisiblePixelWithZtest(xMax, y, z, col);
        }


        for (int y = Math.max((int) b.getY()+1, 0); y < Math.min(c.getY(), height - 1); y++) {
            double s1 = (y - b.getY()) / (c.getY() - b.getY());
            double s2 = (y - a.getY()) / (c.getY() - a.getY());
            Vec3D bc = b.mul(1 - s1).add(c.mul(s1));
            Vec3D ac = a.mul(1 - s2).add(c.mul(s2));

            if (bc.getX() > ac.getX()) {
                Vec3D tmp = bc;
                bc = ac;
                ac = tmp;
            }

            int xMin =Math.min((int)ac.getX(), width-1);
            int xMax=Math.max((int) bc.getX()+1, 0);
            double t = (xMin - bc.getX()) / (ac.getX() - bc.getX());
            double z = bc.mul(1 - t).add(ac.mul(t)).getZ();
            visibility.setVisiblePixelWithZtest(xMin, y, z, col);
            t = (xMax - bc.getX()) / (ac.getX() - bc.getX());
            z = bc.mul(1 - t).add(ac.mul(t)).getZ();
            visibility.setVisiblePixelWithZtest(xMax, y, z, col);
        }

    }

    public void rasterizePoint(Vertex a,Col color,String... text){
        Vec3D vecA = a.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));
        Graphics g=visibility.getImage().getGraphics();
        drawLine(vecA.getX(), vecA.getY(), vecA.getX(), vecA.getY(),color,text);
    }

    public void rasterizeLine(Vertex a,Vertex b,Col color){
        Vec3D vecA = a.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));
        Vec3D vecB = b.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));
        Graphics g = visibility.getImage().getGraphics();
        g.setColor(new Color(color.getRGB()));
        g.drawLine((int) vecA.getX(),(int)vecA.getY(),(int)vecB.getX(),(int)vecB.getY());
    }


    public void rasterizeLine(Vertex a,Vertex b,Col color,char axis){
        Vec3D vecA = a.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));
        Vec3D vecB = b.dehomog().get().mul(new Vec3D(1, -1, 1)).add(new Vec3D(1, 1, 0)).mul(new Vec3D((width - 1) / 2, (height - 1) / 2, 1));
        drawLine(vecA.getX(), vecA.getY(), vecB.getX(), vecB.getY(),color,axis);
    }

    private void drawLine(double x1, double y1, double x2, double y2,Col color,char axis) {
        Graphics g = visibility.getImage().getGraphics();
        g.setColor(new Color(color.getRGB()));
        g.drawLine((int) x1,(int) y1,(int) x2,(int) y2);
        g.drawString(Character.toString(axis),(int)x2+5,(int)y2-5);
    }

    private void drawLine(double x1, double y1, double x2, double y2,Col color,String... text) {
        Graphics g = visibility.getImage().getGraphics();
        g.setColor(new Color(color.getRGB()));
        g.drawLine((int) x1,(int) y1,(int) x2,(int) y2);
        g.drawString(text[0],(int)x2+5,(int)y2+10);
    }

}
