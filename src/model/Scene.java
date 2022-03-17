package model;

import model.Solid;
import transforms.Mat4;
import transforms.Mat4Identity;
import render.Renderer;

import java.util.ArrayList;
import java.util.List;

public class Scene{
    private List<Solid> solids = new ArrayList<>();
    private List<Solid> axis = new ArrayList<>();
    private Renderer renderer;

    private Mat4 view = new Mat4Identity();
    private Mat4 projection = new Mat4Identity();

    public Mat4 getView() {
        return view;
    }

    public Mat4 getProjection() {
        return projection;
    }


    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }

    public Scene(Renderer renderer) {
        this.renderer = renderer;
    }

    public List<Solid> getSolids() {
        return solids;
    }

    public List<Solid> getAxis() {
        return axis;
    }

    public void render(Scene scene) {
        /*for(Solid solid : scene.getAxis()){
            Mat4 mat = view.mul(projection);
            renderer.render(solid, mat);
        }*/

        for(Solid solid : scene.getSolids()){
            Mat4 mat = solid.getModel().mul(getView()).mul(getProjection());
            renderer.render(solid, mat);
        }
    }
}
