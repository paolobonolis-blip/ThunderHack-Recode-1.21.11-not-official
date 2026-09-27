package thunder.hack.features.modules.render;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import thunder.hack.events.impl.TotemPopEvent;
import thunder.hack.injection.accesors.IEntity;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.math.MathUtility;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

import java.awt.Color;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PopChams extends Module {
    public PopChams() {
        super("PopChams", Category.RENDER);
    }

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.Textured);
    private final Setting<Boolean> secondLayer = new Setting<>("SecondLayer", true);
    private final Setting<ColorSetting> color = new Setting<>("Color", new ColorSetting(0x8800FF00));
    private final Setting<Integer> ySpeed = new Setting<>("YSpeed", 0, -10, 10);
    private final Setting<Integer> aSpeed = new Setting<>("AlphaSpeed", 5, 1, 100);
    private final Setting<Float> rotSpeed = new Setting<>("RotationSpeed", 0.25f, 0f, 6f);

    private final CopyOnWriteArrayList<Person> popList = new CopyOnWriteArrayList<>();

    private enum Mode {
        Simple, Textured
    }

    @Override
    public void onUpdate() {
        popList.forEach(person -> person.update(popList));
    }

    @Override
    public void onRender3D(PoseStack stack) {
        popList.forEach(person -> renderEntity(stack, person.player, person.getAlpha()));
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void onTotemPop(@NotNull TotemPopEvent e) {
        if (e.getEntity().equals(mc.player) || mc.level == null) return;

        Player entity = new Player(mc.level, new GameProfile(e.getEntity().getUUID(), e.getEntity().getName().getString())) {
            @Override public boolean isSpectator() {return false;}
            @Override public boolean isCreative() {return false;}
            @Override public net.minecraft.world.level.GameType gameMode() {return net.minecraft.world.level.GameType.SURVIVAL;}
        };

        entity.copyPosition(e.getEntity());
        entity.yBodyRot = e.getEntity().yBodyRot;
        entity.yHeadRot = e.getEntity().yHeadRot;
        entity.attackAnim = e.getEntity().attackAnim;
        entity.swingTime = e.getEntity().swingTime;
        entity.setShiftKeyDown(e.getEntity().isShiftKeyDown());
        entity.walkAnimation.setSpeed(e.getEntity().walkAnimation.speed());
        popList.add(new Person(entity, ((AbstractClientPlayer) e.getEntity()).getSkin().body().texturePath()));
    }

    private void renderEntity(@NotNull PoseStack matrices, @NotNull LivingEntity entity, int alpha) {
        double x = entity.getX() - mc.getEntityRenderDispatcher().camera.position().x;
        double y = entity.getY() - mc.getEntityRenderDispatcher().camera.position().y;
        double z = entity.getZ() - mc.getEntityRenderDispatcher().camera.position().z;
        ((IEntity) entity).setPos(entity.position().add(0, (double) ySpeed.getValue() / 50., 0));

        matrices.pushPose();
        matrices.translate((float) x, (float) (y + entity.getBbHeight() / 2f), (float) z);

        Color boxColor = new Color(color.getValue().getRed(), color.getValue().getGreen(), color.getValue().getBlue(), MathUtility.clamp(alpha, 0, 255));
        Render3DEngine.drawFilledBox(matrices, new AABB(-0.4, -0.95, -0.4, 0.4, 0.95, 0.4), boxColor);
        matrices.popPose();
    }

    private class Person {
        private final Player player;
        private Identifier texture;
        private int alpha;

        public Person(Player player, Identifier texture) {
            this.player = player;
            alpha = color.getValue().getAlpha();
            this.texture = texture;
        }

        public void update(CopyOnWriteArrayList<Person> arrayList) {
            if (alpha <= 0) {
                arrayList.remove(this);
                player.discard();
                return;
            }
            alpha -= aSpeed.getValue();
        }

        public int getAlpha() {
            return MathUtility.clamp(alpha, 0, 255);
        }

        public Identifier getTexture() {
            return texture;
        }
    }
}
