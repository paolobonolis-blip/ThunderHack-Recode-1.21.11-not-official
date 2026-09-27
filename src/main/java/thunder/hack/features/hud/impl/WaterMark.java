package thunder.hack.features.hud.impl;

import com.mojang.math.Axis;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.features.modules.client.Media;
import thunder.hack.features.modules.misc.NameProtect;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.TextUtil;
import thunder.hack.utility.render.TextureStorage;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;

public class WaterMark extends HudElement {
    public WaterMark() {
        super("WaterMark", 100, 35);
    }

    public static final Setting<Mode> mode = new Setting<>("Mode", Mode.Big);
    private final Setting<Boolean> ru = new Setting<>("RU", false);

    private final TextUtil textUtil = new TextUtil(
            "ТандерХак",
            "ГромХак",
            "ГрозаКлиент",
            "ТандерХуй",
            "ТандерХряк",
            "ТандерХрюк",
            "ТиндерХак",
            "ТундраХак",
            "ГромВзлом"
    );

    private enum Mode {
        Big, Small, Classic, BaltikaClient, Rifk
    }

    public void onRender2D(GuiGraphics context) {
        super.onRender2D(context);
        String username = ((ModuleManager.media.isEnabled() && Media.nickProtect.getValue()) || ModuleManager.nameProtect.isEnabled()) ? (ModuleManager.nameProtect.isEnabled() ? NameProtect.getCustomName() : "Protected") : mc.getUser().getName();

        if (mode.getValue() == Mode.Big) {
            Render2DEngine.drawHudBase(context.pose(), getPosX(), getPosY(), 106, 30, HudEditor.hudRound.getValue());
            FontRenderers.thglitch.drawString(context.pose(), "THUNDERHACK", getPosX() + 5.5, getPosY() + 5, -1);
            FontRenderers.monsterrat.drawGradientString(context.pose(), "recode", getPosX() + 35.5f, getPosY() + 21f, 1);
            setBounds(getPosX(), getPosY(), 106, 30);
        } else if (mode.getValue() == Mode.Small) {
            if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
                float offset1 = FontRenderers.sf_bold.getStringWidth(username) + 72;
                float offset2 = FontRenderers.sf_bold.getStringWidth((mc.isLocalServer() ? "SinglePlayer" : mc.getConnection().getServerData().ip));
                float offset3 = (Managers.PROXY.isActive() ? FontRenderers.sf_bold.getStringWidth(Managers.PROXY.getActiveProxy().getName()) + 11 : 0);

                Render2DEngine.drawRoundedBlur(context.pose(), getPosX(), getPosY(), 50f, 15f, 3, HudEditor.blurColor.getValue().getColorObject());
                Render2DEngine.drawRoundedBlur(context.pose(), getPosX() + 55, getPosY(), offset1 + offset2 - 36 + offset3, 15f, 3, HudEditor.blurColor.getValue().getColorObject());

                Render2DEngine.setupRender();

                Render2DEngine.drawRect(context.pose(), getPosX() + 13, getPosY() + 1.5f, 0.5f, 11, new Color(0x44FFFFFF, true));

                FontRenderers.sf_bold.drawGradientString(context.pose(), "Recode", getPosX() + 18, getPosY() + 5, 20);

                Render2DEngine.renderGradientTexture(context.pose(), getPosX() + 1, getPosY() + 2, 11, 11, 0, 0, 128, 128, 128, 128,
                        HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));

                Render2DEngine.renderGradientTexture(context.pose(), getPosX() + 58, getPosY() + 3, 8, 8, 0, 0, 128, 128, 128, 128,
                        HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));

