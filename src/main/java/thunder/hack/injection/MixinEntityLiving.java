package thunder.hack.injection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.ThunderHack;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.events.impl.EventTravel;
import thunder.hack.features.modules.Module;
import thunder.hack.features.modules.combat.Aura;
import thunder.hack.features.modules.render.Animations;
import thunder.hack.utility.interfaces.IEntityLiving;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.Vec3;

import static thunder.hack.features.modules.Module.mc;
import static thunder.hack.features.modules.movement.WaterSpeed.Mode.CancelResurface;

@Mixin(LivingEntity.class)
public class MixinEntityLiving implements IEntityLiving {
    @Shadow
    protected double serverX;
    @Shadow
    protected double serverY;
    @Shadow
    protected double serverZ;

    @Unique
    double prevServerX, prevServerY, prevServerZ;

    @Unique
    public List<Aura.Position> positonHistory = new ArrayList<>();

    @Override
    public List<Aura.Position> getPositionHistory() {
        return positonHistory;
    }

    @Inject(method = "getCurrentSwingDuration", at = {@At("HEAD")}, cancellable = true)
    private void getArmSwingAnimationEnd(final CallbackInfoReturnable<Integer> info) {
        if (!ModuleManager.noRender.noSwing.getValue() && ModuleManager.animations.shouldChangeAnimationDuration() && Animations.slowAnimation.getValue())
            info.setReturnValue(Animations.slowAnimationVal.getValue());
    }

    @Inject(method = {"moveOrInterpolateTo"}, at = {@At("HEAD")})
    private void updateTrackedPositionAndAnglesHook(double x, double y, double z, float yaw, float pitch, int interpolationSteps, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;
        prevServerX = serverX;
        prevServerY = serverY;
        prevServerZ = serverZ;
        positonHistory.add(new Aura.Position(serverX, serverY, serverZ));
        positonHistory.removeIf(Aura.Position::shouldRemove);
    }

    @Override
    public double getPrevServerX() {
        return prevServerX;
    }

    @Override
    public double getPrevServerY() {
        return prevServerY;
    }

    @Override
    public double getPrevServerZ() {
        return prevServerZ;
    }

    @Unique
    private boolean prevFlying = false;

    @Inject(method = "isFallFlying", at = @At("TAIL"), cancellable = true)
    public void isFallFlyingHook(CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.elytraRecast.isEnabled()) {
            boolean elytra = cir.getReturnValue();
            if (prevFlying && !cir.getReturnValue()) {
                cir.setReturnValue(ModuleManager.elytraRecast.castElytra());
            }
            prevFlying = elytra;
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travelHook(Vec3 movementInput, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;
        if ((LivingEntity) (Object) this != mc.player) return;
        final EventTravel event = new EventTravel(mc.player.getDeltaMovement(), true);
        ThunderHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            mc.player.move(MoverType.SELF, event.getmVec());
            ci.cancel();
        }
    }

    @Inject(method = "travel", at = @At("RETURN"), cancellable = true)
    public void travelPostHook(Vec3 movementInput, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;
        if ((LivingEntity) (Object) this != mc.player) return;
        final EventTravel event = new EventTravel(movementInput, false);
        ThunderHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            mc.player.move(MoverType.SELF, mc.player.getDeltaMovement());
            ci.cancel();
        }
    }

    @ModifyVariable(method = "setSprinting", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private boolean setSprintingHook(boolean sprinting) {
        if (mc.player != null && mc.level != null && ModuleManager.waterSpeed.isEnabled() && ModuleManager.waterSpeed.mode.is(CancelResurface)) {
            if (mc.player.isInWater() || mc.level.getBlockState(BlockPos.containing(mc.player.position().add(0, -0.5, 0))).getBlock() instanceof LiquidBlock)
                return true;
        }
        return sprinting;
    }

    @Inject(method = "getCurrentSwingDuration", at = @At("HEAD"), cancellable = true)
    private void onGetHandSwingDuration(CallbackInfoReturnable<Integer> cir) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.noSwing.getValue()) {
            cir.setReturnValue(0);
            cir.cancel();
        }
    }
}