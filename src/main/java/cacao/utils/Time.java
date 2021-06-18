package cacao.utils;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time {
    // Since it is static, this represent the time that the application starts
    public static double timeStarted = glfwGetTime();

    public static float getTime() {
        // Time elapsed since the application started
       return (float)((glfwGetTime() - timeStarted));
    }

    public static void printFps(float dt) {
        System.out.println("FPS: " + (1.0f/dt));
    }
}