                Render2DEngine.renderGradientTexture(context.pose(), getPosX() + offset1, getPosY() + 2, 10, 10, 0, 0, 128, 128, 128, 128,
                        HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));

                if (Managers.PROXY.isActive()) {
                    Render2DEngine.renderGradientTexture(context.pose(), getPosX() + offset1 + offset2 + 16, getPosY() + 2, 10, 10, 0, 0, 128, 128, 128, 128,
                            HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(180), HudEditor.getColor(90));

                    FontRenderers.sf_bold.drawString(context.pose(), Managers.PROXY.getActiveProxy().getName(), getPosX() + offset1 + offset2 + 28, getPosY() + 5, -1);
                }

                Render2DEngine.endRender();

                Render2DEngine.setupRender();
                FontRenderers.sf_bold.drawString(context.pose(), username, getPosX() + 68, getPosY() + 4.5f, HudEditor.textColor.getValue().getColor());
                FontRenderers.sf_bold.drawString(context.pose(), (mc.isLocalServer() ? "SinglePlayer" : mc.getConnection().getServerData().ip), getPosX() + offset1 + 13, getPosY() + 4.5f, HudEditor.textColor.getValue().getColor());
                Render2DEngine.endRender();
                setBounds(getPosX(), getPosY(), 100, 15f);
            } else {
                String info = ChatFormatting.DARK_GRAY + "| " + ChatFormatting.RESET + username + ChatFormatting.DARK_GRAY + " | " + ChatFormatting.RESET + Managers.SERVER.getPing() + " ms" + ChatFormatting.DARK_GRAY + " | " + ChatFormatting.RESET + (mc.isLocalServer() ? "SinglePlayer" : mc.getConnection().getServerData().ip);
                float width = FontRenderers.sf_bold.getStringWidth("ThunderHack " + info) + 5;
                Render2DEngine.drawHudBase(context.pose(), getPosX(), getPosY(), width, 10, 3);
                FontRenderers.sf_bold.drawGradientString(context.pose(), ru.getValue() ? textUtil + " " : "ThunderHack ", getPosX() + 2, getPosY() + 2.5f, 10);
                FontRenderers.sf_bold.drawString(context.pose(), info, getPosX() + 2 + FontRenderers.sf_bold.getStringWidth("ThunderHack "), getPosY() + 2.5f, HudEditor.textColor.getValue().getColor());
                setBounds(getPosX(), getPosY(), width, 10);
            }

        } else if (mode.getValue() == Mode.BaltikaClient) {
            Render2DEngine.drawHudBase(context.pose(), getPosX(), getPosY(), 100, 64, HudEditor.hudRound.getValue());

            Render2DEngine.addWindow(context.pose(), getPosX(), getPosY(), getPosX() + 100, getPosY() + 64, 1f);
            context.pose().pushPose();
            context.pose().translate(getPosX() + 10, getPosY() + 32, 0);
            context.pose().mulPose(Axis.ZP.rotation((float) Math.toRadians(mc.player.tickCount * 3 + Render3DEngine.getTickDelta())));
            context.pose().translate(-(getPosX() + 10), -(getPosY() + 32), 0);
            context.blit(TextureStorage.baltika, (int) getPosX() - 10, (int) getPosY() + 2, 0, 0, 40, 64, 40, 64);
            context.pose().popPose();
            Render2DEngine.popWindow();

            FontRenderers.thglitch.drawString(context.pose(), "BALTIKA", getPosX() + 43, getPosY() + 41.5, -1);
            setBounds(getPosX(), getPosY(), 100, 64);
        } else if (mode.is(Mode.Rifk)) {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

            // лень вставлять реал билд дату
            // too lazy to insert the real build date

            String info = ChatFormatting.GREEN + String.format("th7 | build: 16/06/2024 | rate: %d | %s", Math.round(Managers.SERVER.getTPS()), format.format(date));
            float width = FontRenderers.profont.getStringWidth(info) + 5;
            Render2DEngine.drawRectWithOutline(context.pose(), getPosX(), getPosY(), width, 8, Color.decode("#192A1A"), Color.decode("#833B7B"));
            Render2DEngine.drawGradientBlurredShadow1(context.pose(), getPosX(), getPosY(), width, 8, 10, Color.decode("#161A1E"), Color.decode("#161A1E"), Color.decode("#382E37"), Color.decode("#382E37"));
            FontRenderers.profont.drawString(context.pose(), info, getPosX() + 2.7, getPosY() + 2.953, HudEditor.textColor.getValue().getColor());
            setBounds(getPosX(), getPosY(), width, 8);
        } else {
            FontRenderers.monsterrat.drawGradientString(context.pose(), "ThunderHack v" + ThunderHack.VERSION, getPosX() + 5.5f, getPosY() + 5, 10);
            setBounds(getPosX(), getPosY(), 100, 3);
        }
    }

    @Override
    public void onUpdate() {
        textUtil.tick();
    }
}