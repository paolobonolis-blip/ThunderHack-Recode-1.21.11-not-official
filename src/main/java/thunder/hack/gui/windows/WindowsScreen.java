package thunder.hack.gui.windows;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static thunder.hack.core.manager.IManager.mc;

public class WindowsScreen extends Screen {
    private List<WindowBase> windows = new ArrayList<>();
    public static WindowBase lastClickedWindow;
    public static WindowBase draggingWindow;
    private static final Identifier clickGuiIcon = Identifier.fromNamespaceAndPath("thunderhack", "textures/gui/elements/clickgui.png");

    public WindowsScreen(WindowBase... windows) {
        super(Component.nullToEmpty("THWindows"));
        this.windows.clear();
        lastClickedWindow = null;
        this.windows = Arrays.stream(windows).toList();
    }


    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        GuiGraphics context = new GuiGraphics(extractor);
        if (Module.fullNullCheck())
            extractBackground(extractor, mouseX, mouseY, delta);

        PoseStack matrices = context.pose();
        int i = mc.getWindow().getGuiScaledWidth() / 2;

        float offset = (windows.size() * 20f) / -2f - 23;

        Render2DEngine.drawHudBase(matrices, i + offset - 1.5f, mc.getWindow().getGuiScaledHeight() - 25, windows.size() * 20f + 23f, 19, HudEditor.hudRound.getValue());

        context.blit(clickGuiIcon, (int) (i + offset) + 1, mc.getWindow().getGuiScaledHeight() - 23, 15, 15);

        Render2DEngine.drawLine(i + offset + 20, mc.getWindow().getGuiScaledHeight() - 23, i + offset + 20, mc.getWindow().getGuiScaledHeight() - 9, Color.GRAY.getRGB());

        offset += 23;
        for (WindowBase w : windows) {
            Color c = Render2DEngine.isHovered(mouseX, mouseY, i + offset, mc.getWindow().getGuiScaledHeight() - 24, 17, 17) ? new Color(0x7C2F2F2F, true) :
                    !w.isVisible() ? new Color(0x7C1E1E1E, true) : new Color(0x7C3B3B3B, true);
            Render2DEngine.drawRect(matrices, i + offset, mc.getWindow().getGuiScaledHeight() - 24, 17, 17, HudEditor.hudRound.getValue(), 0.7f, c, c, c, c);
            context.blit(w.getIcon() != null ? w.getIcon() : TextureStorage.configIcon, (int) (i + offset) + 3, mc.getWindow().getGuiScaledHeight() - 21, 11, 11);
            offset += 20f;
        }

        windows.stream().filter(WindowBase::isVisible).forEach(w -> {
            if (w != lastClickedWindow)
                w.render(context, mouseX, mouseY);
        });

        if (lastClickedWindow != null && lastClickedWindow.isVisible())
            lastClickedWindow.render(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        windows.forEach(w -> w.mouseReleased(event.x(), event.y(), event.button()));
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        windows.stream().filter(WindowBase::isVisible).forEach(w -> w.mouseClicked(mouseX, mouseY, button));

        int i = mc.getWindow().getGuiScaledWidth() / 2;
        float offset = (windows.size() * 20f) / -2f - 23;

        if (Render2DEngine.isHovered(mouseX, mouseY, (i + offset) + 1, mc.getWindow().getGuiScaledHeight() - 23, 15, 15))
            mc.setScreen(ClickGUI.getClickGui());

        offset += 23;
        for (WindowBase w : windows) {
            if (Render2DEngine.isHovered(mouseX, mouseY, i + offset, mc.getWindow().getGuiScaledHeight() - 24, 17, 17))
                w.setVisible(!w.isVisible());
            offset += 20f;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        windows.stream().filter(WindowBase::isVisible).forEach(w -> w.keyPressed(event.key(), event.scancode(), event.modifiers()));
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        for (char key : event.codepointAsString().toCharArray())
            windows.stream().filter(WindowBase::isVisible).forEach(w -> w.charTyped(key, 0));
        return super.charTyped(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        windows.stream().filter(WindowBase::isVisible).forEach(w -> w.mouseScrolled((int) (verticalAmount * 5D)));
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
