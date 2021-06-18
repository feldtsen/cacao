package cacao.renderer;

import cacao.utils.FileLoaderManager;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private String filepath;
    private String fileName;

    private int texID;

    private int width, height;

    public Texture(String fileName) {
        this.fileName = fileName;
        this.filepath = FileLoaderManager.getTextureFilePath(fileName);

        // Generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);


        //Set texture parameters
        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // When stretching the image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // When shrinking an image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);


        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if (image != null) {
            this.width = width.get(0);
            this.height = height.get(0);
            switch (channels.get(0)) {
                case 3:
                    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                            0, GL_RGB, GL_UNSIGNED_BYTE, image);
                    break;
                case 4:
                    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                            0, GL_RGBA, GL_UNSIGNED_BYTE, image);
                    break;
                default:
                    assert false : "Error (Texture): unknown number of channels " + channels.get(0);
                    break;
            }
        } else {
            assert false : "Error: (Texture) Could not load image '" + filepath + "'";
        }

        // Since we have the image on the GPU, we no longer need to store it
        // all we need is the texture ID, it let's us index the texture on the GPU
        stbi_image_free(image);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getFileName() {
        return fileName;
    }
}
