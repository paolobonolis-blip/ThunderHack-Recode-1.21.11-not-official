package thunder.hack.features.modules.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.ThunderHack;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.EventHeldItemRenderer;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;

import java.awt.*;

public class Chams extends Module {
    public Chams() {
        super("Chams", Category.RENDER);
    }

    public final Setting<Boolean> handItems = new Setting<>("HandItems", false);
    private final Setting<ColorSetting> handItemsColor = new Setting<>("HandItemsColor", new ColorSetting(new Color(0x9317DE5D, true)), v -> handItems.getValue());

    public final Setting<Boolean> crystals = new Setting<>("Crystals", false);
    private final Setting<ColorSetting> crystalColor = new Setting<>("CrystalColor", new ColorSetting(new Color(0x932DD8E8, true)), v -> crystals.getValue());
    private final Setting<Boolean> staticCrystal = new Setting<>("StaticCrystal", true, v -> crystals.getValue());
    private final Setting<CMode> crystalMode = new Setting<>("CrystalMode", CMode.One, v -> crystals.getValue());

    public final Setting<Boolean> players = new Setting<>("Players", false);
    private final Setting<ColorSetting> playerColor = new Setting<>("PlayerColor", new ColorSetting(new Color(0x932DD8E8, true)), v -> players.getValue());
    private final Setting<ColorSetting> friendColor = new Setting<>("FriendColor", new ColorSetting(new Color(0x932DE830, true)), v -> players.getValue());
    private final Setting<Boolean> playerTexture = new Setting<>("PlayerTexture", true, v -> players.getValue());
    private final Setting<Boolean> simple = new Setting<>("Simple", false, v -> players.getValue());

    private final Setting<Boolean> alternativeBlending = new Setting<>("AlternativeBlending", true);

    private enum CMode {
        One, Two, Three
    }

    private final Identifier crystalTexture = Identifier.parse("textures/entity/end_crystal/end_crystal.png");
    private static final float SINE_45_DEGREES = (float) Math.sin(0.7853981633974483);

