package thunder.hack.injection.accesors;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Explosion.class)
public interface IExplosion {
    @Mutable
    @Accessor("x")
    void setX(double x);

    @Mutable
    @Accessor("y")
    void setY(double y);

    @Mutable
    @Accessor("z")
    void setZ(double z);

    @Mutable
    @Accessor("entity")
    void setEntity(Entity entity);

    @Mutable
    @Accessor("world")
    void setWorld(Level world);

    @Mutable
    @Accessor("world")
    Level getWorld();

    @Mutable
    @Accessor("damageSource")
    DamageSource getDamageSource();
}
