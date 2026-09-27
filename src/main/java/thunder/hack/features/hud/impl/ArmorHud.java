package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import thunder.hack.features.hud.HudElement;
import thunder.hack.setting.Setting;
import thunder.hack.utility.render.Render2DEngine;

import java.util.ArrayList;
import java.util.List;

public class ArmorHud extends HudElement {
    public ArmorHud() {
        super("ArmorHud", 60, 25);
    }

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.V2);

    private enum Mode {
        V1, V2
    }

    public void onRender2D(GuiGraphics context) {
        super.onRender2D(context);
        float xItemOffset = getPosX();
        List<ItemStack> armorSlots = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
            armorSlots.add(mc.player.getItemBySlot(slot));
        }
        armorSlots = armorSlots.reversed();
        for (ItemStack itemStack : armorSlots) {
            if (itemStack.isEmpty()) continue;

            if (mode.is(Mode.V1)) {
                context.renderItem(itemStack, (int) xItemOffset, (int) getPosY());
                context.drawItemInSlot(mc.font,itemStack,  (int) xItemOffset, (int) getPosY());
            } else {
                context.renderItem(itemStack, (int) xItemOffset, (int) getPosY());
                Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
                float offset = (equippable != null && equippable.slot() == EquipmentSlot.HEAD) ? -4 : 0;
                Render2DEngine.addWindow(context.pose(), (int) xItemOffset, getPosY() + offset + (15 - offset) * ((float) itemStack.getDamageValue() / (float) itemStack.getMaxDamage()), xItemOffset + 15, getPosY() + 15, 1f);
                context.renderItem(itemStack, (int) xItemOffset, (int) getPosY());
                Render2DEngine.popWindow();
            }
            xItemOffset += 20;
        }

        setBounds(getPosX(), getPosY(), 60, 25);
    }
}
