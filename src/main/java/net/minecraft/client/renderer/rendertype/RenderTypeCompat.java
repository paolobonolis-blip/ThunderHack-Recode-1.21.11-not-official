package net.minecraft.client.renderer.rendertype;

import com.mojang.blaze3d.pipeline.RenderPipeline;

public final class RenderTypeCompat {
    public static RenderType create(String name, RenderPipeline pipeline) {
        return RenderType.create(name, RenderSetup.builder(pipeline).createRenderSetup());
    }

    private RenderTypeCompat() {
    }
}