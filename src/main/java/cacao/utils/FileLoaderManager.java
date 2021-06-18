package cacao.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileLoaderManager {
    private static final String root = "src/main/resources/assets/";
    private static final String shadersRoot = root + "shaders/";
    private static final String texturesRoot = root + "textures/";

    public static String loadVertexShaderSource(String shaderName) {
       return getContent(shadersRoot + "vertexShaders/" + shaderName);
    }

    public static String loadFragmentShaderSource(String shaderName) {
        return getContent(shadersRoot + "fragmentShaders/" + shaderName);
    }

    private static String getContent(String filePath) {
        StringBuilder result = new StringBuilder();

        try {
            result.append(Files.readString(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: could not open file for shader: " + filePath;
        }
        return result.toString();
    }

    public static String getTextureFilePath(String fileName) {
       return texturesRoot + fileName;
    }
}
