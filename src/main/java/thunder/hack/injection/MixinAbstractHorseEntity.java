package thunder.hack.injection;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.core.manager.client.ModuleManager;

@Mixin(AbstractHorse.class)
public abstract class MixinAbstractHorseEntity extends Animal {
    protected MixinAbstractHorseEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "isSaddled", at = @At("HEAD"), cancellable = true)
    public void onIsSaddled(CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.entityControl.isEnabled())
            cir.setReturnValue(true);
    }
}
