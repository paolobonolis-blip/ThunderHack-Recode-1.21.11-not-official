package thunder.hack.utility.player;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.injection.accesors.IInteractionManager;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static thunder.hack.features.modules.Module.mc;

public final class InventoryUtility {
    private static int cachedSlot = -1;

    public static int getItemCount(Item item) {
        if (mc.player == null) return 0;

        int counter = 0;

        for (int i = 0; i <= 44; ++i) {
            ItemStack itemStack = mc.player.getInventory().getItem(i);
            if (itemStack.getItem() != item) continue;
            counter += itemStack.getCount();
        }

        return counter;
    }

    public static SearchInvResult getAxe() {
        if (mc.player == null) return SearchInvResult.notFound();
        int slot = -1;
        float f = 1.0F;

        for (int b1 = 9; b1 < 45; b1++) {
            ItemStack itemStack = mc.player.getInventory().getItem(b1 >= 36 ? b1 - 36 : b1);
            if (itemStack != null && itemStack.getItem() instanceof AxeItem axe) {
                float f1 = axe.components().get(DataComponents.MAX_DAMAGE);
                f1 += EnchantmentHelper.getItemEnchantmentLevel(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS), itemStack);
                if (f1 > f) {
                    f = f1;
                    slot = b1;
                }
            }
        }

        if (slot >= 36) slot = slot - 36;

