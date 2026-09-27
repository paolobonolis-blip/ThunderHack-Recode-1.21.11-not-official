package thunder.hack.utility.render;

import org.jetbrains.annotations.NotNull;

public class MSAAFramebuffer {
    public static final int MAX_SAMPLES = 16;

    public static void use(boolean fancy, Runnable drawAction) {
        drawAction.run();
    }

    public static void use(int samples, @NotNull Object mainBuffer, @NotNull Runnable drawAction) {
        drawAction.run();
    }
}
