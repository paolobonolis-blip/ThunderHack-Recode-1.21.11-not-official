package thunder.hack.features.modules.render;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.world.clock.ClockNetworkState;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.BooleanSettingGroup;
import thunder.hack.setting.impl.ColorSetting;

import java.awt.*;
import java.util.Map;

public class WorldTweaks extends Module {
    public WorldTweaks() {
        super("WorldTweaks", Category.RENDER);
    }

    public static final Setting<BooleanSettingGroup> fogModify = new Setting<>("FogModify", new BooleanSettingGroup(true));
    public static final Setting<Integer> fogStart = new Setting<>("FogStart", 0, 0, 256).addToGroup(fogModify);
    public static final Setting<Integer> fogEnd = new Setting<>("FogEnd", 64, 10, 256).addToGroup(fogModify);
    public static final Setting<ColorSetting> fogColor = new Setting<>("FogColor", new ColorSetting(new Color(0xA900FF))).addToGroup(fogModify);
    public final Setting<Boolean> ctime = new Setting<>("ChangeTime", false);
    public final Setting<Integer> ctimeVal = new Setting<>("Time", 21, 0, 23);

    long oldTime;

    private Holder<WorldClock> overworldClock() {
        return mc.level.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK).getOrThrow(WorldClocks.OVERWORLD);
    }

    private void setWorldClockTicks(long ticks) {
        mc.level.clockManager().handleUpdates(mc.level.getGameTime(), Map.of(overworldClock(), new ClockNetworkState(ticks, 0f, 1f)));
    }

    @Override
    public void onEnable() {
        oldTime = mc.level.clockManager().getTotalTicks(overworldClock());
    }

    @Override
    public void onDisable() {
        setWorldClockTicks(oldTime);
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof ClientboundSetTimePacket && ctime.getValue()) {
            oldTime = mc.level.clockManager().getTotalTicks(overworldClock());
            event.cancel();
        }
    }

    @Override
    public void onUpdate() {
        if (ctime.getValue()) setWorldClockTicks(ctimeVal.getValue() * 1000L);
    }
}