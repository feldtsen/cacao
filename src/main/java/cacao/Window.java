package cacao;

import cacao.listeners.KeyListener;
import cacao.listeners.MouseListener;
import cacao.scenes.LevelEditorScene;
import cacao.scenes.LevelScene;
import cacao.scenes.Scene;
import cacao.utils.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


/*
* The Window class represent the layer between the client and OS
* */

public class Window {
    private static Window INSTANCE = null;

    private static Scene currentScene = null;

    private boolean isFullscreen = false;

    /*
    * Where the window is in memory space
    * */

    private long glfwWindow;

    /*
    * The width and height of the game window
    * */

    private int width, height;

    /*
    * The title shown in the game window border
    * */

    private String title;

    private Window() {
        this.width   = 1920;
        this.height  = 1080;
        this.title   = "Game Window";
    }

    /*
    * Used to activate a scene
    *   newScene: which scene to switch to
    * */
    public static void changeScene(int sceneIndex) {
        boolean validScene = true;

        switch (sceneIndex) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene: " + sceneIndex;
                validScene = false;
        }

        if(validScene) {
            currentScene.init();
            currentScene.start();
        }
    }

    /*
     * Ensures only one instance is created (singleton)
     * */

    public static Window getInstance() {

        /*
         * If the window instance do not exist, create one
         * */

        if (INSTANCE == null) INSTANCE = new Window();

        return INSTANCE;
    }


    /*
    * If you want access to the currently running scene
    * */
    public static Scene getCurrentScene() {
        return getInstance().currentScene;
    }


    /*
    * Optional configuration
    * */

    public void setup(int width, int height, String title) {
           this.width  = width;
           this.height = height;
           this.title  = title;
    }

    public void setup(int width, int height, String title, boolean isFullscreen) {
       this.setup(width, height, title);
       this.isFullscreen = isFullscreen;
    }

    /*
    * Runs necessary initialization processes, open the window and start the game loop
    * */

    public void run() {
        System.out.println("LWJGL version: " + Version.getVersion());

        init();
        loop();


        // Free the memory when the loop exists (since LWJGL use C bindings)
        // The OS will do it for us, but it's good practice
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GFLW and the free error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {

        /*
         * Setup an error callback
         * */

        GLFWErrorCallback.createPrint(System.err).set();

        /*
        * Initialize GLFW
        * */

        if(!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        /*
         * Configure GLFW
         * GLFW will use these hints to create the window
         * */

        glfwDefaultWindowHints();
        // Should not be visible until we are done creating the window
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        // You can resize the window
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        // When  the window starts, it is in the maximized position
        if(isFullscreen) glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        /*
         * Create the window
         * */

        // Return where the window is in memory space
        // NULL = default values: 1st NULL: primary monitor, 2nd NULL: no sharing
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        // Check if the window creation was successful
        if(glfwWindow == NULL) throw new IllegalStateException("Failed to create the GLFW window");

        /*
         * Setup mouse listeners
         * */

        // Setup cursor callback for changing mouse position
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        // Setup mouse button callback
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        // Setup mouse scroll callback
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

        /*
         * Setup keyboard listeners
         * */

        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        /*
         * Make the OpenGL context current
         * */

        glfwMakeContextCurrent(glfwWindow);

        /*
         * 0: v-sync disabled
         * 1: v-sync enabled
        * */

        glfwSwapInterval(0);

        /*
        * Make the window visible
        * */

        glfwShowWindow(glfwWindow);

        /*
        * Needed for LWJGL interoperation with GLFW's OpenGL context (or any external context)
        * In short, it makes bindings available for use
        * */

        GL.createCapabilities();

        /*
        * Set the initial scene
        * */

        Window.changeScene(0);
    }

    private void loop() {
        float beginTime = Time.getTime(),
                endTime = Time.getTime();
        float dt = endTime - beginTime;

        while(!glfwWindowShouldClose(glfwWindow)) {

            /*
            * Get different poll events (puts them in the event listeners)
            * Mouse events, key events...
            * */

            glfwPollEvents();

            // Set the clear color
            glClearColor(.2f, .2f, .25f, 1.0f);
            // Tells OpenGl how to clear the buffer (flush the screen with the clear color)
            glClear(GL_COLOR_BUFFER_BIT);

            /*
            * Update the current scene
            * */

            currentScene.update(dt);
            // Time.printFps(dt);

            // Swap the buffer
            glfwSwapBuffers(glfwWindow);

            /*
            * Calculate the time spent since last frame
            * */

            endTime = Time.getTime();
            // The time difference
            dt = endTime - beginTime;
            beginTime = endTime;
        }

    }
}
