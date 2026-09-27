package thunder.hack.injection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.PostChain;
import thunder.hack.utility.interfaces.IShaderEffect;

@Mixin(PostChain.class)
public class MixinShaderEffect implements IShaderEffect {

    @Override
    public void addFakeTargetHook(String name, RenderTarget buffer) {
    }

    @Inject(method = "close", at = @At("HEAD"))
    void deleteFakeBuffersHook(CallbackInfo ci) {
    }
}
