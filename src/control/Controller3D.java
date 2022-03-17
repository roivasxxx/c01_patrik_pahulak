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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class Controller3D implements Controller, ActionListener {

    private final Panel panel;

    Arrow xAxis = new Arrow('x');
    Arrow yAxis = new Arrow('y');
    Arrow zAxis = new Arrow('z');
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
    Pyramid pyramid = new Pyramid(
    		a,b,c,d,e
    		);
    Triangle abe = new Triangle(a,b,e,new Col(255,255,0));
    Triangle bde = new Triangle(b,d,e,new Col(125,125,0));
    Triangle cde=new Triangle(c,a,e,new Col(0,255,255));
    Triangle dae=new Triangle(d,a,e,new Col(0,125,125));
    double cubeSize=0.2;
    Cube cube=new Cube(new Vertex(new Point3D(0.8,-0.8,0)),cubeSize);
    BicubicSurface bc=new BicubicSurface();

    private Visibility visibility;
    private Rasterizer rasterizer;
    private Renderer renderer;
    private double yTransform = 0, xTransform = 0, zTransform = 0,
            yInc = 0, xInc = 0, zInc = 0, zoom = 1;
    double cubeRotation=0;
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
                ,triangle2,triangleOutlined
                ,pyramid,abe,bde,cde,cube,
                bc,triangleStrip
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
                      cube.setModel(new Mat4Transl(-0.9,+0.7,0.1).mul( new Mat4RotX(cubeRotation)).mul(new Mat4Transl(+0.9,-0.7,-0.1)));
                      redraw();
                      cubeRotation+=0.05;
                      break;
                  }
                  case KeyEvent.VK_DOWN:{
                      cube.setModel(new Mat4Transl(-0.9,+0.7,0.1).mul( new Mat4RotX(cubeRotation)).mul(new Mat4Transl(+0.9,-0.7,-0.1)));
                      redraw();
                      cubeRotation-=0.05;
                      break;
                  }
              }
              }
           });
        //             case KeyEvent.VK_BACK_SPACE:
        //                 hardClear();
        //                 break;
        //             case KeyEvent.VK_M:
        //                 modeCleared = !modeCleared;
        //                 panel.clear();
        //                 break;
        //         }

        //         switch (key.getKeyCode()) {
        //             //Movement
        //             case KeyEvent.VK_SHIFT -> {
        //                 cameraView = cameraView.up(0.1);
        //                 redraw();
        //             }
        //             case KeyEvent.VK_CONTROL -> {
        //                 cameraView = cameraView.down(0.1);
        //                 redraw();
        //             }
        //             case KeyEvent.VK_W -> {
        //                 cameraView = cameraView.forward(0.1);
        //                 redraw();
        //             }
        //             case KeyEvent.VK_S -> {
        //                 cameraView = cameraView.backward(0.1);
        //                 redraw();
        //             }
        //             case KeyEvent.VK_A -> {
        //                 cameraView = cameraView.left(0.1);
        //                 redraw();
        //             }
        //             case KeyEvent.VK_D -> {
        //                 cameraView = cameraView.right(0.1);
        //                 redraw();
        //             }
        //             //Rotations
        //             case KeyEvent.VK_E -> zInc += 0.1;
        //             case KeyEvent.VK_Q -> zInc -= 0.1;
        //             case KeyEvent.VK_R -> yInc += 0.1;
        //             case KeyEvent.VK_F -> yInc -= 0.1;
        //             case KeyEvent.VK_X -> xInc -= 0.1;
        //             case KeyEvent.VK_C -> xInc += 0.1;
        //             //Transforms
        //             case KeyEvent.VK_NUMPAD9 -> zTransform += 0.1;
        //             case KeyEvent.VK_NUMPAD7 -> zTransform -= 0.1;
        //             case KeyEvent.VK_NUMPAD4 -> yTransform += 0.1;
        //             case KeyEvent.VK_NUMPAD6 -> yTransform -= 0.1;
        //             case KeyEvent.VK_NUMPAD8 -> xTransform += 0.1;
        //             case KeyEvent.VK_NUMPAD5 -> xTransform -= 0.1;
        //             //zoom
        //             case KeyEvent.VK_NUMPAD1 -> zoom += 0.1;
        //             case KeyEvent.VK_NUMPAD2 -> zoom -= 0.1;
        //             //extends
        //             case KeyEvent.VK_U -> {
        //                 try {
        //                     shadeI = shadeI + 1;
        //                     rasterizer.setShader(shaderList.get(shadeI));
        //                 }catch (Exception e){
        //                     shadeI = 0;
        //                     rasterizer.setShader(shaderList.get(shadeI));
        //                 }
        //             }
        //             case KeyEvent.VK_O -> {
        //                 outline = !outline;
        //                 visibility.setOutline(outline);
        //             }
        //             case KeyEvent.VK_P -> {
        //                 if (perspective) {
        //                     scene.setProjection(new Mat4OrthoRH(
        //                             2, 2, 0.1, 100));
        //                 } else {
        //                     scene.setProjection(new Mat4PerspRH(
        //                             (float) Math.PI / 2, 1, 0.1, 100));
        //                 }
        //                 perspective = !perspective;
        //                 redraw();
        //             }

        //             case KeyEvent.VK_G -> {
        //                 JOptionPane.showMessageDialog(null,
        //                         getSolidDialog(), "Choose solid dialog", JOptionPane.PLAIN_MESSAGE);

        //                 if(comboBox.getSelectedItem() != null){
        //                     JOptionPane.showMessageDialog(null,
        //                             getTransformDialog(),
        //                             comboBox.getSelectedItem().toString() + " transform dialog",
        //                             JOptionPane.PLAIN_MESSAGE);
        //                 }
        //                 redraw();
        //             }
        //             default -> throw new IllegalStateException("Unexpected value: " + key.getKeyCode());
        //         }
        //         redraw();
        //     }
        // });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
                redraw();
            }
        });
    }

    // private Object getTransformDialog() {
    //     JPanel jPanel = new JPanel();

    //     Button btnActive = new Button("Set active.");
    //     Button btnAnimation = new Button("Turn on/off animation.");

    //     for (Solid s : scene.getSolids()) {

    //         if (s.getClass().getName().substring(16).equals(comboBox.getSelectedItem().toString())) {
    //             active = s;
    //         }
    //     }

    //     ActionListener transformListener = actionEvent -> {
    //         deActivate();
    //         active.setActiveSolid();
    //     };

    //     ActionListener animation = actionEvent -> {
    //         if (tm) timer.start();
    //         else timer.stop();
    //         tm = !tm;
    //     };

    //     btnActive.addActionListener(transformListener);
    //     btnAnimation.addActionListener(animation);
    //     jPanel.add(btnActive);
    //     jPanel.add(btnAnimation);
    //     return jPanel;
    // }

    // private void deActivate() {
    //     for (Solid deActive : scene.getSolids()) {
    //         deActive.deActiveSolid();
    //     }
    // }

    // private JPanel getSolidDialog() {
    //     JPanel jPanel = new JPanel();
    //     jPanel.add(new Label("Choose what solid do you want to change:"));
    //     List<String> options = new ArrayList<>();

    //     for (Solid s : scene.getSolids()) {
    //         String tmpStr = s.getClass().getName();
    //         options.add(tmpStr.substring(16));
    //     }

    //     JComboBox comboBox = new JComboBox(options.toArray());
    //     setComboBox(comboBox);
    //     comboBox.addItemListener(e -> setComboBox(comboBox));
    //     jPanel.add(comboBox);
    //     return jPanel;
    // }

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

    private void hardClear() {
        panel.clear();
        initObjects(panel.getRaster());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        redraw();
    }
}
