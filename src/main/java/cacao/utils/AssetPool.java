package cacao.utils;

import cacao.components.SpriteSheet;
import cacao.renderer.Shader;
import cacao.renderer.Texture;

import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, Texture> textures = new HashMap<>();
    private static final Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    // Good to load all assets in an init function of a scene (load screen)
    //      prepare all the shaders that can be used
    public static Shader getShader(String vertexFileName, String fragmentFileName) {
        //  Make a unique identifier
        String shaderName = vertexFileName + fragmentFileName;

        if(!shaders.containsKey(shaderName)) {
            Shader shader = new Shader(vertexFileName, fragmentFileName);
            shader.compile();
            shaders.put(shaderName, shader);
        }

        return shaders.get(shaderName);
    }

    public static Texture getTexture(String fileName) {

        if (!textures.containsKey(fileName)) {
            Texture texture = new Texture(fileName);
            textures.put(fileName, texture);
        }

        return textures.get(fileName);
    }

    // Only store the latest sprite sheet with the given filename
    public static void addSpriteSheet(String fileName, SpriteSheet spriteSheet) {
        spriteSheets.put(fileName, spriteSheet);
    }

    public static SpriteSheet getSpriteSheet(String filename) {
        // If you have not added the sprite sheet to the collection, throw an error
        // This will not be in the release mode, so we should return a default value
        assert spriteSheets.containsKey(filename) : "Error: the sprite sheet " + filename + " does not exist";
        // Else return the stored sprite sheet
        // TODO: create the sprite sheet somewhere else, now it generates everytime a sprite sheet is missing
        SpriteSheet missing = new SpriteSheet(getTexture("missing.png"), 32, 32, 1, 0);
        return spriteSheets.getOrDefault(filename, missing);
    }

}
