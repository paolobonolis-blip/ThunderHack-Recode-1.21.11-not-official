package thunder.hack.injection;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.core.manager.client.ModuleManager;

@Mixin(EndCrystalRenderer.class)
public class MixinEndCrystalEntityRenderer {

    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    public void render(EndCrystalRenderState endCrystalEntity, PoseStack matrixStack, SubmitNodeCollector vertexConsumerProvider, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (ModuleManager.chams.isEnabled() && ModuleManager.chams.crystals.getValue()) {
            ci.cancel();
        }
    }
}