        if (slot == -1) return SearchInvResult.notFound();
        return new SearchInvResult(slot, true, mc.player.getInventory().getItem(slot));
    }

    public static SearchInvResult getPickAxeHotbar() {
        if (mc.player == null) return SearchInvResult.notFound();

        int slot = -1;
        float f = 1.0F;
        for (int b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack = mc.player.getInventory().getItem(b1);
            if (itemStack != null && itemStack.getItem().getAttackDamageBonus(null, 0.0F, null) > 0.0F) {
                float f1 = 0;
                f1 += EnchantmentHelper.getItemEnchantmentLevel(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY), itemStack);
                if (f1 > f) {
                    f = f1;
                    slot = b1;
                }
            }
        }

        if (slot == -1) return SearchInvResult.notFound();
        return new SearchInvResult(slot, true, mc.player.getInventory().getItem(slot));
    }

    public static SearchInvResult getPickAxe() {
        if (mc.player == null) return SearchInvResult.notFound();

        int slot = -1;
        float f = 1.0F;
        for (int b1 = 9; b1 < 45; b1++) {
            ItemStack itemStack = mc.player.getInventory().getItem(b1);
            if (itemStack != null && itemStack.getItem().getAttackDamageBonus(null, 0.0F, null) > 0.0F) {
                float f1 = 0;
                f1 += EnchantmentHelper.getItemEnchantmentLevel(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY), itemStack);
                if (f1 > f) {
                    f = f1;
                    slot = b1;
                }
            }
        }

        if (slot == -1) return SearchInvResult.notFound();
        return new SearchInvResult(slot, true, mc.player.getInventory().getItem(slot));
    }

    public static SearchInvResult getPickAxeHotBar() {
        if (mc.player == null) return SearchInvResult.notFound();

        int slot = -1;
        float f = 1.0F;
        for (int b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack = mc.player.getInventory().getItem(b1);
            if (itemStack != null && itemStack.getItem().getAttackDamageBonus(null, 0.0F, null) > 0.0F) {
                float f1 = 0;
                f1 += EnchantmentHelper.getItemEnchantmentLevel(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY), itemStack);
                if (f1 > f) {
                    f = f1;
                    slot = b1;
                }
            }
        }

        if (slot == -1) return SearchInvResult.notFound();
        return new SearchInvResult(slot, true, mc.player.getInventory().getItem(slot));
    }

    public static SearchInvResult getSkull() {
        if (mc.player == null) return SearchInvResult.notFound();
        int slot = -1;
        for (int b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack = mc.player.getInventory().getItem(b1);
            if (itemStack != null &&
                    (itemStack.getItem().equals(Items.SKELETON_SKULL)
                            || itemStack.getItem().equals(Items.WITHER_SKELETON_SKULL)
                            || itemStack.getItem().equals(Items.CREEPER_HEAD)
                            || itemStack.getItem().equals(Items.PLAYER_HEAD)
                            || itemStack.getItem().equals(Items.ZOMBIE_HEAD))) {
                slot = b1;
                break;
            }
        }
        if (slot == -1) return SearchInvResult.notFound();
        return new SearchInvResult(slot, true, mc.player.getInventory().getItem(slot));
    }

    public static SearchInvResult getSword() {
        if (mc.player == null) return SearchInvResult.notFound();

        int slot = -1;
        float f = 1.0F;
        for (int b1 = 9; b1 < 45; b1++) {
            ItemStack itemStack = mc.player.getInventory().getItem(b1);
            if (itemStack != null && itemStack.getItem().getAttackDamageBonus(null, 0.0F, null) > 0.0F) {
                float f1 = itemStack.getItem().components().get(DataComponents.MAX_DAMAGE);
                f1 += EnchantmentHelper.getItemEnchantmentLevel(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS), itemStack);
                if (f1 > f) {
                    f = f1;
                    slot = b1;
                }
            }
        }

        if (slot == -1) return SearchInvResult.notFound();
        return new SearchInvResult(slot, true, mc.player.getInventory().getItem(slot));
    }

    public static SearchInvResult getSwordHotBar() {
        if (mc.player == null) return SearchInvResult.notFound();

        int slot = -1;
        float f = 1.0F;
        for (int b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack = mc.player.getInventory().getItem(b1);
            if (itemStack != null && itemStack.getItem().getAttackDamageBonus(null, 0.0F, null) > 0.0F) {
                float f1 = itemStack.getItem().components().get(DataComponents.MAX_DAMAGE);
                f1 += EnchantmentHelper.getItemEnchantmentLevel(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS), itemStack);
                if (f1 > f) {
                    f = f1;
                    slot = b1;
                }
            }
        }

        if (slot == -1) return SearchInvResult.notFound();
        return new SearchInvResult(slot, true, mc.player.getInventory().getItem(slot));
    }

    // TODO check
    public static SearchInvResult getAxeHotBar() {
        if (mc.player == null) return SearchInvResult.notFound();

        int slot = -1;
        float f = 1.0F;
        for (int b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack = mc.player.getInventory().getItem(b1);
            if (itemStack != null && itemStack.getItem() instanceof AxeItem axe) {
                float f1 = axe.components().get(DataComponents.MAX_DAMAGE);
                f1 += EnchantmentHelper.getItemEnchantmentLevel(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS), itemStack);
                if (f1 > f) {
                    f = f1;
                    slot = b1;
                }
            }
        }

        if (slot == -1) return SearchInvResult.notFound();
        return new SearchInvResult(slot, true, mc.player.getInventory().getItem(slot));
    }


    public static int getElytra() {
        if (mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && mc.player.getItemBySlot(EquipmentSlot.CHEST).getDamageValue() < 430)
            return -2;

        int slot = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.getInventory().getItem(i);
            if (s.getItem() == Items.ELYTRA && s.getDamageValue() < 430) {
                slot = i;
                break;
            }
        }

        if (slot < 9 && slot != -1)
            slot = slot + 36;

        return slot;
    }

    public static SearchInvResult findInHotBar(Searcher searcher) {
        if (mc.player != null) {
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.player.getInventory().getItem(i);
                if (searcher.isValid(stack)) {
                    return new SearchInvResult(i, true, stack);
                }
            }
        }

        return SearchInvResult.notFound();
    }

    public static SearchInvResult findItemInHotBar(List<Item> items) {
        return findInHotBar(stack -> items.contains(stack.getItem()));
    }

    public static SearchInvResult findItemInHotBar(Item... items) {
        return findItemInHotBar(Arrays.asList(items));
    }

    public static SearchInvResult findInInventory(Searcher searcher) {
        if (mc.player != null) {
            for (int i = 36; i >= 0; i--) {
                ItemStack stack = mc.player.getInventory().getItem(i);
                if (searcher.isValid(stack)) {
                    if (i < 9) i += 36;
                    return new SearchInvResult(i, true, stack);
                }
            }
        }

        return SearchInvResult.notFound();
    }

    public static SearchInvResult findItemInInventory(List<Item> items) {
        return findInInventory(stack -> items.contains(stack.getItem()));
    }

    public static SearchInvResult findItemInInventory(Item... items) {
        return findItemInInventory(Arrays.asList(items));
    }

    public static SearchInvResult findBlockInHotBar(@NotNull List<Block> blocks) {
        return findItemInHotBar(blocks.stream().map(Block::asItem).toList());
    }

    public static SearchInvResult findBlockInHotBar(Block... blocks) {
        return findItemInHotBar(Arrays.stream(blocks).map(Block::asItem).toList());
    }

    public static SearchInvResult findBlockInInventory(@NotNull List<Block> blocks) {
        return findItemInInventory(blocks.stream().map(Block::asItem).toList());
    }

    public static SearchInvResult findBlockInInventory(Block... blocks) {
        return findItemInInventory(Arrays.stream(blocks).map(Block::asItem).toList());
    }

    public static void saveSlot() {
        cachedSlot = mc.player.getInventory().getSelectedSlot();
    }

    public static void returnSlot() {
        if (cachedSlot != -1)
            switchTo(cachedSlot);
        cachedSlot = -1;
    }

    public static void saveAndSwitchTo(int slot) {
        saveSlot();
        if (mc.player == null || mc.getConnection() == null) return;
        if (mc.player.getInventory().getSelectedSlot() == slot && Managers.PLAYER.serverSideSlot == slot)
            return;
        mc.player.getInventory().setSelectedSlot(slot);
        ((IInteractionManager) mc.gameMode).syncSlot();
    }

    public static void switchTo(int slot) {
        if (mc.player == null || mc.getConnection() == null) return;
        if (mc.player.getInventory().getSelectedSlot() == slot && Managers.PLAYER.serverSideSlot == slot)
            return;
        mc.player.getInventory().setSelectedSlot(slot);
        ((IInteractionManager) mc.gameMode).syncSlot();
    }

    public static void switchToSilent(int slot) {
        if (mc.player == null || mc.getConnection() == null) return;
        mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
    }

    public static SearchInvResult getAntiWeaknessItem() {
        if (mc.player == null) return SearchInvResult.notFound();

        Item mainHand = mc.player.getMainHandItem().getItem();
        if (mainHand.getAttackDamageBonus(null, 0.0F, null) > 0.0F) {
            return new SearchInvResult(mc.player.getInventory().getSelectedSlot(), true, mc.player.getMainHandItem());
        }

        return findInHotBar(
                itemStack -> itemStack.getItem().getAttackDamageBonus(null, 0.0F, null) > 0.0F
        );
    }

    public static float getHitDamage(@NotNull ItemStack weapon, Player ent) {
        if (mc.player == null) return 0;
        float baseDamage = 1f;

        if (weapon.getItem().getAttackDamageBonus(null, 0.0F, null) > 0.0F)
            baseDamage = 7;

        if (weapon.getItem() instanceof AxeItem axeItem)
            baseDamage = 9;

        if (mc.player.fallDistance > 0 || ModuleManager.criticals.isEnabled())
            baseDamage += baseDamage / 2f;

        if (mc.player.hasEffect(MobEffects.STRENGTH)) {
            int strength = Objects.requireNonNull(mc.player.getEffect(MobEffects.STRENGTH)).getAmplifier() + 1;
            baseDamage += 3 * strength;
        }

        // Reduce by armour
        baseDamage = CombatRules.getDamageAfterAbsorb(ent, baseDamage, mc.level.damageSources().generic(), ent.getArmorValue(), (float) ent.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue());
        return baseDamage;
    }

    public static SearchInvResult findBedInHotBar() {
        if (mc.player == null) return SearchInvResult.notFound();
        for (int b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack = mc.player.getInventory().getItem(b1);
            if (itemStack != null && itemStack.getItem() instanceof BedItem)
                return new SearchInvResult(b1, true, mc.player.getInventory().getItem(b1));
        }
        return SearchInvResult.notFound();
    }

    public static SearchInvResult findBed() {
        if (mc.player == null) return SearchInvResult.notFound();
        for (int b1 = 9; b1 < 45; b1++) {
            ItemStack itemStack = mc.player.getInventory().getItem(b1 >= 36 ? b1 - 36 : b1);
            if (itemStack != null && itemStack.getItem() instanceof BedItem)
                return new SearchInvResult(b1, true, mc.player.getInventory().getItem(b1));
        }
        return SearchInvResult.notFound();
    }

    public static Item getItem(String Name) {
        if (Name == null) return Items.AIR;
        for (Block block : BuiltInRegistries.BLOCK)
            if (block.getDescriptionId().replace("block.minecraft.", "").equals(Name.toLowerCase()))
                return Item.byBlock(block);
        for (Item item : BuiltInRegistries.ITEM)
            if (item.getDescriptionId().replace("item.minecraft.", "").equals(Name.toLowerCase()))
                return item;
        return Items.DIRT;
    }

    public static int getBedsCount() {
        if (mc.player == null) return 0;

        int counter = 0;

        for (int i = 0; i <= 44; ++i) {
            ItemStack itemStack = mc.player.getInventory().getItem(i);
            if (!(itemStack.getItem() instanceof BedItem)) continue;
            counter += itemStack.getCount();
        }

        return counter;
    }


    public interface Searcher {
        boolean isValid(ItemStack stack);
    }
}