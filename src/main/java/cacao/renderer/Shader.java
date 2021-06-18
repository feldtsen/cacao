package cacao.renderer;

import cacao.utils.FileLoaderManager;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private final String vertexShaderSource;
    private final String fragmentShaderSource;
    private int shaderProgramID;
    private boolean beingUsed = false;

    public Shader(String vertexShaderFileName, String fragmentShaderFileName) {
        vertexShaderSource = FileLoaderManager.loadVertexShaderSource(vertexShaderFileName);
        fragmentShaderSource = FileLoaderManager.loadFragmentShaderSource(fragmentShaderFileName);

    }

    public void compile() {
        int vertexID = compileHelper(GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentID = compileHelper(GL_FRAGMENT_SHADER, fragmentShaderSource);
        shaderProgramID = link(vertexID, fragmentID);
    }


    /*
     * Given the GL shader type and shader source code
     *   -> compiles the shader and return the shader ID
     * */

    private int compileHelper(int GL_TYPE_SHADER, String shaderSrc) {
        // Compile and link shaders
        // First load and compile the shader
        int shaderID = glCreateShader(GL_TYPE_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(shaderID, shaderSrc);
        glCompileShader(shaderID);

        // Check for errors in compilation
        int success = glGetShaderi(shaderID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: compilation for shaderID " + shaderID + " failed");
            System.out.println(glGetShaderInfoLog(shaderID, len));
            assert false : "";
        }

        return shaderID;
    }

     /*
     * Given the vertex and fragment IDs
     *   -> links them together to form a program
     * */

     private int link(int vertexID, int fragmentID) {
         // Link shaders
         int shaderProgramID = glCreateProgram();
         glAttachShader(shaderProgramID, vertexID);
         glAttachShader(shaderProgramID, fragmentID);
         glLinkProgram(shaderProgramID);

         // Check for errors
         int success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
         if (success == GL_FALSE) {
             int len = glGetProgrami(shaderProgramID, GL_COMPILE_STATUS);
             System.out.println("ERROR: compilation for shaderProgramID " + shaderProgramID + " failed to link shaders");
             System.out.println(glGetShaderInfoLog(shaderProgramID, len));
             assert false : "";
         }

         return shaderProgramID;
     }

     /*
     * Bind the program
     * */

     public void use() {
         if(!beingUsed) {
             glUseProgram(shaderProgramID);
             beingUsed = true;
         }
     }

     /*
     * Use no program
     * */

     public void detach() {
         glUseProgram(0);
         beingUsed = false;
     }

     public int getShaderProgramID() {
         return shaderProgramID;
     }

     public void uploadMat4f(String varName, Matrix4f mat4) {
         int varLocation = glGetUniformLocation(shaderProgramID, varName);

         use();

         FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
         mat4.get(matBuffer);
         glUniformMatrix4fv(varLocation, false, matBuffer);
     }

     public void uploadMat3f(String varName, Matrix3f mat3) {
         int varLocation = glGetUniformLocation(shaderProgramID, varName);

         use();

         FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
         mat3.get(matBuffer);
         glUniformMatrix3fv(varLocation, false, matBuffer);
     }

     public void uploadVec4f(String varName, Vector4f vec4f) {
         int varLocation = glGetUniformLocation(shaderProgramID, varName);

         use();

         glUniform4f(varLocation, vec4f.x, vec4f.y, vec4f.z, vec4f.w);
     }

     public void uploadVec3f(String varName, Vector3f vec3f) {
         int varLocation = glGetUniformLocation(shaderProgramID, varName);

         use();

         glUniform3f(varLocation, vec3f.x, vec3f.y, vec3f.z);
     }
     public void uploadVec2f(String varName, Vector2f vec2f) {
         int varLocation = glGetUniformLocation(shaderProgramID, varName);

         use();

         glUniform2f(varLocation, vec2f.x, vec2f.y);
     }

     public void uploadFloat(String varName, float val) {
         int varLocation = glGetUniformLocation(shaderProgramID, varName);

         use();

         glUniform1f(varLocation, val);
     }

     public void uploadInt(String varName, int val) {
         int varLocation = glGetUniformLocation(shaderProgramID, varName);

         use();

         glUniform1i(varLocation, val);
     }

     public void uploadTexture(String varName, int slot) {
         int varLocation = glGetUniformLocation(shaderProgramID, varName);

         use();

         glUniform1i(varLocation, slot);
     }

     public void uploadIntArray(String varName, int[] array) {
         int varLocation = glGetUniformLocation(shaderProgramID, varName);

         use();

         glUniform1iv(varLocation, array);
     }

}
