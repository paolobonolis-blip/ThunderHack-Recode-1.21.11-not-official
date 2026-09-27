package thunder.hack.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import thunder.hack.events.impl.EventCollision;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class Jesus extends Module {
    public Jesus() {
        super("Jesus", Category.MOVEMENT);
    }

    public final Setting<Mode> mode = new Setting<>("Mode", Mode.SOLID);

    @EventHandler
    public void onCollide(EventCollision e) {
        if (e.getState().getBlock() instanceof LiquidBlock) {
            e.setState(mode.is(Mode.SOLID) ? Blocks.ENDER_CHEST.defaultBlockState() : Blocks.OBSIDIAN.defaultBlockState());
        }
    }

    public enum Mode {
        SOLID, SOLID2
    }
}
