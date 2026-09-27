package thunder.hack.features.modules.player;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.equipment.Equippable;
import thunder.hack.core.Managers;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;
import thunder.hack.utility.render.TextureStorage;

import java.awt.*;

import static thunder.hack.features.modules.client.ClientSettings.isRu;

public class DurabilityAlert extends Module {
    public DurabilityAlert() {
        super("DurabilityAlert", Category.PLAYER);
    }

    private final Setting<Boolean> friends = new Setting<>("Friend message", true);
    private final Setting<Integer> percent = new Setting<>("Percent", 20, 1, 100);
    private boolean need_alert = false;
    private final Timer timer = new Timer();

    @Override
    public void onUpdate() {
        if (friends.getValue()) {
            for (Player player : mc.level.players()) {
                if (!Managers.FRIEND.isFriend(player)) continue;
                if (player == mc.player) continue;
                for (EquipmentSlot slot : EquipmentSlot.VALUES) {
                    if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
                    ItemStack stack = player.getItemBySlot(slot);
                    if (stack.isEmpty() || !(stack.getItem() instanceof net.minecraft.world.item.Item)) continue;
                    if (!isArmor(stack)) continue;
                    if (getDurability(stack) < percent.getValue() && timer.passedMs(30000)) {
                        mc.player.connection.sendCommand("msg " + player.getName().getString() + (isRu() ? " Срочно чини броню!" : " Fix your armor right now!"));

                        timer.reset();
                    }
                }
            }
        }

        boolean flag = false;
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
            ItemStack stack = mc.player.getItemBySlot(slot);
            if (stack.isEmpty() || !isArmor(stack)) continue;
            if (getDurability(stack) < percent.getValue()) {
                need_alert = true;
                flag = true;
            }
        }
        if (!flag && need_alert) need_alert = false;
    }

    public static boolean isArmor(ItemStack stack) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.slot() == null) return false;
        return equippable.slot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
    }

    public void onRender2D(GuiGraphics context) {
        if (need_alert) {
            FontRenderers.sf_bold.drawCenteredString(context.pose(), isRu() ? "Срочно чини броню!" : "Fix your armor right now!", (float) mc.getWindow().getGuiScaledWidth() / 2f, (float) mc.getWindow().getGuiScaledHeight() / 3f, new Color(0xFFDF00).getRGB());

            Color c1 = new Color(0xFFDF00);
            context.blit(TextureStorage.brokenShield, (int) (mc.getWindow().getGuiScaledWidth() / 2f - 40), (int) (mc.getWindow().getGuiScaledHeight() / 3f - 120), 0, 0, 80, 80, 80, 80);
        }
    }

    public static int getDurability(ItemStack stack) {
        return (int) ((stack.getMaxDamage() - stack.getDamageValue()) / Math.max(0.1, stack.getMaxDamage()) * 100.0f);
    }
}
