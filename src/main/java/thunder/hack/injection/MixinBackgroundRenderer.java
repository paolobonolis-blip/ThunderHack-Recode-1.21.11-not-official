package thunder.hack.injection;

import org.joml.Vector4f;
import thunder.hack.core.manager.client.ModuleManager;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.features.modules.render.WorldTweaks;

import static thunder.hack.features.modules.Module.mc;

@Mixin(FogRenderer.class)
public class MixinBackgroundRenderer {
    @Inject(method = "setupFog", at = @At("TAIL"), cancellable = true)
    private void onSetupFog(Camera camera, int viewDistance, DeltaTracker deltaTracker, float f, ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        FogData data = cir.getReturnValue();
        if (data == null) return;

        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.fog.getValue()) {
            data.renderDistanceStart = viewDistance;
            data.renderDistanceEnd = viewDistance * 2.0f;
            data.environmentalStart = viewDistance;
            data.environmentalEnd = viewDistance * 2.0f;
            data.skyEnd = viewDistance * 2.0f;
            data.cloudEnd = viewDistance * 2.0f;
        } else if (mc.player != null && ModuleManager.noRender.isEnabled() && ModuleManager.noRender.blindness.getValue() && mc.player.hasEffect(MobEffects.BLINDNESS)) {
            data.renderDistanceStart = viewDistance;
            data.renderDistanceEnd = viewDistance * 2.0f;
            data.environmentalStart = viewDistance;
            data.environmentalEnd = viewDistance * 2.0f;
        }

        if (ModuleManager.worldTweaks.isEnabled() && WorldTweaks.fogModify.getValue().isEnabled()) {
            data.renderDistanceStart = WorldTweaks.fogStart.getValue();
            data.renderDistanceEnd = WorldTweaks.fogEnd.getValue();
            data.environmentalStart = WorldTweaks.fogStart.getValue();
            data.environmentalEnd = WorldTweaks.fogEnd.getValue();
            data.color = new Vector4f(WorldTweaks.fogColor.getValue().getGlRed(), WorldTweaks.fogColor.getValue().getGlGreen(), WorldTweaks.fogColor.getValue().getGlBlue(), 1.0f);
        }
    }
}