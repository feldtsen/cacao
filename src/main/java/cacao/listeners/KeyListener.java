package cacao.listeners;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    private static KeyListener INSTANCE = null;

    // There are 350 keys that are detected by GLFW
    private boolean keyPressed[] = new boolean[350];

    private KeyListener() {}

    // Singleton
    public static KeyListener getInstance() {
        if(INSTANCE == null) INSTANCE = new KeyListener();

        return INSTANCE;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        // We don't need to check if it is inbound, since 350 is all the keys possible

        if (action == GLFW_PRESS) {
            getInstance().keyPressed[key] = true;
            return;
        }

        if (action == GLFW_RELEASE) {
            getInstance().keyPressed[key] = false;
        }
    }

    public static boolean isAnyKeyPressed() {
        for(boolean b : getInstance().keyPressed) if(!b) return false;
        return true;
    }

    public static boolean isKeyPressed(int keyCode) {
        // Checks if the key is valid, and then if the key is pressed
        return keyCode < getInstance().keyPressed.length && getInstance().keyPressed[keyCode];
    }
}
