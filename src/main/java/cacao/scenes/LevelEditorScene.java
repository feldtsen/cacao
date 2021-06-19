package cacao.scenes;

import cacao.Window;
import cacao.camera.Camera2D;
import cacao.components.GameObject;
import cacao.components.Sprite;
import cacao.components.SpriteRenderer;
import cacao.components.SpriteSheet;
import cacao.listeners.KeyListener;
import cacao.renderer.Texture;
import cacao.renderer.Transform;
import cacao.utils.AssetPool;
import cacao.utils.Time;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.security.Key;

import static org.lwjgl.glfw.GLFW.*;


public class LevelEditorScene extends Scene {

    public LevelEditorScene() { }

    @Override
    public void init() {
        // Load the necessary resources for the current scene, if they don't exist it's created
        loadResources();

        camera = new Camera2D(new Vector2f());

        SpriteSheet units = AssetPool.getSpriteSheet("spritesheet.png");

        int size = 30;
        int offset = 0;
        int row = 0;

   

        for (int i = 0; i < 2000; i++) {
            // Mario
            GameObject mario = new GameObject(
                    "Mario" + offset,
                    new Transform(new Vector2f(offset, row * size), new Vector2f(size , size))
            );
            //Sprite sprite = new Sprite(AssetPool.getTexture("testImage.png"));
            Sprite sprite = units.getSprite(i % 26);
            mario.addComponent(new SpriteRenderer(sprite));

            // Goomba
            //GameObject goomba = new GameObject(
            //        "Goomba" + offset,
            //        new Transform(new Vector2f(offset, row * size + 340), new Vector2f(size , size))
            //);
            //sprite = new Sprite(AssetPool.getTexture("testImage2.png"));
            //goomba.addComponent(new SpriteRenderer(sprite));

            addGameObjectToScene(mario);
            //addGameObjectToScene(goomba);

            offset += size;

            if (offset >= 1300){
                offset = 0;
                row++;
            }

        }

    }

    private void loadResources() {
        AssetPool.getShader(
                "default.glsl",
                "default.glsl"
        );

        AssetPool.addSpriteSheet(
                "spritesheet.png",
                new SpriteSheet(
                        AssetPool.getTexture("spritesheet.png"),
                        16, 16, 26, 0
                )
        );

    }

    private int spriteIndex = 1;
    private float delayMs = .2f;
    private float timeElapsed;
    @Override
    public void update(float dt) {
        timeElapsed += dt;

        for (GameObject go : this.gameObjects) {
            //go.transform.position.x += Math.random() * 100 * dt;

            if(timeElapsed >= delayMs) {
                go.getComponent(SpriteRenderer.class).setSprite(AssetPool.getSpriteSheet("spritesheet.png").getSprite(spriteIndex));
                spriteIndex = ( 1 + ((spriteIndex + 1) % 3));
            }

            go.update(dt);
        }
        if (timeElapsed >= delayMs)
            timeElapsed = 0f;

        if (KeyListener.isKeyPressed(GLFW_KEY_W))
            getCamera().move(0, 300, dt);
        if (KeyListener.isKeyPressed(GLFW_KEY_S))
            getCamera().move(0, -300, dt);
        if (KeyListener.isKeyPressed(GLFW_KEY_A))
            getCamera().move(-300, 0, dt);
        if (KeyListener.isKeyPressed(GLFW_KEY_D))
            getCamera().move(300, 0, dt);

        renderer.render();

    }

}
