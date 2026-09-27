package thunder.hack.injection;

import thunder.hack.core.manager.client.ModuleManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {
    @Inject(method = "submitNameTag", at = @At("HEAD"), cancellable = true)
    private void renderLabelIfPresent(T entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light, float tickDelta, CallbackInfo info) {
        if(entity instanceof ArmorStand && ModuleManager.noRender.isEnabled() && ModuleManager.noRender.noArmorStands.getValue())
            info.cancel();

        if (entity instanceof Player && ModuleManager.nameTags.isEnabled())
            info.cancel();
    }
}