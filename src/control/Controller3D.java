package control;

import solidModels.*;
import model.Vertex;
import model.Scene;
import raster.RasterBufferedImage;
import raster.Visibility;
import render.*;
import render.Renderer;
import transforms.*;
import view.Panel;
import solidModels.Cube;
import solidModels.BicubicSurface;
import solidModels.Pyramid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class Controller3D implements Controller, ActionListener {

    private final Panel panel;

    Axis xAxis = new Axis('x');
    Axis yAxis = new Axis('y');
    Axis zAxis = new Axis('z');
    TriangleStrip triangleStrip = new TriangleStrip(0.2,-0.2,-0.5);
    Triangle triangle = new Triangle(

            new Vertex( new Point3D(0.1, -0.6 ,0),new Vec2D(1,1)),
            new Vertex(new Point3D(0.5, -0.6 ,0.4), new Vec2D(0,0)),
            new Vertex( new Point3D(0.5, -0.6 ,0),new Vec2D(0,1))

            , new Col(0,0,1.)
            );
    Triangle triangle2 = new Triangle(
            new Vertex( new Point3D(0.1, -0.6 ,0),new Vec2D(1,1)),
            new Vertex( new Point3D(0.1, -0.6 ,0.4),new Vec2D(1,0)),
            new Vertex( new Point3D(0.5, -0.6 ,0.4), new Vec2D(0,0)),

            new Col(0,0,1.)
    );
    Triangle triangleOutlined = new Triangle(
            new Vertex( new Point3D(0.1, 0.1 ,0)),
            new Vertex( new Point3D(0.2, 0.2 ,0.2)),
            new Vertex( new Point3D(0.1, 0.1 ,0.4)),

            new Col(0,0,1.),true
    );
    

    Vertex a=new Vertex(new Point3D(2,-2,0));
	Vertex b=new Vertex(new Point3D(2.5,-2,0));
	Vertex c=new Vertex(new Point3D(2,-2.5,0));
	Vertex d=new Vertex(new Point3D(2.5,-2.5,0));
	Vertex e=new Vertex(new Point3D(2.25,-2.25,1));

    Pyramid ppp=new Pyramid(e,a,b,d,c);

    double cubeSize=0.2;
    Cube cube=new Cube(new Vertex(new Point3D(0.8,-0.8,0)),cubeSize);
    BicubicSurface bc=new BicubicSurface();

    private Visibility visibility;
    private Rasterizer rasterizer;
    private Renderer renderer;
    private double yTransform = 0, xTransform = 0, zTransform = 0,
            yInc = 0, xInc = 0, zInc = 0, zoom = 1;
    double cubeRotationX =0;
    double cubeRotationZ =0;
    private boolean perspective = true;
    private JComboBox comboBox = new JComboBox();

    private Scene scene;
    private Camera cameraView = new Camera()
            .withPosition(new Vec3D(0.2,1,0.5)).withAzimuth(-1.5).withZenith(0);


    private int width, height;
    private boolean pressed = false;
    private int ox, oy;

    private double velocity = 0.05;

    boolean modeCleared = true;

    public Controller3D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
        redraw();
    }

    public void initObjects(RasterBufferedImage raster) {
        raster.setClearValue(new Col(0x55768A));
        visibility = new Visibility(raster);
        rasterizer = new Rasterizer(visibility);
        renderer = new Renderer(rasterizer);
        scene = new Scene(renderer);

        initMat();
        initGeometry();

    }

    private void initMat() {
        scene.setView(cameraView.getViewMatrix());
        scene.setProjection(new Mat4PerspRH((float) Math.PI / 2, 1, 0.1, 100));
    }

    private void initGeometry() {
        cube.setSolidId("1");
        scene.getSolids().addAll(Arrays.asList(
                xAxis,yAxis,zAxis,triangle
                ,triangle2,triangleOutlined,cube,
                bc,triangleStrip,ppp
        		));
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent ev) {
                if (ev.getButton() == MouseEvent.BUTTON1) {
                    pressed = true;
                    ox = ev.getX();
                    oy = ev.getY();
                    redraw();
                }
            }

            public void mouseReleased(MouseEvent ev) {
                if (ev.getButton() == MouseEvent.BUTTON1) {
                    pressed = false;
                    redraw();
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent ev) {
                if (pressed) {
                    cameraView = cameraView.addAzimuth((float) Math.PI * (ev.getX() - ox) / (float) panel.getWidth());
                    cameraView = cameraView.addZenith((float) Math.PI * (ev.getY() - oy) / (float) panel.getWidth());
                    ox = ev.getX();
                    oy = ev.getY();
                    redraw();
                }
            }
        });

        panel.addKeyListener(new KeyAdapter() {
           public void keyPressed(KeyEvent key) {
              switch (key.getKeyCode()) {
              case KeyEvent.VK_W: {
                                   cameraView = cameraView.forward(0.1);
                                   redraw();
                               }break;
                              case KeyEvent.VK_S: {
                                   cameraView = cameraView.backward(0.1);
                                  redraw();
                              }break;
                              case KeyEvent.VK_A:{
                                  cameraView = cameraView.left(0.1);
                                  redraw();
                              }break;
                              case KeyEvent.VK_D: {
                                  cameraView = cameraView.right(0.1);
                                  redraw();
                              }break;
                                case KeyEvent.VK_P : {
                                     if (perspective) {
                                          scene.setProjection(new Mat4OrthoRH(
                                                  2, 2, 0.1, 100));
                                      } else {
                                          scene.setProjection(new Mat4PerspRH(
                                                   (float) Math.PI / 2, 1, 0.1, 100));
                                     }
                                      perspective = !perspective;
                                      redraw();
                                  }break;
                  case KeyEvent.VK_E : zInc += 0.1;break;
                  case KeyEvent.VK_Q : zInc -= 0.1;break;
                  case KeyEvent.VK_R : yInc += 0.1;break;
                  case KeyEvent.VK_F : yInc -= 0.1;break;
                  case KeyEvent.VK_X : xInc -= 0.1;break;
                  case KeyEvent.VK_C : xInc += 0.1;break;
                  case KeyEvent.VK_UP:{
                      cube.setModel(cube.getModel().mul(new Mat4Transl(-0.9,+0.7,0.1).mul( new Mat4RotX(Math.PI/8)).mul(new Mat4Transl(+0.9,-0.7,-0.1))));
                      redraw();
                      cubeRotationX +=0.005;
                      break;
                  }
                  case KeyEvent.VK_DOWN:{
                      cube.setModel(cube.getModel().mul(new Mat4Transl(-0.9,+0.7,0.1).mul( new Mat4RotX(-Math.PI/8)).mul(new Mat4Transl(+0.9,-0.7,-0.1))));
                      redraw();
                      cubeRotationX -=0.005;
                      break;
                  }
                  case KeyEvent.VK_RIGHT:{
                      cube.setModel(cube.getModel().mul(new Mat4Transl(-0.9,+0.7,0.1).mul( new Mat4RotZ(Math.PI/8)).mul(new Mat4Transl(+0.9,-0.7,-0.1))));
                      redraw();
                      cubeRotationZ +=0.005;
                      break;
                  }
                  case KeyEvent.VK_LEFT:{
                      cube.setModel(cube.getModel().mul(new Mat4Transl(-0.9,+0.7,0.1).mul( new Mat4RotZ(-Math.PI/8)).mul(new Mat4Transl(+0.9,-0.7,-0.1))));
                      redraw();
                      cubeRotationZ -=0.005;
                      break;
                  }
              }
              }
           });


        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
                redraw();
            }
        });
    }


    private void setComboBox(JComboBox comboBox) {
        this.comboBox = comboBox;
    }

    private void redraw() {
        if (modeCleared)
            panel.clear();
        width = panel.getRaster().getWidth();
        height = panel.getRaster().getHeight();

        Graphics g = panel.getRaster().getGraphics();
        g.setColor(Color.white);
        g.drawString("mode (cleared every redraw): " + modeCleared, 10, 10);
        g.drawString("(c) UHK FIM PGRF", width - 120, height - 10);

        scene.getSolids().forEach(solid -> {
            if(solid.getSolidId()!="1"){
                solid.setModel(new Mat4RotXYZ(xInc, yInc, zInc)
                        .mul(new Mat4Transl(xTransform, yTransform, zTransform))
                        .mul(new Mat4Scale(zoom, zoom, zoom)));
            }
        });

        scene.render(scene);

        visibility.clear();
        scene.setView(cameraView.getViewMatrix());
        panel.repaint();
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        redraw();
    }
}
