package cacao.listeners;

import java.util.stream.IntStream;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener INSTANCE;

    private double scrollX, scrollY,
                   xPos, yPos, lastX, lastY;

    private boolean mouseButtonPressed[] = new boolean[3],
                    isDragging;

    private MouseListener() {
        // It's important to set this up to avoid bugs in the initial frames (like accessing some random location in memory)
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos    = 0.0;
        this.yPos    = 0.0;
        this.lastX   = 0.0;
        this.lastY   = 0.0;
    }

    /*
     * We just want one instance (singleton)
     *  */
    public static MouseListener getInstance() {
        if (INSTANCE == null) INSTANCE = new MouseListener();

        return INSTANCE;
    }

    public static void mousePosCallback(long window, double xPos, double yPos) {
       getInstance().lastX = getInstance().xPos;
       getInstance().lastY = getInstance().yPos;
       getInstance().xPos = xPos;
       getInstance().yPos = yPos;

        // Given that the position have changed and that a mouse button is being pressed
        // implies dragging motion
        getInstance().isDragging = isMouseButtonPressed();
    }

    private static boolean isMouseButtonPressed() {
        // checks if any of the mouse buttons are currently being pressed
        return IntStream.range(0, getInstance().mouseButtonPressed.length).anyMatch(i -> getInstance().mouseButtonPressed[i]);
    }

    // mods are just combination keys, like CTRL, SHIFT...
    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        // Finished if the button is not supported
        if(button >= MouseListener.getInstance().mouseButtonPressed.length)
            return;

        switch (action) {
            case GLFW_PRESS -> {
                getInstance().mouseButtonPressed[button] = true;
            }
            case GLFW_RELEASE -> {
                getInstance().mouseButtonPressed[button] = false;
                getInstance().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        getInstance().scrollX = xOffset;
        getInstance().scrollY = yOffset;
    }

    public static void endFrame()
    {
        getInstance().scrollX = 0;
        getInstance().scrollY = 0;
        getInstance().lastX = getInstance().xPos;
        getInstance().lastY = getInstance().yPos;
    }

    public static float getX() {
        return (float)getInstance().xPos;
    }

    public static float getY() {
        return (float)getInstance().yPos;
    }

    public static float getDx() {
        return (float)(getInstance().lastX - getInstance().xPos);
    }

    public static float getDy() {
        return (float)(getInstance().lastY - getInstance().yPos);
    }

    public static float getScrollX() {
        return (float)getInstance().scrollX;
    }

    public static float getScrollY() {
        return (float)getInstance().scrollY;
    }

    public static boolean isDragging() {
        return getInstance().isDragging;
    }

    public static boolean isMouseButtonDown(int button) {
        // Check if valid button, then check if the button is pressed
        return button < getInstance().mouseButtonPressed.length && getInstance().mouseButtonPressed[button];
    }
}
