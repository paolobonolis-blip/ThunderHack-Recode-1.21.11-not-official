package thunder.hack.features.modules.player;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import thunder.hack.features.modules.Module;
import thunder.hack.utility.Timer;

public class MouseElytraFix extends Module {
    public MouseElytraFix() {
        super("MouseElytraFix", Category.PLAYER);
    }

    private final Timer delay = new Timer();

    @Override
    public void onUpdate() {
        Equippable carried = mc.player.containerMenu.getCarried().get(DataComponents.EQUIPPABLE);
        if (carried != null && carried.slot() == EquipmentSlot.CHEST && !ElytraSwap.swapping) {
            if (delay.every(300))
                if (mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
                    mc.gameMode.handleContainerInput(0, 6, 1, ClickType.PICKUP.toContainerInput(), mc.player);
                    int empty = findEmptySlot();
                    boolean needDrop = (empty == 999);
                    if (needDrop)
                        empty = 9;
                    mc.gameMode.handleContainerInput(0, empty, 1, ClickType.PICKUP.toContainerInput(), mc.player);
                    if (needDrop)
                        mc.gameMode.handleContainerInput(0, -999, 1, ClickType.PICKUP.toContainerInput(), mc.player);
                }
        }
    }

    public static int findEmptySlot() {
        for (int i = 0; i < 36; i++)
            if (mc.player.getInventory().getItem(i).isEmpty()) return i < 9 ? i + 36 : i;
        return 999;
    }
}
