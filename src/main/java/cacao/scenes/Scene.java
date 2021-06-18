package cacao.scenes;

import cacao.camera.Camera2D;
import cacao.components.GameObject;
import cacao.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera2D camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();


    public Scene() {

    }

    public void init() {

    }

    public void start() {
        for (GameObject go : gameObjects) {
           go.start();
           renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        gameObjects.add(go);
        if (!isRunning) return;
        // If the game is running, we stat the gameObject directly
        go.start();
        renderer.add(go);
    }

    public abstract void update(float dt);

    public Camera2D getCamera() {
        return camera;
    }

}
