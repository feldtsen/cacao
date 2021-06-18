package cacao.renderer;

import cacao.components.GameObject;
import cacao.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    private static final int MAX_BATCH_SIZE = 1000;
    List<BatchRenderer> batches;

    public Renderer() {
        batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);
        if (sprite != null) add(sprite);
    }

    private void add(SpriteRenderer sprite) {
        boolean spriteAddedToBatch = false;

        // Check if we can place the sprite in an existing batch
        for (BatchRenderer batch : batches) {
           Texture texture = sprite.getTexture();

           /*
            * Check the next batch if
            *   1. There is a texture associated with the sprite
            *   2. or the current batch does not already have the texture stored
            *   3. or the texture list for the current batch is full
            *   4. or if the batch does not have room
            * */

            if (!batch.hasRoom())
                continue;

            if (!(texture == null || batch.hasTexture(texture) || batch.hasTextureRoom()))
                continue;

            // Add the sprite to the current batch
            batch.addSprite(sprite);
            spriteAddedToBatch = true;
            break;
        }

        if (spriteAddedToBatch) return;

        // Create a new batch
        BatchRenderer newBatch = new BatchRenderer(MAX_BATCH_SIZE);
        newBatch.start();
        batches.add(newBatch);
        newBatch.addSprite(sprite);
    }

    public void render() {
        for (BatchRenderer batch : batches) batch.render();
    }
}
