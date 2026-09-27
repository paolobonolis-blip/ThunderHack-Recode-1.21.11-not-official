package thunder.hack.injection.accesors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.Uniform;
import java.util.Map;

@Mixin(GlProgram.class)
public interface ShaderProgramAccessor {
    @Accessor("loadedUniforms")
    Map<String, Uniform> getUniformsHook();
}