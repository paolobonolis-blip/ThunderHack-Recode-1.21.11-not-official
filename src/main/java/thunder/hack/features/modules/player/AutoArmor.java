package thunder.hack.features.modules.player;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.hud.HudEditorGui;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.player.InventoryUtility;
import thunder.hack.utility.player.MovementUtility;

import java.util.Arrays;
import java.util.List;

public class AutoArmor extends Module {
    public AutoArmor() {
        super("AutoArmor", Category.PLAYER);
    }

    private final Setting<EnchantPriority> head = new Setting<>("Head", EnchantPriority.Protection);
    private final Setting<EnchantPriority> body = new Setting<>("Body", EnchantPriority.Protection);
    private final Setting<EnchantPriority> tights = new Setting<>("Tights", EnchantPriority.Protection);
    private final Setting<EnchantPriority> feet = new Setting<>("Feet", EnchantPriority.Protection);
    private final Setting<ElytraPriority> elytraPriority = new Setting<>("ElytraPriority", ElytraPriority.Ignore);
    private final Setting<Integer> delay = new Setting<>("Delay", 5, 0, 10);
    private final Setting<Boolean> oldVersion = new Setting<>("OldVersion", false);
    private final Setting<Boolean> pauseInventory = new Setting<>("PauseInventory", false);
    private final Setting<Boolean> noMove = new Setting<>("NoMove", false);
    private final Setting<Boolean> ignoreCurse = new Setting<>("IgnoreCurse", true);
    private final Setting<Boolean> strict = new Setting<>("Strict", false);

    private int tickDelay = 0;

    List<ArmorData> armorList = Arrays.asList(
            new ArmorData(EquipmentSlot.FEET, 36, -1, -1, -1),
            new ArmorData(EquipmentSlot.LEGS, 37, -1, -1, -1),
            new ArmorData(EquipmentSlot.CHEST, 38, -1, -1, -1),
            new ArmorData(EquipmentSlot.HEAD, 39, -1, -1, -1)
    );

    @Override
    public void onUpdate() {
        if (mc.screen != null && pauseInventory.getValue() && !(mc.screen instanceof ChatScreen) && !(mc.screen instanceof ClickGUI) && !(mc.screen instanceof HudEditorGui))
            return;

        if (tickDelay-- > 0)
            return;

        armorList.forEach(ArmorData::reset);

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            int prot = getProtection(stack);
            if (prot > 0)
                for (ArmorData e : armorList) {
                    EquipmentSlot stackSlot = getEquipmentSlot(stack);
                    if (e.getEquipmentSlot() == stackSlot)
                        if (prot > e.getPrevProt() && prot > e.getNewProtection()) {
                            e.setNewSlot(i);
                            e.setNewProtection(prot);
                        }
                }
        }

        for (ArmorData armorPiece : armorList) {
            int slot = armorPiece.getNewSlot();
            if (slot != -1) {
                if ((armorPiece.getPrevProt() == -1 || !oldVersion.getValue()) && slot < 9) {
                    InventoryUtility.saveAndSwitchTo(slot);
                    sendSequencedPacket(id -> new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, id, mc.player.getYRot(), mc.player.getXRot()));
                    InventoryUtility.returnSlot();
                } else {
                    if (MovementUtility.isMoving() && noMove.getValue())
                        return;

                    int newArmorSlot = slot < 9 ? 36 + slot : slot;

                    if(strict.getValue())
                        sendPacket(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.STOP_SPRINTING));

                    clickSlot(newArmorSlot);
                    clickSlot((armorPiece.getArmorSlot() - 34) + (39 - armorPiece.getArmorSlot()) * 2);
                    if (armorPiece.getPrevProt() != -1)
                        clickSlot(newArmorSlot);

                    sendPacket(new ServerboundContainerClosePacket(mc.player.containerMenu.containerId));
                }