    public void renderCrystal(EndCrystal endCrystalEntity, float f, float g, PoseStack matrixStack, int i, ModelPart core, ModelPart frame) {
        BufferBuilder buffer;

        if (crystalMode.getValue() != CMode.One) {
            if (crystalMode.getValue() == CMode.Three) {
            } else {
            }
            buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        } else {
            buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        }

        matrixStack.pushPose();
        float h = staticCrystal.getValue() ? -1.4f : EndCrystalRenderer.getY((float) endCrystalEntity.time + g);
        float j = ((float) endCrystalEntity.time + g) * 3.0f;
        matrixStack.pushPose();
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        matrixStack.translate(0.0f, -0.5f, 0.0f);
        int k = OverlayTexture.NO_OVERLAY;
        matrixStack.mulPose(Axis.YP.rotationDegrees(j));
        matrixStack.translate(0.0f, 1.5f + h / 2.0f, 0.0f);
        matrixStack.mulPose(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        frame.render(matrixStack, buffer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.mulPose(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        matrixStack.mulPose(Axis.YP.rotationDegrees(j));
        frame.render(matrixStack, buffer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.mulPose(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        matrixStack.mulPose(Axis.YP.rotationDegrees(j));
        core.render(matrixStack, buffer, i, k);
        matrixStack.popPose();
        matrixStack.popPose();
        Render2DEngine.endBuilding(buffer);
    }

    public void renderPlayer(Player pe, float f, float g, PoseStack matrixStack, int i, EntityModel model, CallbackInfo ci, Runnable post) {
        BufferBuilder buffer;

        if (!simple.getValue()) {
            buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        } else {
            buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        }

        float n;
        Direction direction;
        Entity entity;
        matrixStack.pushPose();

        if (Managers.FRIEND.isFriend(pe)) {
        } else {
        }

        float h = Mth.rotLerp(g, pe.yBodyRotO, pe.yBodyRot);
        float j = Mth.rotLerp(g, pe.yHeadRotO, pe.yHeadRot);
        float k = j - h;
        if (pe.isPassenger() && pe.getVehicle() instanceof LivingEntity livingEntity2) {
            h = Mth.rotLerp(g, livingEntity2.yBodyRotO, livingEntity2.yBodyRot);
            k = j - h;
            float headClamp = Mth.wrapDegrees(k);
            if (headClamp < -85.0f) {
                headClamp = -85.0f;
            }
            if (headClamp >= 85.0f) {
                headClamp = 85.0f;
            }
            h = j - headClamp;
            if (headClamp * headClamp > 2500.0f) {
                h += headClamp * 0.2f;
            }
            k = j - h;
        }
        float m = Mth.lerp(g, pe.xRotO, pe.getXRot());
        if (pe.hasPose(Pose.SLEEPING) && pe.getBedOrientation() != null) {
            float sleepOffset = pe.getEyeHeight(Pose.STANDING) - 0.1f;
            matrixStack.translate((float) (-pe.getBedOrientation().getStepX()) * sleepOffset, 0.0f, (float) (-pe.getBedOrientation().getStepZ()) * sleepOffset);
        }
        float l = pe.tickCount + g;

        setupTransforms1(pe, matrixStack, l, h, g);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);

        matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);

        float walkDist = 0.0f;
        float walkSpeed = 0.0f;
        if (!pe.isPassenger() && pe.isAlive()) {
            walkSpeed = pe.walkAnimation.speed(g);
            walkDist = pe.walkAnimation.position(g);
            if (pe.isBaby())
                walkDist *= 3.0f;

            if (walkSpeed > 1.0f)
                walkSpeed = 1.0f;
        }

        LivingEntityRenderState state = new LivingEntityRenderState();
        state.ageInTicks = l;
        state.yRot = j;
        state.xRot = m;
        state.bodyRot = h;
        state.walkAnimationPos = walkDist;
        state.walkAnimationSpeed = walkSpeed;
        state.isBaby = pe.isBaby();
        state.isInWater = pe.isInWater();
        state.isAutoSpinAttack = pe.isAutoSpinAttack();
        state.bedOrientation = pe.getBedOrientation();
        state.pose = pe.getPose();
        state.eyeHeight = pe.getEyeHeight(pe.getPose());
        state.isUpsideDown = pe.isVisuallySwimming() && !pe.isInWater();

        model.setupAnim(state);
        int p = LivingEntityRenderer.getOverlayCoords(state, 0);
        model.renderToBuffer(matrixStack, buffer, i, p);
        Render2DEngine.endBuilding(buffer);
        matrixStack.popPose();
        if (!playerTexture.getValue()) {
            ci.cancel();
            post.run();
        }
    }

    public void setupTransforms1(Player abstractClientPlayerEntity, PoseStack matrixStack, float f, float g, float h) {
        float j = abstractClientPlayerEntity.getSwimAmount(h);
        float k = abstractClientPlayerEntity.getViewXRot(h);
        float l;
        float m;
        if (abstractClientPlayerEntity.isFallFlying()) {
            setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            l = (float) abstractClientPlayerEntity.getFallFlyingTicks() + h;
            m = Mth.clamp(l * l / 100.0F, 0.0F, 1.0F);
            if (!abstractClientPlayerEntity.isAutoSpinAttack()) {
                matrixStack.mulPose(Axis.XP.rotationDegrees(m * (-90.0F - k)));
            }

            Vec3 vec3d = abstractClientPlayerEntity.getViewVector(h);
            Vec3 vec3d2 = abstractClientPlayerEntity.getDeltaMovement();
            double d = vec3d2.horizontalDistanceSqr();
            double e = vec3d.horizontalDistanceSqr();
            if (d > 0.0 && e > 0.0) {
                double n = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                double o = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                matrixStack.mulPose(Axis.YP.rotation((float) (Math.signum(o) * Math.acos(n))));
            }
        } else if (j > 0.0F) {
            setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            l = abstractClientPlayerEntity.isInWater() ? -90.0F - k : -90.0F;
            m = Mth.lerp(j, 0.0F, l);
            matrixStack.mulPose(Axis.XP.rotationDegrees(m));
            if (abstractClientPlayerEntity.isVisuallySwimming()) {
                matrixStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
        }
    }

    private void setupTransforms(Player entity, PoseStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        if (!entity.hasPose(Pose.SLEEPING)) {
            matrices.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        }

        if (entity.deathTime > 0) {
            float f = ((float) entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrices.mulPose(Axis.ZP.rotationDegrees(f * 90.0F));
        } else if (entity.isAutoSpinAttack()) {
            matrices.mulPose(Axis.XP.rotationDegrees(-90.0F - entity.getXRot()));
            matrices.mulPose(Axis.YP.rotationDegrees(((float) entity.tickCount + tickDelta) * -75.0F));
        } else if (entity.hasPose(Pose.SLEEPING)) {
            Direction direction = entity.getBedOrientation();
            float g = direction != null ? getYaw(direction) : bodyYaw;
            matrices.mulPose(Axis.YP.rotationDegrees(g));
            matrices.mulPose(Axis.ZP.rotationDegrees(90.0F));
            matrices.mulPose(Axis.YP.rotationDegrees(270.0F));
        }
    }

    private static float getYaw(Direction direction) {
        return switch (direction) {
            case NORTH -> 270.0f;
            case SOUTH -> 90.0f;
            case EAST -> 180.0f;
            default -> 0.0f;
        };
    }

    @EventHandler
    public void onRenderHands(EventHeldItemRenderer e) {
        if (handItems.getValue()) {
            // hand item tinting removed for 26.1 port
        }
    }
}