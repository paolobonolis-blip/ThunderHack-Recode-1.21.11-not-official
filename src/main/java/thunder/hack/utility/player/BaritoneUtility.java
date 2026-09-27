package thunder.hack.utility.player;

public final class BaritoneUtility {
    private BaritoneUtility() {
    }

    public static void execute(String command) {
        try {
            Object provider = Class.forName("baritone.api.BaritoneAPI").getMethod("getProvider").invoke(null);
            Object primary = provider.getClass().getMethod("getPrimaryBaritone").invoke(provider);
            Object commandManager = primary.getClass().getMethod("getCommandManager").invoke(primary);
            commandManager.getClass().getMethod("execute", String.class).invoke(commandManager, command);
        } catch (ReflectiveOperationException ignored) {
        }
    }
}
