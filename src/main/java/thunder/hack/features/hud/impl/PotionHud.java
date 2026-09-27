package thunder.hack.features.hud.impl;

import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

import java.awt.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class PotionHud extends HudElement {
    public PotionHud() {
        super("Potions", 100, 100);
    }

    private float vAnimation, hAnimation;

    private final Setting<Boolean> colored = new Setting<>("Colored", false);

    public static String getDuration(MobEffectInstance pe) {
        if (pe.isInfiniteDuration()) {
            return "*:*";
        } else {
            int var1 = pe.getDuration();
            int mins = var1 / 1200;
            String sec = String.format("%02d", (var1 % 1200) / 20);
            return mins + ":" + sec;
        }
    }

        /*
        Render2DEngine.addWindow(context.getMatrices(), getPosX(), getPosY(), getPosX() + hAnimation, getPosY() + vAnimation, 1f);
        for (StatusEffectInstance potionEffect : effects) {
            StatusEffect potion = potionEffect.getEffectType().value();
            String power = "";
            switch (potionEffect.getAmplifier()) {
                case 0 -> power = "I";
                case 1 -> power = "II";
                case 2 -> power = "III";
                case 3 -> power = "IV";
                case 4 -> power = "V";
            }

            String s = potion.getName().getString() + " " + power;
            String s2 = getDuration(potionEffect) + "";

            Color c = new Color(potionEffect.getEffectType().value().getColor());
            FontRenderers.sf_bold_mini.drawString(context.getMatrices(), s + "  " + s2, getPosX() + 5, getPosY() + 20 + y_offset, colored.getValue() ? c.getRGB() : HudEditor.textColor.getValue().getColor());
            y_offset += 10;
        }*/

    public void onRender2D(GuiGraphics context) {
        super.onRender2D(context);

        int y_offset1 = 0;
        float max_width = 50;

        float pointerX = 0;
        for (MobEffectInstance potionEffect : mc.player.getActiveEffects()) {
            MobEffect potion = potionEffect.getEffect().value();

            if (y_offset1 == 0)
                y_offset1 += 4;

            y_offset1 += 9;

            float nameWidth = FontRenderers.sf_bold_mini.getStringWidth(potion.getDisplayName().getString() + " " + (potionEffect.getAmplifier() + 1));
            float timeWidth = FontRenderers.sf_bold_mini.getStringWidth(getDuration(potionEffect));
            float width = (nameWidth + timeWidth) * 1.4f;

            if (width > max_width)
                max_width = width;

            if (timeWidth > pointerX)
                pointerX = timeWidth;
        }

        vAnimation = AnimationUtility.fast(vAnimation, 14 + y_offset1, 15);
        hAnimation = AnimationUtility.fast(hAnimation, max_width, 15);

        Render2DEngine.drawHudBase(context.pose(), getPosX(), getPosY(), hAnimation, vAnimation, HudEditor.hudRound.getValue());

        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Glowing)) {
            FontRenderers.sf_bold.drawCenteredString(context.pose(), "Potions", getPosX() + hAnimation / 2, getPosY() + 4, HudEditor.textColor.getValue().getColorObject());
        } else {
            FontRenderers.sf_bold.drawGradientCenteredString(context.pose(), "Potions", getPosX() + hAnimation / 2, getPosY() + 4, 10);
        }

        if (y_offset1 > 0) {
            if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
                Render2DEngine.drawRectDumbWay(context.pose(), getPosX() + 4, getPosY() + 13, getPosX() + getWidth() - 4, getPosY() + 13.5f, new Color(0x54FFFFFF, true));
            } else {
                Render2DEngine.horizontalGradient(context.pose(), getPosX() + 2, getPosY() + 13.7f, getPosX() + 2 + hAnimation / 2f - 2, getPosY() + 14, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
                Render2DEngine.horizontalGradient(context.pose(), getPosX() + 2 + hAnimation / 2f - 2, getPosY() + 13.7f, getPosX() + 2 + hAnimation - 4, getPosY() + 14, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));
            }
        }

        Render2DEngine.addWindow(context.pose(), getPosX(), getPosY(), getPosX() + hAnimation, getPosY() + vAnimation, 1f);
        int y_offset = 0;
        for (MobEffectInstance potionEffect : mc.player.getActiveEffects()) {
            MobEffect potion = potionEffect.getEffect().value();

            float px = getPosX() + (max_width - pointerX - 10);

            context.pose().pushPose();
            context.pose().translate(getPosX() + 2, getPosY() + 16 + y_offset, 0);
            context.pose().popPose();

            FontRenderers.sf_bold_mini.drawString(context.pose(), potion.getDisplayName().getString() + " " + ChatFormatting.RED + (potionEffect.getAmplifier() + 1), getPosX() + 12, getPosY() + 19 + y_offset, HudEditor.textColor.getValue().getColor());
            FontRenderers.sf_bold_mini.drawCenteredString(context.pose(), getDuration(potionEffect), px + (getPosX() + max_width - px) / 2f, getPosY() + 19 + y_offset, HudEditor.textColor.getValue().getColor());
            Render2DEngine.drawRect(context.pose(), px, getPosY() + 17 + y_offset, 0.5f, 8, new Color(0x44FFFFFF, true));
            y_offset += 9;
        }
        Render2DEngine.popWindow();
        setBounds(getPosX(), getPosY(), hAnimation, vAnimation);
    }
}