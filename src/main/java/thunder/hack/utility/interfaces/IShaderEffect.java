package thunder.hack.utility.interfaces;

import com.mojang.blaze3d.pipeline.RenderTarget;

public interface IShaderEffect {
    void addFakeTargetHook(String name, RenderTarget buffer);
}
