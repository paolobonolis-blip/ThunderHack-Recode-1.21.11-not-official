package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import thunder.hack.features.hud.HudElement;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;
import thunder.hack.utility.render.animation.AnimationUtility;

import java.awt.*;
import net.minecraft.client.gui.GuiGraphics;

public class CandleHud extends HudElement {
    public CandleHud() {
        super("Candle", 10, 100);
    }

    private Setting<Integer> scale = new Setting<>("Scale", 25, 15, 100);
    private Setting<For> mode = new Setting<>("For", For.Win);

    private float xAnim, yAnim, prevPitch;

    public void onRender2D(GuiGraphics context) {
        super.onRender2D(context);

        float yDelta = mc.player.yHeadRotO - mc.player.getYHeadRot();
        if (yDelta > 0) xAnim = AnimationUtility.fast(xAnim, -15, 10);
        else if (yDelta < 0) xAnim = AnimationUtility.fast(xAnim, 35, 10);
        else xAnim = AnimationUtility.fast(xAnim, 10, 10);

        float pDelta = prevPitch - mc.player.getXRot();
        if (pDelta > 0) yAnim = AnimationUtility.fast(yAnim, -15, 10);
        else if (pDelta < 0) yAnim = AnimationUtility.fast(yAnim, 35, 10);
        else yAnim = AnimationUtility.fast(yAnim, 10, 10);

        prevPitch = mc.player.getXRot();

        context.pose().pushPose();
        context.pose().translate((int) getPosX(), (int) getPosY(), 0);
        float scalefactor = (float) scale.getValue() / 100f;
        context.pose().scale(scalefactor, scalefactor, 1);
        context.blit(TextureStorage.candle, 0, -5, 0, 0, 102, 529, 102, 529);
        context.pose().popPose();

        drawFire(context.pose(), getPosX() + (40 + 15f - xAnim) * scalefactor, getPosY() + (10 - yAnim) * scalefactor, 7 * scalefactor, 7 * scalefactor,
                Render2DEngine.applyOpacity(new Color(0xFA460F), (float) Math.sin((mc.player.tickCount + 10) / 15f) + 1.4f));
        drawFire(context.pose(), getPosX() + (40 + 5.5f - xAnim / 2f) * scalefactor, getPosY() + (15 - yAnim / 2f) * scalefactor, 15 * scalefactor, 15 * scalefactor,
                Render2DEngine.applyOpacity(new Color(0xFF8F1E), (float) Math.sin((mc.player.tickCount + 20) / 15f) + 1.5f));
        drawFire(context.pose(), getPosX() + (40 + 1.5f - xAnim / 3f) * scalefactor, getPosY() + (27 - yAnim / 3f) * scalefactor, 20 * scalefactor, 20 * scalefactor,
                Render2DEngine.applyOpacity(new Color(0xFCC352), (float) Math.sin((mc.player.tickCount + 30) / 15f) + 1.6f));
        drawFire(context.pose(), getPosX() + (40 - xAnim / 4f) * scalefactor, getPosY() + (45 - yAnim / 4f) * scalefactor, 25 * scalefactor, 25 * scalefactor,
                Render2DEngine.applyOpacity(new Color(0xFCD087), (float) Math.sin((mc.player.tickCount + 40) / 15f) + 1.7f));
        drawFire(context.pose(), getPosX() + (40 + 4f - xAnim / 4f) * scalefactor, getPosY() + (43 - yAnim / 4f) * scalefactor, 15 * scalefactor, 15 * scalefactor,
                Render2DEngine.applyOpacity(new Color(0x6690F6), (float) Math.sin((mc.player.tickCount + 40) / 15f) + 1.8f));

        setBounds(getPosX(), getPosY(), (int) (102 * scalefactor), (int) (529 * scalefactor));
    }

    private void drawFire(PoseStack matrices, float x, float y, float width, float height, Color color) {
        width = width + 7 * 2;
        height = height + 7 * 2;
        x = x - 7;
        y = y - 7;

        Render2DEngine.renderTexture(matrices, x, y, width, height, 0, 0, width, height, width, height);
    }

    private enum For {
        Luck, Win, LowPing, AntiKick
    }
}
