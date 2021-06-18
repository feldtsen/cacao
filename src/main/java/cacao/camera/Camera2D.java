package cacao.camera;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera2D {
    private Matrix4f projectionMatrix, viewMatrix;

    private Vector2f position;

    public Camera2D(Vector2f position) {
        this.position = position;

        // This is initialized to avoid bugs
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, 32.0f * 40.f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);

        viewMatrix.identity();

        return viewMatrix.lookAt(
            new Vector3f(position.x, position.y, 20.0f),
            cameraFront.add(position.x, position.y, 0.0f),
            cameraUp
        );
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void move(float x, float y, float dt) {
        position.x += dt * x;
        position.y += dt * y;
    }

}
