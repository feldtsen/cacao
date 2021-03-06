package cacao.components;

import cacao.renderer.Texture;
import cacao.renderer.Transform;
import org.joml.Vector2f;

import java.util.Arrays;

public class Sprite {
    private Texture texture;
    /*
     * [0,1]     [1,1]
     *
     *
     * [0,0]     [1,0]
     * */
    private Vector2f[] texCoords = new Vector2f[]{
            new Vector2f(0, 1),
            new Vector2f(1, 0),
            new Vector2f(0, 0),
            new Vector2f(1, 1)
    };

    public Sprite(Texture texture) {
        init(texture);
    }

    public Sprite (Texture texture, Vector2f[] texCoords) {
        init(texture, texCoords);
    }

    private void init (Texture texture) {
        this.texture = texture;
    }

    private void init (Texture texture, Vector2f[] texCoords) {
        init(texture);
        this.texCoords = texCoords;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getTexCoords() {
        return texCoords;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if (!(o instanceof Sprite s)) return false;

        return Arrays.equals(s.texCoords, this.texCoords) && s.texture.equals(this.texture);
    }
}
