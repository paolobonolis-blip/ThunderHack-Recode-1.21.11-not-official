package thunder.hack.injection;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import thunder.hack.core.Managers;
import thunder.hack.utility.render.shaders.satin.impl.ReloadableShaderEffectManager;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.core.*;
import net.minecraft.world.phys.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.features.modules.player.NoEntityTrace;
import thunder.hack.utility.math.FrameRateCounter;
import thunder.hack.utility.render.BlockAnimationUtility;
import thunder.hack.utility.render.Render3DEngine;

import static thunder.hack.features.modules.Module.mc;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow
    private float zoom;

    @Shadow
    private float zoomX;

    @Shadow
    private float zoomY;

    @Shadow
    private float viewDistance;

    @Shadow
    public abstract void tick();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 1, shift = At.Shift.BEFORE), method = "render")
    void postHudRenderHook(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
        FrameRateCounter.INSTANCE.recordFrame();
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderLevel")
    void render3dHook(DeltaTracker tickCounter, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;

        Camera camera = mc.gameRenderer.getMainCamera();
        PoseStack matrixStack = new PoseStack();
        matrixStack.mulPose(Axis.XP.rotationDegrees(camera.xRot()));
        matrixStack.mulPose(Axis.YP.rotationDegrees(camera.yRot() + 180.0f));

        Render3DEngine.lastProjMat.set(new Matrix4f());
        Render3DEngine.lastModMat.set(RenderSystem.getModelViewMatrix());
        Render3DEngine.lastWorldSpaceMatrix.set(matrixStack.last().pose());

        Managers.MODULE.onRender3D(matrixStack);
        BlockAnimationUtility.onRender(matrixStack);
        Render3DEngine.onRender3D(matrixStack); // <- не двигать

    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand(Lnet/minecraft/client/Camera;FLorg/joml/Matrix4f;)V", shift = At.Shift.AFTER))
    public void postRender3dHook(DeltaTracker tickCounter, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;
        Managers.SHADER.renderShaders();
    }

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F"))
    private float renderWorldHook(float delta, float first, float second) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.nausea.getValue()) return 0;
        return Mth.lerp(delta, first, second);
    }

    @Inject(method = "loadPrograms", at = @At(value = "RETURN"))
    private void loadSatinPrograms(ResourceProvider factory, CallbackInfo ci) {
        ReloadableShaderEffectManager.INSTANCE.reload(factory);
    }

    @Inject(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;findCrosshairTarget(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"), cancellable = true)
    private void onUpdateTargetedEntity(float tickDelta, CallbackInfo info) {
        if (Module.fullNullCheck()) return;

        /*
        if (ModuleManager.aura.isEnabled() && Aura.target != null && mc.player.distanceTo(Aura.target) <= ModuleManager.aura.attackRange.getValue() && ModuleManager.aura.rotationMode.getValue() != Aura.Mode.None) {
            mc.getProfiler().pop();
            info.cancel();
            //add vector from aura
            mc.crosshairTarget = new EntityHitResult(Aura.target);
        }
         */

        if (ModuleManager.freeCam.isEnabled()) {
            info.cancel();
            mc.hitResult = Managers.PLAYER.getRtxTarget(ModuleManager.freeCam.getFakeYaw(), ModuleManager.freeCam.getFakePitch(), ModuleManager.freeCam.getFakeX(), ModuleManager.freeCam.getFakeY(), ModuleManager.freeCam.getFakeZ());
        }
    }

    @Inject(method = "findCrosshairTarget", at = @At("HEAD"), cancellable = true)
    private void findCrosshairTargetHook(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta, CallbackInfoReturnable<HitResult> cir) {
        if (ModuleManager.noEntityTrace.isEnabled() && (mc.player.getMainHandItem().is(h -> h.is(ItemTags.PICKAXES)) || !NoEntityTrace.ponly.getValue())) {
            if (mc.player.getMainHandItem().is(h -> h.is(ItemTags.SWORDS)) && NoEntityTrace.noSword.getValue()) return;
            double d = Math.max(blockInteractionRange, entityInteractionRange);
            Vec3 vec3d = camera.getEyePosition(tickDelta);
            HitResult hitResult = camera.pick(d, tickDelta, false);
            cir.setReturnValue(ensureTargetInRangeCustom(hitResult, vec3d, blockInteractionRange));
        }
    }

    @Inject(method = "getProjectionMatrix", at = @At("TAIL"), cancellable = true)
    public void getBasicProjectionMatrixHook(double fov, CallbackInfoReturnable<Matrix4f> cir) {
        if (ModuleManager.aspectRatio.isEnabled()) {
            PoseStack matrixStack = new PoseStack();
            matrixStack.last().pose().identity();
            if (zoom != 1.0f) {
                matrixStack.translate(zoomX, -zoomY, 0.0f);
                matrixStack.scale(zoom, zoom, 1.0f);
            }
            matrixStack.last().pose().mul(new Matrix4f().setPerspective((float) (fov * 0.01745329238474369), ModuleManager.aspectRatio.ratio.getValue(), 0.05f, viewDistance * 4.0f));
            cir.setReturnValue(matrixStack.last().pose());
        }
    }

    @Inject(method = "getFov(Lnet/minecraft/client/Camera;FZ)D", at = @At("TAIL"), cancellable = true)
    public void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cb) {
        if (ModuleManager.fov.isEnabled()) {
            if (cb.getReturnValue() == 70 && !ModuleManager.fov.itemFov.getValue() && mc.options.getCameraType() != CameraType.FIRST_PERSON)
                return;

            else if (ModuleManager.fov.itemFov.getValue() && cb.getReturnValue() == 70) {
                cb.setReturnValue(ModuleManager.fov.itemFovModifier.getValue().doubleValue());
                return;
            }

            if (mc.player.isUnderWater())
                return;

            cb.setReturnValue(ModuleManager.fov.fovModifier.getValue().doubleValue());
        }
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void bobViewHook(PoseStack matrices, float tickDelta, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;
        if (ModuleManager.noBob.isEnabled()) {
            ModuleManager.noBob.bobView(matrices, tickDelta);
            ci.cancel();
            return;
        }
        if (ClientSettings.customBob.getValue()) {
            ThunderHack.core.bobView(matrices, tickDelta);
            ci.cancel();
        }
    }

    @Unique
    private HitResult ensureTargetInRangeCustom(HitResult hitResult, Vec3 cameraPos, double interactionRange) {
        Vec3 vec3d = hitResult.getLocation();
        if (!vec3d.closerThan(cameraPos, interactionRange)) {
            Vec3 vec3d2 = hitResult.getLocation();
            Direction direction = Direction.getApproximateNearest(vec3d2.x - cameraPos.x, vec3d2.y - cameraPos.y, vec3d2.z - cameraPos.z);
            return BlockHitResult.miss(vec3d2, direction, BlockPos.containing(vec3d2));
        } else {
            return hitResult;
        }
    }

    @Inject(method = "displayItemActivation", at = @At("HEAD"), cancellable = true)
    private void showFloatingItemHook(ItemStack floatingItem, CallbackInfo info) {
        if (ModuleManager.totemAnimation.isEnabled()) {
            ModuleManager.totemAnimation.showFloatingItem(floatingItem);
            info.cancel();
        }
    }

    @Inject(method = "renderFloatingItem", at = @At("HEAD"), cancellable = true)
    private void renderFloatingItemHook(GuiGraphics context, float tickDelta, CallbackInfo ci) {
        if (ModuleManager.totemAnimation.isEnabled()) {
            ModuleManager.totemAnimation.renderFloatingItem(context, tickDelta);
            ci.cancel();
        }
    }

    @Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
    private void tiltViewWhenHurtHook(PoseStack matrices, float tickDelta, CallbackInfo ci) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.hurtCam.getValue())
            ci.cancel();
    }
}
