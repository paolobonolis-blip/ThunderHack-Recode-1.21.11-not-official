package thunder.hack.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import thunder.hack.api.IAddon;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.utility.ThunderUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static thunder.hack.features.modules.Module.mc;

public class MainMenuScreen extends Screen {
    private final List<MainMenuButton> buttons = new ArrayList<>();
    public boolean confirm = false;
    public static int ticksActive;

    protected MainMenuScreen() {
        super(Component.nullToEmpty("THMainMenuScreen"));
        INSTANCE = this;

        buttons.add(new MainMenuButton(-110, -70, I18n.get("menu.singleplayer").toUpperCase(Locale.ROOT), () -> mc.setScreen(new SelectWorldScreen(this))));
        buttons.add(new MainMenuButton(4, -70, I18n.get("menu.multiplayer").toUpperCase(Locale.ROOT), () -> mc.setScreen(new JoinMultiplayerScreen(this))));
        buttons.add(new MainMenuButton(-110, -29, I18n.get("menu.options")
                .toUpperCase(Locale.ROOT)
                .replace(".", ""), () -> mc.setScreen(new OptionsScreen(this, mc.options, mc.level != null))));
        buttons.add(new MainMenuButton(4, -29, "CLICKGUI", () -> ModuleManager.clickGui.setGui()));
        buttons.add(new MainMenuButton(-110, 12, I18n.get("menu.quit").toUpperCase(Locale.ROOT), mc::stop, true));
    }

    private static MainMenuScreen INSTANCE = new MainMenuScreen();

    public static MainMenuScreen getInstance() {
        ticksActive = 0;

        if (INSTANCE == null) {
            INSTANCE = new MainMenuScreen();
        }
        return INSTANCE;
    }

    @Override
    public void tick() {
        ticksActive++;

        if (ticksActive > 400) {
            ticksActive = 0;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        GuiGraphics context = new GuiGraphics(extractor);
        float halfOfWidth = mc.getWindow().getGuiScaledWidth() / 2f;
        float halfOfHeight = mc.getWindow().getGuiScaledHeight() / 2f;

        float mainX = halfOfWidth - 120f;
        float mainY = halfOfHeight - 80f;
        float mainWidth = 240f;
        float mainHeight = 140;

        // Render2DEngine.drawMainMenuShader(context.getMatrices(), 0, 0, halfOfWidth * 2f, halfOfHeight * 2);
        extractBackground(extractor, mouseX, mouseY, delta);

        Render2DEngine.drawHudBase(context.pose(), mainX, mainY, mainWidth, mainHeight, 20);

        buttons.forEach(b -> b.onRender(context, mouseX, mouseY));

        boolean hoveredLogo = Render2DEngine.isHovered(mouseX, mouseY, (int) (halfOfWidth - 120), (int) (halfOfHeight - 130), 210, 50);

        FontRenderers.thglitchBig.drawCenteredString(context.pose(), "THUNDERHACK", (int) (halfOfWidth), (int) (halfOfHeight - 120), new Color(255, 255, 255, hoveredLogo ? 230 : 180).getRGB());

        boolean hovered = Render2DEngine.isHovered(mouseX, mouseY, halfOfWidth - 50, halfOfHeight + 70, 100, 10);

        FontRenderers.sf_medium.drawCenteredString(context.pose(), "<-- Back to default menu", halfOfWidth, halfOfHeight + 70, hovered ? -1 : Render2DEngine.applyOpacity(-1, 0.6f));
        //  FontRenderers.sf_medium.drawString(context.getMatrices(), "By Pan4ur & 06ED", halfOfWidth * 2 - FontRenderers.sf_medium.getStringWidth("By Pan4ur & 06ED") - 5f, halfOfHeight * 2 - 10, Render2DEngine.applyOpacity(-1, 0.4f));

        onlineText:
        {
            String onlineUsers = String.format("online: %s%s", ChatFormatting.DARK_GREEN, Managers.TELEMETRY.getOnlinePlayers().size());

            FontRenderers.sf_bold.drawCenteredString(context.pose(), onlineUsers, halfOfWidth, halfOfHeight * 2 - 15, Color.GREEN);

            context.pose().pushPose();
            context.pose().translate(halfOfWidth - 10 - FontRenderers.sf_medium.getStringWidth(onlineUsers) / 2f, halfOfHeight * 2 - 17, 0);
            Render2DEngine.drawBloom(context.pose(), Render2DEngine.applyOpacity(Color.GREEN, 0.6f), 9f);
            context.pose().popPose();

            context.pose().pushPose();
            context.pose().translate(halfOfWidth - 10 - FontRenderers.sf_medium.getStringWidth(onlineUsers) / 2f, halfOfHeight * 2 - 17, 0);
            Render2DEngine.drawBloom(context.pose(), Render2DEngine.applyOpacity(Color.GREEN, (float) (0.5f + (Math.sin((double) System.currentTimeMillis() / 500)) / 2f)), 9f);
            context.pose().popPose();

        }

        Render2DEngine.drawHudBase(context.pose(), mc.getWindow().getGuiScaledWidth() - 40, mc.getWindow().getGuiScaledHeight() - 40, 30, 30, 5, Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getGuiScaledWidth() - 40, mc.getWindow().getGuiScaledHeight() - 40, 30, 30) ? 0.7f : 1f);
        context.blit(TextureStorage.thTeam, mc.getWindow().getGuiScaledWidth() - 40, mc.getWindow().getGuiScaledHeight() - 40, 30, 30);

        Render2DEngine.drawHudBase(context.pose(), mc.getWindow().getGuiScaledWidth() - 80, mc.getWindow().getGuiScaledHeight() - 40, 30, 30, 5, Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getGuiScaledWidth() - 80, mc.getWindow().getGuiScaledHeight() - 40, 30, 30) ? 0.7f : 1f);
        context.blit(TextureStorage.donation, mc.getWindow().getGuiScaledWidth() - 79, mc.getWindow().getGuiScaledHeight() - 39, 28, 28);

