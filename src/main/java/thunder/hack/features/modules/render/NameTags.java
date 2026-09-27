package thunder.hack.features.modules.render;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL11;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.player.FriendManager;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.features.hud.impl.PotionHud;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.features.modules.misc.NameProtect;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.TextureStorage;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;

public class NameTags extends Module {
    private final Map<ResourceKey<Enchantment>, String> encMap = new HashMap<>();

    public NameTags() {
        super("NameTags", Category.RENDER);
        encMap.put(Enchantments.BLAST_PROTECTION, "B");
        encMap.put(Enchantments.PROTECTION, "P");
        encMap.put(Enchantments.SHARPNESS, "S");
        encMap.put(Enchantments.EFFICIENCY, "E");
        encMap.put(Enchantments.UNBREAKING, "U");
        encMap.put(Enchantments.POWER, "PO");
        encMap.put(Enchantments.THORNS, "T");
    }

    private final Setting<Boolean> self = new Setting<>("Self", false);
    private final Setting<Float> scale = new Setting<>("Scale", 1f, 0.1f, 10f);
    private final Setting<Boolean> resize = new Setting<>("Resize", false);
    private final Setting<Float> height = new Setting<>("Height", 2f, 0.1f, 10f);
    private final Setting<Boolean> gamemode = new Setting<>("Gamemode", false);
    private final Setting<Boolean> spawners = new Setting<>("SpawnerNameTag", false);
    private final Setting<Boolean> entityOwner = new Setting<>("EntityOwner", false);
    private final Setting<Boolean> ping = new Setting<>("Ping", false);
    private final Setting<Boolean> hp = new Setting<>("HP", true);
    private final Setting<Boolean> distance = new Setting<>("Distance", true);
    private final Setting<Boolean> pops = new Setting<>("TotemPops", true);
    private final Setting<OutlineColor> outline = new Setting<>("OutlineType", OutlineColor.New);
    private final Setting<OutlineColor> friendOutline = new Setting<>("FriendOutline", OutlineColor.None);
    private final Setting<ColorSetting> outlineColor = new Setting<>("OutlineColor", new ColorSetting(0x80000000));
    private final Setting<ColorSetting> friendOutlineColor = new Setting<>("FriendOutlineColor", new ColorSetting(0x80000000));
    private final Setting<Boolean> enchantss = new Setting<>("Enchants", true);
    private final Setting<Boolean> onlyHands = new Setting<>("OnlyHands", false, v -> enchantss.getValue());
    private final Setting<Boolean> funtimeHp = new Setting<>("FunTimeHp", false);
    private final Setting<Boolean> ignoreBots = new Setting<>("IgnoreBots", false);
    private final Setting<Boolean> potions = new Setting<>("Potions", true);
    private final Setting<Boolean> shulkers = new Setting<>("Shulkers", true);
    private final Setting<ColorSetting> fillColorA = new Setting<>("Fill", new ColorSetting(0x80000000));
    private final Setting<ColorSetting> fillColorF = new Setting<>("FriendFill", new ColorSetting(0x80000000));
    private final Setting<Font> font = new Setting<>("FontMode", Font.Fancy);
    private final Setting<Armor> armorMode = new Setting<>("ArmorMode", Armor.Full);
    private final Setting<Health> health = new Setting<>("Health", Health.Number);


