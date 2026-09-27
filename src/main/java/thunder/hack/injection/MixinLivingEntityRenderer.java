package thunder.hack.injection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.core.Managers;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.client.ClientSettings;
import thunder.hack.injection.accesors.IClientPlayerEntity;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import static thunder.hack.features.modules.Module.mc;

import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @Unique
    private float originalYRot, originalXRot, originalBodyRot;

    @Shadow
    @Final
    protected M model;

    @Shadow
    @Final
    protected List<RenderLayer<S, M>> layers;

    @Inject(method = "submit", at = @At("HEAD"))
    public void onRenderPre(S livingEntity, PoseStack matrixStack, SubmitNodeCollector vertexConsumerProvider, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;
        if (mc.player != null && mc.player.getControlledVehicle() == null && ClientSettings.renderRotations.getValue()) {
            originalYRot = livingEntity.yRot;
            originalXRot = livingEntity.xRot;
            originalBodyRot = livingEntity.bodyRot;

            livingEntity.xRot = ((IClientPlayerEntity) Minecraft.getInstance().player).getLastPitch();
            livingEntity.yRot = ((IClientPlayerEntity) Minecraft.getInstance().player).getLastYaw();
            livingEntity.bodyRot = Managers.PLAYER.bodyYaw;
        }
    }

    @Inject(method = "submit", at = @At("TAIL"))
    public void onRenderPost(S livingEntity, PoseStack matrixStack, SubmitNodeCollector vertexConsumerProvider, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;
        livingEntity.xRot = originalXRot;
        livingEntity.yRot = originalYRot;
        livingEntity.bodyRot = originalBodyRot;
    }
}
