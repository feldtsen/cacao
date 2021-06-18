package cacao.components;

import cacao.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component{

    private Vector4f color;

    private Sprite sprite;

    public SpriteRenderer(Vector4f color) {
        init(color, new Sprite(null));
    }

    public SpriteRenderer(Sprite sprite) {
        init(new Vector4f(1, 1, 1, 1), sprite);
    }

    private void init(Vector4f color, Sprite sprite) {
        this.color = color;
        this.sprite = sprite;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }

    @Override
    public void start() {
    }

    @Override
    public void update(float dt) {
    }

    public Vector4f getColor() {
        return color;
    }
}
