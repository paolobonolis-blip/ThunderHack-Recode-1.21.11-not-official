package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

import java.awt.*;

public class Crosshair extends Module {
    public Crosshair() {
        super("Crosshair", Category.HUD);
    }

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.Circle);
    private final Setting<Boolean> animated = new Setting<>("Animated", true, v -> mode.is(Mode.Default));
    private final Setting<Boolean> dot = new Setting<>("Dot", false, v -> mode.is(Mode.Default));
    private final Setting<Boolean> t = new Setting<>("T", false, v -> mode.is(Mode.Default));
    private final Setting<ColorMode> colorMode = new Setting<>("ColorMode", ColorMode.Sync);
    public final Setting<ColorSetting> color = new Setting<>("Color", new ColorSetting(0x2250b4b4));
    private final Setting<Boolean> dynamic = new Setting<>("Dynamic", true);
    private final Setting<Float> range = new Setting<>("Range", 30.0f, 0.1f, 120f);
    private final Setting<Float> speed = new Setting<>("Speed", 3.0f, 0.1f, 20f);
    private final Setting<Float> backSpeed = new Setting<>("BackSpeed", 5.0f, 0.1f, 20f);

    private enum ColorMode {
        Custom, Sync
    }

    private enum Mode {
        Circle, WiseTree, Dot, Default
    }

    private float xAnim, yAnim, prevPitch, prevProgress;

    public void onRender2D(GuiGraphics context) {
        if (!mc.options.getCameraType().isFirstPerson()) return;

        float midX = mc.getWindow().getGuiScaledWidth() / 2f;
        float midY = mc.getWindow().getGuiScaledHeight() / 2f;

        float yawDelta = mc.player.yHeadRotO - mc.player.getYHeadRot();
        float pitchDelta = prevPitch - mc.player.getXRot();

        if (yawDelta > 0) xAnim = AnimationUtility.fast(xAnim, midX - range.getValue(), speed.getValue());
        else if (yawDelta < 0) xAnim = AnimationUtility.fast(xAnim, midX + range.getValue(), speed.getValue());
        else xAnim = AnimationUtility.fast(xAnim, midX, backSpeed.getValue());

        if (pitchDelta > 0) yAnim = AnimationUtility.fast(yAnim, midY - range.getValue(), speed.getValue());
        else if (pitchDelta < 0) yAnim = AnimationUtility.fast(yAnim, midY + range.getValue(), speed.getValue());
        else yAnim = AnimationUtility.fast(yAnim, midY, backSpeed.getValue());

        prevPitch = mc.player.getXRot();

        if (!dynamic.getValue()) {
            xAnim = midX;
            yAnim = midY;
        }

        float progress = (360f * mc.player.getAttackStrengthScale(0.5f));
        progress = progress == 0 ? 360f : progress;

        switch (mode.getValue()) {
            case Circle -> {
                Color c1 = colorMode.getValue() == ColorMode.Sync ? HudEditor.hcolor1.getValue().getColorObject() : color.getValue().getColorObject();
                Color c2 = colorMode.getValue() == ColorMode.Sync ? HudEditor.acolor.getValue().getColorObject() : color.getValue().getColorObject();

                Render2DEngine.drawArc(context.pose(), xAnim - 25, yAnim - 25, 50, 50, 0.05f, 0.12f, 0,
                        Render2DEngine.interpolateFloat(prevProgress, progress, Render3DEngine.getTickDelta()), c1, c2);
                prevProgress = progress;
            }
            case WiseTree -> {
                Color color = this.color.getValue().getColorObject();
                context.pose().pushPose();
                context.pose().translate(xAnim, yAnim, 0);
                context.pose().mulPose(Axis.ZP.rotation((System.currentTimeMillis() % 70000) / 70000f * 360f));
                context.pose().translate(-xAnim, -yAnim, 0);
                Render2DEngine.drawRect(context.pose(), xAnim - 0.75f, yAnim - 5, 1.5f, 10, color);
                Render2DEngine.drawRect(context.pose(), xAnim - 5, yAnim - 0.75f, 10, 1.5f, color);
                Render2DEngine.drawRect(context.pose(), xAnim, yAnim - 5, 5, 1.5f, color);
                Render2DEngine.drawRect(context.pose(), xAnim - 5, yAnim + 4, 5.25f, 1.5f, color);
                Render2DEngine.drawRect(context.pose(), xAnim - 5f, yAnim - 5, 1.5f, 4.25f, color);
                Render2DEngine.drawRect(context.pose(), xAnim + 3.5f, yAnim, 1.5f, 5.5f, color);
                context.pose().popPose();
            }
            case Dot -> {
                context.pose().pushPose();
                context.pose().translate(xAnim + 4, yAnim + 4, 0);
                BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

                Color color1 = colorMode.getValue() == ColorMode.Sync ? HudEditor.getColor(1) : color.getValue().getColorObject();
                Matrix4f posMatrix = context.pose().last().pose();
                bufferBuilder.addVertex(posMatrix, 0, -8f, 0).setUv(0f, 1f).setColor(color1.getRGB());
                bufferBuilder.addVertex(posMatrix, -8f, -8f, 0).setUv(1f, 1f).setColor(color1.getRGB());
                bufferBuilder.addVertex(posMatrix, -8f, 0, 0).setUv(1f, 0).setColor(color1.getRGB());
                bufferBuilder.addVertex(posMatrix, 0, 0, 0).setUv(0, 0).setColor(color1.getRGB());
                Render2DEngine.endBuilding(bufferBuilder);
                context.pose().popPose();
            }
            case Default -> {
                Color color = this.color.getValue().getColorObject();

                float offset = animated.getValue() ? -3f + (Render2DEngine.interpolateFloat(prevProgress, progress, Render3DEngine.getTickDelta()) / 100f) : 0;
                prevProgress = progress;

                if (!t.getValue()) {
                    Render2DEngine.drawRect(context.pose(), xAnim - 1, yAnim - 6 + offset, 2, 4, Color.BLACK);
                    Render2DEngine.drawRect(context.pose(), xAnim - 0.5f, yAnim - 5.5f + offset, 1, 3, color);
                }

                Render2DEngine.drawRect(context.pose(), xAnim - 1, yAnim + 2 - offset, 2, 4, Color.BLACK);
                Render2DEngine.drawRect(context.pose(), xAnim - 0.5f, yAnim + 2.5f - offset, 1, 3, color);

                Render2DEngine.drawRect(context.pose(), xAnim - 6 + offset, yAnim - 1, 4, 2, Color.BLACK);
                Render2DEngine.drawRect(context.pose(), xAnim - 5.5f + offset, yAnim - 0.5f, 3, 1, color);

                Render2DEngine.drawRect(context.pose(), xAnim + 2 - offset, yAnim - 1, 4, 2, Color.BLACK);
                Render2DEngine.drawRect(context.pose(), xAnim + 2.5f - offset, yAnim - 0.5f, 3, 1, color);

                if (dot.getValue()) {
                    Render2DEngine.drawRect(context.pose(), xAnim - 1f, yAnim - 1f, 2, 2, Color.BLACK);
                    Render2DEngine.drawRect(context.pose(), xAnim - .5f, yAnim - .5f, 1, 1, color);
                }
            }
        }
    }

    public float getAnimatedPosX() {
        if (xAnim == 0)
            return mc.getWindow().getGuiScaledWidth() / 2f;
        return xAnim;
    }

    public float getAnimatedPosY() {
        if (yAnim == 0)
            return mc.getWindow().getGuiScaledHeight() / 2f;
        return yAnim;
    }
}