        int offsetY = 10;
        for (String change : ThunderUtility.changeLog) {
            String prefix = getPrefix(change);
            FontRenderers.sf_medium.drawString(context.pose(), prefix, 10, offsetY, Render2DEngine.applyOpacity(-1, 0.4f));
            offsetY += 10;
        }

        int totalAddonsLoaded = Managers.ADDON.getTotalAddons();
        String addonsText = "Addons Loaded: " + totalAddonsLoaded;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int textWidth = (int) FontRenderers.sf_bold.getStringWidth(addonsText);
        int textX = screenWidth - textWidth - 5;
        FontRenderers.sf_bold.drawString(context.pose(), addonsText, textX, 5, Color.WHITE.getRGB());

        int offset = 0;
        for (IAddon addon : Managers.ADDON.getAddons()) {
            // for (String addon : Arrays.asList("Addon", "Addon2", "Addon3", "Addon4", "Addon5")) {
            textWidth = (int) FontRenderers.sf_bold.getStringWidth(addon.getName() + " |");
            textX = screenWidth - textWidth - 5;
            FontRenderers.sf_bold.drawString(context.pose(), addon.getName() + ChatFormatting.WHITE + " |", textX, 13 + offset, Color.GRAY.getRGB());
            offset += 9;
        }
    }

    private static @NotNull String getPrefix(@NotNull String change) {
        String prefix = "";
        if (change.contains("[+]")) {
            change = change.replace("[+] ", "");
            prefix = ChatFormatting.GREEN + "[+] " + ChatFormatting.RESET;
        } else if (change.contains("[-]")) {
            change = change.replace("[-] ", "");
            prefix = ChatFormatting.RED + "[-] " + ChatFormatting.RESET;
        } else if (change.contains("[/]")) {
            change = change.replace("[/] ", "");
            prefix = ChatFormatting.LIGHT_PURPLE + "[/] " + ChatFormatting.RESET;
        } else if (change.contains("[*]")) {
            change = change.replace("[*] ", "");
            prefix = ChatFormatting.GOLD + "[*] " + ChatFormatting.RESET;
        }
        return prefix + change;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        float halfOfWidth = mc.getWindow().getGuiScaledWidth() / 2f;
        float halfOfHeight = mc.getWindow().getGuiScaledHeight() / 2f;
        buttons.forEach(b -> b.onClick((int) mouseX, (int) mouseY));

        if (Render2DEngine.isHovered(mouseX, mouseY, halfOfWidth - 50, halfOfHeight + 70, 100, 10)) {
            confirm = true;
            mc.setScreen(new TitleScreen());
            confirm = false;
        }

        if (Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getGuiScaledWidth() - 40, mc.getWindow().getGuiScaledHeight() - 40, 40, 40))
            mc.setScreen(CreditsScreen.getInstance());

        if (Render2DEngine.isHovered(mouseX, mouseY, mc.getWindow().getGuiScaledWidth() - 90, mc.getWindow().getGuiScaledHeight() - 40, 40, 40))
            Util.getPlatform().openUri(URI.create("https://www.donationalerts.com/r/06ed/"));

        if (Render2DEngine.isHovered(mouseX, mouseY, (int) (halfOfWidth - 157), (int) (halfOfHeight - 140), 300, 70))
            Util.getPlatform().openUri(URI.create("https://thunderhack-site.vercel.app/"));

        return super.mouseClicked(event, doubleClick);
    }
}
