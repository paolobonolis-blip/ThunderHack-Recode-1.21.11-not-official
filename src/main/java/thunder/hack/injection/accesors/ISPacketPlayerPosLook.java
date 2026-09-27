package thunder.hack.injection.accesors;

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientboundPlayerPositionPacket.class)
public interface ISPacketPlayerPosLook {
    @Accessor("yaw")
    void setYaw(float val);

    @Accessor("pitch")
    void setPitch(float val);
}