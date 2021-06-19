package cacao.components;

import cacao.renderer.Texture;
import cacao.renderer.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component{

    private Vector4f color;

    private Sprite sprite;

    private Transform lastTransform;

    // Sprite have been updated with new information that needs to get rendered by the GPU
    private boolean isDirty = false;

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
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if(!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    public Vector4f getColor() {
        return color;
    }

    public void setSprite(Sprite sprite) {
        // Don't update if nothing has changed
        if(this.sprite.equals(sprite)) return;

        this.sprite = sprite;
        this.isDirty = true;
    }

    public void setColor(Vector4f color) {
        // Don't update if nothing has changed
        if(this.color.equals(color)) return;

        this.color.set(color);
        this.isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setClean() {
       this.isDirty = false;
    }
}
