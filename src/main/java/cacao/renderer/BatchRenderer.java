package cacao.renderer;

import cacao.Window;
import cacao.components.SpriteRenderer;
import cacao.utils.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class BatchRenderer {

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private final float[] vertices;
    private final int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private int vaoID, vboID;
    private final int maxBatchSize;
    private final Shader shader;

    private final int MAXIMUM_TEXTURES_PER_BATCH = 16;

    // A list of textures used in this batch
    private List<Texture> textures;

    /*
    * VERTEX
    *
    * Position             color                            tex coords          tex id
    * float, float,        float, float, float, float       float, float        float
    * */

    private static final int
            // sizes
            POSITION_SIZE = 2,
            COLOR_SIZE = 4,
            TEX_COORDS_SIZE = 2,
            TEX_ID_SIZE = 1,
            VERTEX_SIZE = POSITION_SIZE + COLOR_SIZE + TEX_COORDS_SIZE + TEX_ID_SIZE,
            VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES,
            NUM_VERTICES_IN_QUAD = 4,
            NUM_INDICES_IN_QUAD = 6,
            // offsets
            POSITION_OFFSET = 0,
            COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES,
            TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES,
            TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;

    public BatchRenderer(int maxBatchSize) {
        shader = AssetPool.getShader("default.glsl", "default.glsl");
        shader.compile();

        this.sprites = new SpriteRenderer[maxBatchSize];
        this.textures = new ArrayList<>();

        this.maxBatchSize = maxBatchSize;

        // batch size + how many vertices needed for one quad + how big each vertex is (position + color + [whatever you have given it])
        vertices = new float[maxBatchSize * NUM_VERTICES_IN_QUAD * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
    }

    public void start() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POSITION_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
        // Use transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void addSprite(SpriteRenderer sprite) {
        // Get index and add renderObject
        int index = numSprites;
        this.sprites[index] = sprite;
        this.numSprites++;

        // If we the sprite have a texture
        if (sprite.getTexture() != null) {
            // Check if we already have the texture
            if (!textures.contains(sprite.getTexture())) {
                textures.add(sprite.getTexture());
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (numSprites >= maxBatchSize) hasRoom = false;

    }

    public void loadVertexProperties(int index) {
        SpriteRenderer sprite = sprites[index];

        // Find offset within array (4 vertices per sprite)
        // positions        color                       etc
        // float float      float float float float     ...potentially more
        int offset = index * NUM_VERTICES_IN_QUAD * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();
        int texID = 0;

        // If the sprite have a texture
        if (sprite.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if(textures.get(i) == sprite.getTexture()) {
                    // [0, tex0, tex1, text2, ...]
                    // First 0 is to reserve a position for no texture
                    texID = i + 1;
                    break;
                }
            }
        }

        /*
        * Add vertices with appropriate properties
        *
        *   (0,1)________(1,1)
        *   |  .            |
        *   |       .       |
        *   |           .   |
        *   (0,0)_______(1,0)
         */

        float xAdd = 0.0f, yAdd = 1.0f;

        for (int i = 0; i < NUM_VERTICES_IN_QUAD; i++) {

            switch (i) {
                case 0 -> {}
                case 1 -> {
                    xAdd = 1.0f;
                    yAdd = 0.0f;
                }
                case 2 -> {
                    xAdd = 0.0f;
                }
                case 3 -> {
                    xAdd = 1.0f;
                    yAdd = 1.0f;
                }
            }

            // Load position
            vertices[offset]     = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            // Load color
            vertices[offset + 2] = color.x; // r
            vertices[offset + 3] = color.y; // g
            vertices[offset + 4] = color.z; // b
            vertices[offset + 5] = color.w; // a

            // Load texture coordinates
            vertices[offset + 6] = texCoords[i].x; // u
            vertices[offset + 7] = texCoords[i].y; // v

            // Load texture id
            vertices[offset + 8] = texID;

            offset += VERTEX_SIZE;
        }
    }

    public void render(){
        // For now, we will re-buffer all data every frame
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Shader
        shader.use();
        shader.uploadMat4f("uProj", Window.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

        for (int i = 0; i < textures.size(); i++) {
            // [0, tex0, tex1, text2, ...]
            // First 0 is to reserve a position for no texture
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }

        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, numSprites * NUM_INDICES_IN_QUAD, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }

        shader.detach();
    }

    public int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int [NUM_INDICES_IN_QUAD * maxBatchSize];

        for (int i = 0; i < maxBatchSize; i++) {
           loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = NUM_INDICES_IN_QUAD * index;
        int offset_index = NUM_VERTICES_IN_QUAD * index;

        /*
        * Generate a quad that will hold the sprite
        *   OBS: counter-clockwise order
        *
        *   0_______________3
        *   |  .            |
        *   |       .       |
        *   |           .   |
        *   2_______________1
        *
        * */

        // The first triangle
        elements[offsetArrayIndex]     = offset_index;
        elements[offsetArrayIndex + 1] = offset_index + 2;
        elements[offsetArrayIndex + 2] = offset_index + 1;

        // The second triangle
        elements[offsetArrayIndex + 3] = offset_index;
        elements[offsetArrayIndex + 4] = offset_index + 1;
        elements[offsetArrayIndex + 5] = offset_index + 3;

    }

    public boolean hasRoom() {
        return hasRoom;
    }

    public boolean hasTextureRoom() {
        return textures.size() < MAXIMUM_TEXTURES_PER_BATCH;
    }

    public boolean hasTexture(Texture texture) {
        return textures.contains(texture);
    }
}
