package thunder.hack.features.modules.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class TotemAnimation extends Module {
    public TotemAnimation() {
        super("TotemAnimation", Category.RENDER);
    }

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.FadeOut);
    private final Setting<Integer> speed = new Setting<>("Speed", 40, 1, 100);

    private ItemStack floatingItem = null;
    private int floatingItemTimeLeft;

    public void showFloatingItem(ItemStack floatingItem) {
        this.floatingItem = floatingItem;
        floatingItemTimeLeft = getTime();
    }

    @Override
    public void onUpdate() {
        if (floatingItemTimeLeft > 0) {
            --floatingItemTimeLeft;
            if (floatingItemTimeLeft == 0) {
                floatingItem = null;
            }
        }
    }

    public void renderFloatingItem(GuiGraphics context, float tickDelta) {
        if (floatingItem != null && floatingItemTimeLeft > 0 && !mode.is(Mode.Off)) {
            int scaledWidth = mc.getWindow().getGuiScaledWidth();
            int scaledHeight = mc.getWindow().getGuiScaledHeight();

            int elapsedTime = getTime() - floatingItemTimeLeft;
            float animationProgress = ((float) elapsedTime + tickDelta) / (float) getTime();
            float progressSquared = animationProgress * animationProgress;
            float progressCubed = animationProgress * progressSquared;
            float oscillationFactor = 10.25F * progressCubed * progressSquared - 24.95F * progressSquared * progressSquared + 25.5F * progressCubed - 13.8F * progressSquared + 4.0F * animationProgress;
            float oscillationRadians = oscillationFactor * 3.1415927F;
            float scale = 8.0F + 28.0F * Mth.sin(oscillationRadians);

            context.renderItem(floatingItem, (int) ((float) scaledWidth / 2f - scale / 2f), (int) ((float) scaledHeight / 2f - scale / 2f), (int) scale);
        }
    }

    private int getTime() {
        int invertedSpeed = 101 - speed.getValue();

        if (mode.is(Mode.FadeOut))
            return invertedSpeed / 4;

        if (mode.is(Mode.Insert))
            return invertedSpeed / 2;

        return invertedSpeed;
    }

    private enum Mode {
        FadeOut, Size, Otkisuli, Insert, Fall, Rocket, Roll, Off
    }
}
