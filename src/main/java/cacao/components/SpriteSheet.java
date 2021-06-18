package cacao.components;

import cacao.renderer.Texture;
import cacao.utils.AssetPool;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {
    private Texture texture;
    private List<Sprite> sprites;

    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing) {
        this.sprites = new ArrayList<>();

        this.texture = texture;

        int currentX  = 0;
        // Bottom left corner of the top left sprite
        int currentY  = texture.getHeight() - spriteHeight;

        for (int i = 0; i < numSprites; i++) {
            float rightX = (currentX + spriteWidth) / (float)texture.getWidth();
            float leftX = currentX / (float)texture.getWidth();

            float topY = (currentY + spriteHeight) / (float)texture.getHeight();
            float bottomY = currentY / (float)texture.getHeight();

            Vector2f[] texCoords = new Vector2f[]{
                    new Vector2f(leftX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(rightX, topY)
            };

            Sprite sprite = new Sprite(this.texture, texCoords);
            this.sprites.add(sprite);

            currentX += spriteWidth;
            if (currentX < texture.getWidth()) continue;

            currentX = 0;
            // Move one row down
            currentY -= (float)spriteHeight;
        }
    }

    // Returns the user specified sprite from the sprite sheet
    public Sprite getSprite(int index) {
        return this.sprites.get(index);
    }

}