                tickDelay = delay.getValue();
                return;
            }
        }
    }

    private EquipmentSlot getEquipmentSlot(ItemStack is) {
        Equippable equippable = is.get(DataComponents.EQUIPPABLE);
        if (equippable != null && equippable.slot() != null)
            return equippable.slot();
        return EquipmentSlot.CHEST;
    }

    private int getProtection(ItemStack is) {
        EquipmentSlot slot = getEquipmentSlot(is);
        boolean isArmor = slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
        boolean isElytra = is.has(DataComponents.GLIDER);
        if (isArmor || isElytra) {
            int prot = 0;

            if (isElytra) {
                if (!(is.has(DataComponents.GLIDER) && !is.nextDamageWillBreak()))
                    return 0;

                boolean ePlus = elytraPriority.is(ElytraPriority.ElytraPlus) && (ModuleManager.elytraRecast.isEnabled() || ModuleManager.elytraPlus.isEnabled());
                boolean ignore = elytraPriority.is(ElytraPriority.Ignore) && getEquipmentSlot(mc.player.getInventory().getItem(38)) == EquipmentSlot.CHEST && mc.player.getInventory().getItem(38).has(DataComponents.GLIDER);

                if (ePlus || ignore || elytraPriority.is(ElytraPriority.Always))
                    prot = 999;
            }

            int blastMultiplier = 1;
            int protectionMultiplier = 1;

            switch (slot) {
                case HEAD -> {
                    if(head.is(EnchantPriority.Protection)) protectionMultiplier *= 2;
                    else blastMultiplier *= 2;
                }
                case BODY -> {
                    if(body.is(EnchantPriority.Protection)) protectionMultiplier *= 2;
                    else blastMultiplier *= 2;
                }
                case LEGS -> {
                    if(tights.is(EnchantPriority.Protection)) protectionMultiplier *= 2;
                    else blastMultiplier *= 2;
                }
                case FEET -> {
                    if(feet.is(EnchantPriority.Protection)) protectionMultiplier *= 2;
                    else blastMultiplier *= 2;
                }
            }

            if (is.isEnchanted()) {
                ItemEnchantments enchants = EnchantmentHelper.getEnchantmentsForCrafting(is);

                //mc.world.getRegistryManager().get(Enchantments.BLAST_PROTECTION.getRegistryRef()).getEntry(Enchantments.BLAST_PROTECTION).get()
                if (enchants.keySet().contains(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.PROTECTION)))
                    prot += enchants.getLevel(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.PROTECTION)) * protectionMultiplier;

                if (enchants.keySet().contains(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.BLAST_PROTECTION)))
                    prot += enchants.getLevel(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.BLAST_PROTECTION)) * blastMultiplier;

                if (enchants.keySet().contains(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.BINDING_CURSE)) && ignoreCurse.getValue())
                    prot = -999;
            }

            return (isArmor ? getArmorRating(is, slot) : 0) + prot;
        } else if (!is.isEmpty()) return 0;
        return -1;
    }

    private int getArmorRating(ItemStack is, EquipmentSlot slot) {
        net.minecraft.world.item.component.ItemAttributeModifiers mods =
                is.getOrDefault(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS, net.minecraft.world.item.component.ItemAttributeModifiers.EMPTY);
        double prot = mods.compute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, 0.0D, slot);
        double tough = mods.compute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS, 0.0D, slot);
        return (int) ((prot + (int) Math.ceil(tough)) * 10);
    }

    public class ArmorData {
        private EquipmentSlot equipmentSlot;
        private int armorSlot, prevProtection, newSlot, newProtection;

        public ArmorData(EquipmentSlot equipmentSlot, int armorSlot, int prevProtection, int newSlot, int newProtection) {
            this.equipmentSlot = equipmentSlot;
            this.armorSlot = armorSlot;
            this.prevProtection = prevProtection;
            this.newSlot = newSlot;
            this.newProtection = newProtection;
        }

        public int getArmorSlot() {
            return armorSlot;
        }

        public int getPrevProt() {
            return prevProtection;
        }

        public void setPrevProt(int prevProtection) {
            this.prevProtection = prevProtection;
        }

        public int getNewSlot() {
            return newSlot;
        }

        public void setNewSlot(int newSlot) {
            this.newSlot = newSlot;
        }

        public int getNewProtection() {
            return newProtection;
        }

        public void setNewProtection(int newProtection) {
            this.newProtection = newProtection;
        }

        public EquipmentSlot getEquipmentSlot() {
            return equipmentSlot;
        }

        public void reset() {
            setPrevProt(getProtection(mc.player.getInventory().getItem(getArmorSlot())));
            setNewSlot(-1);
            setNewProtection(-1);
        }
    }

    private enum ElytraPriority {
        None, Always, ElytraPlus, Ignore
    }

    private enum EnchantPriority {
        Blast, Protection
    }
}