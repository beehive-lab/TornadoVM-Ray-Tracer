package com.vinhderful.raytracer;

import com.vinhderful.raytracer.renderer.Renderer;
import com.vinhderful.raytracer.scene.Camera;
import com.vinhderful.raytracer.scene.World;
import com.vinhderful.raytracer.shapes.Sphere;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

public class Controller {

    private AnimationTimer timer;

    @FXML
    public Pane pane;
    public Canvas canvas;
    public Slider camX;
    public Slider camY;
    public Slider camZ;
    public Slider camYaw;
    public Slider camPitch;
    public Slider camFOV;
    public Button button;

    private float time;
    private boolean isPlaying = false;

    @FXML
    public void initialize() {
        Renderer renderer = new Renderer(canvas.getGraphicsContext2D());
        World world = new World();
        Camera camera = world.getCamera();

        Sphere sphere3 = new Sphere(new Vector3f(-1.5F, 0, 0), 0.5f, Color.RED, 8F);
        Sphere sphere1 = new Sphere(new Vector3f(0, 0, 0), 0.5f, Color.GREEN, 16F);
        Sphere sphere2 = new Sphere(new Vector3f(1.5F, 0, 0), 0.5f, Color.BLUE, 32F);
        world.addShape(sphere1);
        world.addShape(sphere2);
        world.addShape(sphere3);

        camX.valueProperty().addListener((observable, oldValue, newValue) -> camera.setX(newValue.floatValue()));
        camY.valueProperty().addListener((observable, oldValue, newValue) -> camera.setY(newValue.floatValue()));
        camZ.valueProperty().addListener((observable, oldValue, newValue) -> camera.setZ(newValue.floatValue()));
        camYaw.valueProperty().addListener((observable, oldValue, newValue) -> camera.setYaw(newValue.floatValue()));
        camPitch.valueProperty().addListener((observable, oldValue, newValue) -> camera.setPitch(newValue.floatValue()));
        camFOV.valueProperty().addListener((observable, oldValue, newValue) -> camera.setFOV(newValue.floatValue()));

        world.setLightX(2F);
        world.setLightZ(0F);
        renderer.render(world, camera);
        disableSliders(true);

        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                time = (time + 0.2F) % 360;
                world.setLightX((float) Math.cos(time) * 2F);
                world.setLightZ((float) Math.sin(time) * 2F);

                renderer.render(world, camera);
            }
        };
    }

    public void playPauseAnimation() {
        if (isPlaying) {
            isPlaying = false;
            timer.stop();
            button.setText("Play");
            disableSliders(true);
        } else {
            isPlaying = true;
            timer.start();
            button.setText("Pause");
            disableSliders(false);
        }
    }

    public void disableSliders(boolean state) {
        camX.setDisable(state);
        camY.setDisable(state);
        camZ.setDisable(state);
        camYaw.setDisable(state);
        camPitch.setDisable(state);
        camFOV.setDisable(state);
    }
}