    public void onRender2D(GuiGraphics context) {
        if (mc.options.hideGui) return;
        for (Player ent : mc.level.players()) {
            if (ent == mc.player && (mc.options.getCameraType().isFirstPerson() || !self.getValue())) continue;
            if (getEntityPing(ent) <= 0 && ignoreBots.getValue()) continue;

            double x = ent.xOld + (ent.getX() - ent.xOld) * Render3DEngine.getTickDelta();
            double y = ent.yOld + (ent.getY() - ent.yOld) * Render3DEngine.getTickDelta();
            double z = ent.zOld + (ent.getZ() - ent.zOld) * Render3DEngine.getTickDelta();
            float scale = resize.getValue() ? this.scale.getValue() / mc.player.distanceTo(ent) : this.scale.getValue();
            Vec3 vector = new Vec3(x, y + height.getValue(), z);

            Vector4d position = null;

            vector = Render3DEngine.worldSpaceToScreenSpace(vector);
            if (vector.z > 0 && vector.z < 1) {
                position = new Vector4d(vector.x, vector.y, vector.z, 0);
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
            }

            String final_string = "";

            if (ping.getValue()) final_string += getPingColor(getEntityPing(ent)) + getEntityPing(ent) + "ms " + ChatFormatting.WHITE;
            if (gamemode.getValue()) final_string += translateGamemode(getEntityGamemode(ent)) + " ";

            if (FriendManager.friends.stream().anyMatch(i -> i.contains(ent.getDisplayName().getString())) && NameProtect.hideFriends.getValue() && ModuleManager.nameProtect.isEnabled()) {
                final_string += NameProtect.getCustomName() + " ";
            } else {
                final_string += ent.getDisplayName().getString() + " ";
            }

            if (hp.getValue() && health.is(Health.Number)) {
                final_string += getHealthColor(getHealth(ent)) + round2(getHealth(ent)) + " ";
            }

            if (distance.getValue()) final_string += String.format("%.1f", mc.player.distanceTo(ent)) + "m ";
            if (pops.getValue() && Managers.COMBAT.getPops(ent) != 0)
                final_string += (ChatFormatting.RESET + "" + Managers.COMBAT.getPops(ent));

            if (position != null) {
                double posX = position.x;
                double posY = position.y;
                double endPosX = position.z;
                double maxEnchantY = 0;

                float diff = (float) (endPosX - posX) / 2;
                float textWidth;

                if (font.getValue() == Font.Fancy) textWidth = (FontRenderers.sf_bold.getStringWidth(final_string) * 1);
                else textWidth = mc.font.width(final_string);

                float tagX = (float) ((posX + diff - textWidth / 2) * 1);

                ArrayList<ItemStack> stacks = new ArrayList<>();

                if (armorMode.getValue() != Armor.Durability) stacks.add(ent.getOffhandItem());

                stacks.add(ent.getItemBySlot(EquipmentSlot.FEET));
                stacks.add(ent.getItemBySlot(EquipmentSlot.LEGS));
                stacks.add(ent.getItemBySlot(EquipmentSlot.CHEST));
                stacks.add(ent.getItemBySlot(EquipmentSlot.HEAD));

                if (armorMode.getValue() != Armor.Durability) stacks.add(ent.getMainHandItem());

                context.pose().pushPose();
                context.pose().translate(tagX - 2 + (textWidth + 4) / 2f, (float) (posY - 13f) + 6.5f, 0);
                context.pose().scale(scale, scale, 1f);
                context.pose().translate(-(tagX - 2 + (textWidth + 4) / 2f), -(float) ((posY - 13f) + 6.5f), 0);

                float item_offset = 0;
                if (armorMode.getValue() != Armor.None) for (ItemStack armorComponent : stacks) {
                    if (!armorComponent.isEmpty()) {
                        if (armorMode.getValue() == Armor.Full) {
                            context.pose().pushPose();
                            context.pose().translate(posX - 55 + item_offset, (float) (posY - 33f), 0);
                            context.pose().scale(1.1f, 1.1f, 1.1f);
                            context.renderItem(armorComponent, 0, 0);
                            context.drawItemInSlot(mc.font, armorComponent, 0, 0);
                            context.pose().popPose();
                        } else {
                            context.pose().pushPose();
                            context.pose().translate(posX - 35 + item_offset, (float) (posY - 20), 0);
                            context.pose().scale(0.7f, 0.7f, 0.7f);

                            float durability = armorComponent.getMaxDamage() - armorComponent.getDamageValue();
                            int percent = (int) ((durability / (float) armorComponent.getMaxDamage()) * 100F);

                            Color color;
                            if (percent < 33) {
                                color = Color.RED;
                            } else if (percent > 33 && percent < 66) {
                                color = Color.YELLOW;
                            } else {
                                color = Color.GREEN;
                            }
                            context.drawString(mc.font, percent + "%", 0, 0, color.getRGB(), false);
                            context.pose().popPose();
                        }

                        float enchantmentY = 0;

                        ItemEnchantments enchants = EnchantmentHelper.getEnchantmentsForCrafting(armorComponent);


                        if (enchantss.getValue()) {
                            if (!onlyHands.getValue() || (armorComponent == ent.getOffhandItem() || armorComponent == ent.getMainHandItem())) {
                                for (ResourceKey<Enchantment> enchantment : encMap.keySet()) {
                                    if (enchants.keySet().contains(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(enchantment))) {
                                        String id = encMap.get(enchantment);
                                        int level = enchants.getLevel(mc.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(enchantment));
                                        String encName = id + level;

                                        if (font.getValue() == Font.Fancy) {
                                            FontRenderers.sf_bold.drawString(context.pose(), encName, posX - 50 + item_offset, (float) posY - 45 + enchantmentY, -1);
                                        } else {
                                            context.pose().pushPose();
                                            context.pose().translate((posX - 50f + item_offset), (posY - 45f + enchantmentY), 0);
                                            context.drawString(mc.font, encName, 0, 0, -1, false);
                                            context.pose().popPose();
                                        }
                                        enchantmentY -= 8;
                                        if (maxEnchantY > enchantmentY)
                                            maxEnchantY = enchantmentY;
                                    }
                                }
                            }
                        }
                    }
                    item_offset += 18f;
                }

                Color color = Managers.FRIEND.isFriend(ent) ? fillColorF.getValue().getColorObject() : fillColorA.getValue().getColorObject();

                OutlineColor cl = Managers.FRIEND.isFriend(ent) ? friendOutline.getValue() : outline.getValue();

                if (cl == OutlineColor.New)
                    Render2DEngine.drawRectWithOutline(context.pose(), tagX - 2, (float) (posY - 13f), textWidth + 4, 11, color, outlineColor.getValue().getColorObject());
                else
                    Render2DEngine.drawRect(context.pose(), tagX - 2, (float) (posY - 13f), textWidth + 4, 11, color);

                if (Managers.TELEMETRY.getOnlinePlayers().contains(ent.getGameProfile().name())) {
                    Render2DEngine.drawRect(context.pose(), tagX - 14, (float) (posY - 13f), 12, 11, color.brighter().brighter());
                    Color lColor = HudEditor.getColor(0);
                    Render2DEngine.renderTexture(context.pose(), tagX - 13, (float) (posY - 12.5f), 10, 10, 0, 0, 256, 256, 256, 256);
                }

                switch (cl) {
                    case None, New -> {

                    }
                    case Sync -> {
                        Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 14f), textWidth + 6, 1, HudEditor.getColor(270));
                        Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 3f), textWidth + 6, 1, HudEditor.getColor(0));
                        Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 14f), 1, 11, HudEditor.getColor(180));
                        Render2DEngine.drawRect(context.pose(), tagX + textWidth + 2, (float) (posY - 14f), 1, 11, HudEditor.getColor(90));
                    }
                    case Custom -> {
                        Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 14f), textWidth + 6, 1, Managers.FRIEND.isFriend(ent) ? friendOutlineColor.getValue().getColorObject() : outlineColor.getValue().getColorObject());
                        Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 3f), textWidth + 6, 1, Managers.FRIEND.isFriend(ent) ? friendOutlineColor.getValue().getColorObject() : outlineColor.getValue().getColorObject());
                        Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 14f), 1, 11, Managers.FRIEND.isFriend(ent) ? friendOutlineColor.getValue().getColorObject() : outlineColor.getValue().getColorObject());
                        Render2DEngine.drawRect(context.pose(), tagX + textWidth + 2, (float) (posY - 14f), 1, 11, Managers.FRIEND.isFriend(ent) ? friendOutlineColor.getValue().getColorObject() : outlineColor.getValue().getColorObject());
                    }
                }


                if (font.getValue() == Font.Fancy) {
                    FontRenderers.sf_bold.drawString(context.pose(), final_string, tagX, (float) posY - 10, -1);
                } else {
                    context.pose().pushPose();
                    context.pose().translate(tagX, ((float) posY - 11), 0);
                    context.drawString(mc.font, final_string, 0, 0, -1, false);
                    context.pose().popPose();
                }

                if (!health.is(Health.Number)) {
                    int i = Mth.ceil(ent.getHealth());
                    float f = (float) ent.getAttributeValue(Attributes.MAX_HEALTH);
                    int p = Mth.ceil(ent.getAbsorptionAmount());
                    context.pose().pushPose();
                    context.pose().translate(posX - 44, posY, 0);
                    context.pose().scale(1.1f, 1.1f, 1f);
                    renderHealthBar(context, ent, f, i, p);
                    context.pose().popPose();
                }

                if (potions.getValue())
                    renderStatusEffectOverlay(context, (float) posX, (float) (posY + maxEnchantY - 60), ent);

                Item handItem = ent.getMainHandItem().getItem();

                if (shulkers.getValue())
                    renderShulkerToolTip(context, (int) posX - 90, (int) posY - 120, (handItem instanceof BlockItem bi) && (bi.getBlock() instanceof ShulkerBoxBlock) ? ent.getMainHandItem() : ent.getOffhandItem());
                context.pose().popPose();
            }
        }

        if (spawners.getValue()) drawSpawnerNameTag(context);
        if (entityOwner.getValue()) drawEntityOwner(context);
    }

    private void drawSpawnerNameTag(GuiGraphics context) {
        for (BlockEntity blockEntity : StorageEsp.getBlockEntities()) {
            if (blockEntity instanceof SpawnerBlockEntity spawner) {
                Vec3 vector = new Vec3(spawner.getBlockPos().getX() + 0.5, spawner.getBlockPos().getY() + 1.5, spawner.getBlockPos().getZ() + 0.5);
                Vector4d position = null;
                vector = Render3DEngine.worldSpaceToScreenSpace(new Vec3(vector.x, vector.y, vector.z));
                if (vector.z > 0 && vector.z < 1) {
                    position = new Vector4d(vector.x, vector.y, vector.z, 0);
                    position.x = Math.min(vector.x, position.x);
                    position.y = Math.min(vector.y, position.y);
                    position.z = Math.max(vector.x, position.z);
                }
                if (spawner.getSpawner() == null || spawner.getSpawner().getOrCreateDisplayEntity(mc.level, spawner.getBlockPos()) == null)
                    continue;
                String final_string = spawner.getSpawner().getOrCreateDisplayEntity(mc.level, spawner.getBlockPos()).getName().getString() + " " + String.format("%.1f", ((float) spawner.getSpawner().spawnDelay / 20f)) + "s";

                if (spawner.getSpawner().getSpin() == spawner.getSpawner().getOSpin() && spawner.getSpawner().getSpin() == 0f && (float) spawner.getSpawner().spawnDelay / 20f == 1f)
                    final_string = spawner.getSpawner().getOrCreateDisplayEntity(mc.level, spawner.getBlockPos()).getName().getString() + " loot!";


                if (position != null) {
                    double posX = position.x;
                    double posY = position.y;
                    double endPosX = position.z;

                    float diff = (float) (endPosX - posX) / 2;
                    float textWidth = (FontRenderers.sf_bold.getStringWidth(final_string) * 1);
                    float tagX = (float) ((posX + diff - textWidth / 2) * 1);

                    Render2DEngine.drawRect(context.pose(), tagX - 2, (float) (posY - 13f), textWidth + 4, 11, fillColorA.getValue().getColorObject());

                    switch (outline.getValue()) {
                        case None -> {

                        }
                        case Sync -> {
                            Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 14f), textWidth + 6, 1, HudEditor.getColor(270));
                            Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 3f), textWidth + 6, 1, HudEditor.getColor(0));
                            Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 14f), 1, 11, HudEditor.getColor(180));
                            Render2DEngine.drawRect(context.pose(), tagX + textWidth + 2, (float) (posY - 14f), 1, 11, HudEditor.getColor(90));
                        }
                        case Custom -> {
                            Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 14f), textWidth + 6, 1, outlineColor.getValue().getColorObject());
                            Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 3f), textWidth + 6, 1, outlineColor.getValue().getColorObject());
                            Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 14f), 1, 11, outlineColor.getValue().getColorObject());
                            Render2DEngine.drawRect(context.pose(), tagX + textWidth + 2, (float) (posY - 14f), 1, 11, outlineColor.getValue().getColorObject());
                        }
                    }


                    FontRenderers.sf_bold.drawString(context.pose(), final_string, tagX, (float) posY - 10, -1);
                }
            }
        }
    }

    public void drawEntityOwner(GuiGraphics context) {
        for (Entity ent : mc.level.entitiesForRendering()) {
            String ownerName = "";
            if (ent instanceof Projectile pe) {
                if (pe.getOwner() != null) ownerName = pe.getOwner().getDisplayName().getString();
            } else if (ent instanceof Horse he) {
                if (he.getOwnerReference().getUUID() != null) ownerName = he.getOwnerReference().getUUID().toString();
            } else if (ent instanceof TamableAnimal te && te.isTame() && te.getOwner() != null) {
                ownerName = te.getOwner().getDisplayName().getString();
            } else continue;

            String final_string = "Owned by " + ownerName;
            double x = ent.xOld + (ent.getX() - ent.xOld) * Render3DEngine.getTickDelta();
            double y = ent.yOld + (ent.getY() - ent.yOld) * Render3DEngine.getTickDelta();
            double z = ent.zOld + (ent.getZ() - ent.zOld) * Render3DEngine.getTickDelta();
            Vec3 vector = new Vec3(x, y + 2, z);
            Vector4d position = null;
            vector = Render3DEngine.worldSpaceToScreenSpace(new Vec3(vector.x, vector.y, vector.z));
            if (vector.z > 0 && vector.z < 1) {
                position = new Vector4d(vector.x, vector.y, vector.z, 0);
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
            }

            if (position != null) {
                double posX = position.x;
                double posY = position.y;
                double endPosX = position.z;

                float diff = (float) (endPosX - posX) / 2;
                float textWidth = (FontRenderers.sf_bold.getStringWidth(final_string) * 1);
                float tagX = (float) ((posX + diff - textWidth / 2) * 1);

                Render2DEngine.drawRect(context.pose(), tagX - 2, (float) (posY - 13f), textWidth + 4, 11, fillColorA.getValue().getColorObject());

                switch (outline.getValue()) {
                    case None -> {

                    }
                    case Sync -> {
                        Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 14f), textWidth + 6, 1, HudEditor.getColor(270));
                        Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 3f), textWidth + 6, 1, HudEditor.getColor(0));
                        Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 14f), 1, 11, HudEditor.getColor(180));
                        Render2DEngine.drawRect(context.pose(), tagX + textWidth + 2, (float) (posY - 14f), 1, 11, HudEditor.getColor(90));
                    }
                    case Custom -> {
                        Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 14f), textWidth + 6, 1, outlineColor.getValue().getColorObject());
                        Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 3f), textWidth + 6, 1, outlineColor.getValue().getColorObject());
                        Render2DEngine.drawRect(context.pose(), tagX - 3, (float) (posY - 14f), 1, 11, outlineColor.getValue().getColorObject());
                        Render2DEngine.drawRect(context.pose(), tagX + textWidth + 2, (float) (posY - 14f), 1, 11, outlineColor.getValue().getColorObject());
                    }
                }
                FontRenderers.sf_bold.drawString(context.pose(), final_string, tagX, (float) posY - 10, -1);
            }
        }
    }

    public static int getEntityPing(Player entity) {
        if (mc.getConnection() == null) return 0;
        PlayerInfo playerListEntry = mc.getConnection().getPlayerInfo(entity.getUUID());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }

    public static GameType getEntityGamemode(Player entity) {
        if (entity == null) return null;
        PlayerInfo playerListEntry = mc.getConnection().getPlayerInfo(entity.getUUID());
        return playerListEntry == null ? null : playerListEntry.getGameMode();
    }

    private String translateGamemode(GameType gamemode) {
        if (gamemode == null) return "[BOT]";
        return switch (gamemode) {
            case SURVIVAL -> "[S]";
            case CREATIVE -> "[C]";
            case SPECTATOR -> "[SP]";
            case ADVENTURE -> "[A]";
        };
    }

    public float getHealth(Player ent) {
        // Первый в комьюнити хп резольвер. Правда, еж?
        if ((mc.getConnection() != null && mc.getConnection().getServerData() != null && mc.getConnection().getServerData().ip.contains("funtime") || funtimeHp.getValue())) {
            Objective scoreBoard = null;
            String resolvedHp = "";
            if ((ent.level().getScoreboard()).getDisplayObjective(DisplaySlot.BELOW_NAME) != null) {
                scoreBoard = (ent.level().getScoreboard()).getDisplayObjective(DisplaySlot.BELOW_NAME);
                if (scoreBoard != null) {
                    ReadOnlyScoreInfo readableScoreboardScore = ent.level().getScoreboard().getPlayerScoreInfo(ent, scoreBoard);
                    MutableComponent text2 = ReadOnlyScoreInfo.safeFormatValue(readableScoreboardScore, scoreBoard.numberFormatOrDefault(StyledFormat.NO_STYLE));
                    resolvedHp = text2.getString();
                }
            }
            float numValue = 0;
            try {
                numValue = Float.parseFloat(resolvedHp);
            } catch (NumberFormatException ignored) {
            }
            return numValue;
        } else return ent.getHealth() + ent.getAbsorptionAmount();
    }

    private void renderHealthBar(GuiGraphics context, Player player, float maxHealth, int lastHealth, int absorption) {
        int i = Mth.ceil((double) maxHealth / 2.0);
        int j = Mth.ceil((double) absorption / 2.0);
        int k = i * 2;
        int cont = 0;

        for (int l = i + j - 1; l >= 0; --l) {
            int n = l % 10;
            int o = n * 8;

            if (cont < 10) {
                drawHeart(context, HeartType.CONTAINER, o, false, player);
                cont++;
            }

            int q = l * 2;
            if (q < lastHealth) {
                drawHeart(context, HeartType.NORMAL, o, q + 1 == lastHealth, player);
            }

            if (l >= i) {
                int r = q - k;
                if (q - k < absorption) {
                    context.pose().pushPose();
                    context.pose().translate(0, 0, 0.001f);
                    drawHeart(context, HeartType.ABSORBING, o, r + 1 == absorption, player);
                    context.pose().popPose();
                }
            }
        }

    }

    private void drawHeart(GuiGraphics context, HeartType type, int x, boolean half, Player player) {
        if (health.is(Health.Dots)) {

            Color color = Managers.FRIEND.isFriend(player) ? fillColorF.getValue().getColorObject() : fillColorA.getValue().getColorObject();
            if (type == HeartType.CONTAINER) {
                Render2DEngine.drawRect(context.pose(), x, 0, 7, 3, color);
            } else if (type == HeartType.NORMAL) {
                if (half) {
                    Render2DEngine.drawRect(context.pose(), x, 0, 3, 3, getHealthColor2(player.getHealth() + player.getAbsorptionAmount()));
                    Render2DEngine.drawRect(context.pose(), x + 3, 0, 4, 3, color);
                } else {
                    Render2DEngine.drawRect(context.pose(), x, 0, 7, 3, getHealthColor2(player.getHealth() + player.getAbsorptionAmount()));
                }
            }
        } else context.blitSprite(type.getTexture(half), x, 0, 9, 9);
    }

    private enum HeartType {
        CONTAINER(Identifier.parse("hud/heart/container"), Identifier.parse("hud/heart/container")), NORMAL(Identifier.parse("hud/heart/full"), Identifier.parse("hud/heart/half")), ABSORBING(Identifier.parse("hud/heart/absorbing_full"), Identifier.parse("hud/heart/absorbing_half"));

        private final Identifier fullTexture;
        private final Identifier halfTexture;

        HeartType(Identifier fullTexture, Identifier halfTexture) {
            this.fullTexture = fullTexture;
            this.halfTexture = halfTexture;
        }

        public Identifier getTexture(boolean half) {
            return half ? halfTexture : fullTexture;
        }
    }

    public @NotNull String getHealthColor(float health) {
        if (health <= 15 && health > 7) return ChatFormatting.YELLOW + "";
        if (health > 15) return ChatFormatting.GREEN + "";
        return ChatFormatting.RED + "";
    }

    public @NotNull String getPingColor(int ping) {
        if (ping <= 60) return ChatFormatting.GREEN + "";
        if (ping > 60 && ping < 120) return ChatFormatting.YELLOW + "";
        return ChatFormatting.RED + "";
    }

    private @NotNull Color getHealthColor2(float health) {
        if (health <= 15 && health > 7) return Color.YELLOW;
        if (health > 15) return Color.GREEN;
        return Color.RED;
    }

    public static float round2(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) return 1f;
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    private void renderStatusEffectOverlay(GuiGraphics context, float x, float y, Player player) {
        ArrayList<MobEffectInstance> effects = new ArrayList<>(player.getActiveEffects());
        if (effects.isEmpty()) return;
        x += effects.size() * 12.5f;
        for (MobEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(effects)) {
            x -= 25;
            String power = "";
            switch (statusEffectInstance.getAmplifier()) {
                case 0 -> power = "I";
                case 1 -> power = "II";
                case 2 -> power = "III";
                case 3 -> power = "IV";
                case 4 -> power = "V";
            }

            context.pose().pushPose();
            context.pose().translate(x, y, 0);
            FontRenderers.sf_bold_mini.drawCenteredString(context.pose(), PotionHud.getDuration(statusEffectInstance), 9, -8, -1);
            FontRenderers.categories.drawCenteredString(context.pose(), power, 9, -16, -1);
            context.pose().popPose();

        }
    }

    public boolean renderShulkerToolTip(GuiGraphics context, int offsetX, int offsetY, ItemStack stack) {
        try {
            ItemContainerContents compoundTag = stack.get(DataComponents.CONTAINER);
            if (compoundTag == null) return false;

            float[] colors = new float[]{1F, 1F, 1F};
            Item focusedItem = stack.getItem();
            if (focusedItem instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock sbb) {
                try {
                    int argb = sbb.getColor().getTextColor();
                    Color c = new Color(argb, true);
                    colors = new float[]{c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f};
                } catch (NullPointerException npe) {
                    colors = new float[]{1F, 1F, 1F};
                }
            } else {
                return false;
            }
            draw(context, compoundTag.nonEmptyItemCopyStream().toList(), offsetX, offsetY, colors);
        } catch (Exception ignore) {
            return false;
        }
        return true;
    }

    private void draw(GuiGraphics context, List<ItemStack> itemStacks, int offsetX, int offsetY, float[] colors) {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        offsetX += 8;
        offsetY -= 82;

        drawBackground(context, offsetX, offsetY, colors);

        int row = 0;
        int i = 0;
        for (ItemStack itemStack : itemStacks) {
            context.renderItem(itemStack, offsetX + 8 + i * 18, offsetY + 7 + row * 18);
            context.drawItemInSlot(mc.font, itemStack, offsetX + 8 + i * 18, offsetY + 7 + row * 18);
            i++;
            if (i >= 9) {
                i = 0;
                row++;
            }
        }
    }

    private void drawBackground(GuiGraphics context, int x, int y, float[] colors) {
        context.blit(TextureStorage.container, x, y, 0, 0, 176, 67, 176, 67);
    }

    public enum Font {
        Fancy, Fast
    }

    public enum Armor {
        None, Full, Durability
    }

    public enum Health {
        Number, Hearts, Dots
    }

    private enum OutlineColor {
        Sync, Custom, None, New
    }
}