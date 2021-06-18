import cacao.Window;

public class Main {
    public static void main(String[] args) {
        /*
        * Create the window singleton
        * */
        Window window = Window.getInstance();

        window.setup(1920, 1080, "Mario");
        window.run();
    }
}
