package thunder.hack.core.manager.client;

import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.shaders.satin.api.managed.ManagedShaderEffect;
import thunder.hack.utility.render.shaders.satin.api.managed.ShaderEffectManager;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.manager.IManager;
import thunder.hack.features.modules.render.Shaders;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;

public class ShaderManager implements IManager {
    private final static List<RenderTask> tasks = new ArrayList<>();
    private ThunderHackFramebuffer shaderBuffer;

    public float time = 0;

    public static ManagedShaderEffect DEFAULT_OUTLINE;
    public static ManagedShaderEffect SMOKE_OUTLINE;
    public static ManagedShaderEffect GRADIENT_OUTLINE;
    public static ManagedShaderEffect SNOW_OUTLINE;
    public static ManagedShaderEffect FADE_OUTLINE;

    public static ManagedShaderEffect DEFAULT;
    public static ManagedShaderEffect SMOKE;
    public static ManagedShaderEffect GRADIENT;
    public static ManagedShaderEffect SNOW;
    public static ManagedShaderEffect FADE;

    public void renderShader(Runnable runnable, Shader mode) {
        tasks.add(new RenderTask(runnable, mode));
    }

    public void renderShaders() {
        if (DEFAULT == null) {
            shaderBuffer = new ThunderHackFramebuffer(mc.getMainRenderTarget().width, mc.getMainRenderTarget().height);
            reloadShaders();
        }

        if(shaderBuffer == null)
            return;

        tasks.forEach(t -> applyShader(t.task(), t.shader()));
        tasks.clear();
    }

    public void applyShader(Runnable runnable, Shader mode) {
        runnable.run();
    }

    public ManagedShaderEffect getShader(@NotNull Shader mode) {
        return switch (mode) {
            case Gradient -> GRADIENT;
            case Smoke -> SMOKE;
            case Snow -> SNOW;
            case Fade -> FADE;
            default -> DEFAULT;
        };
    }

    public ManagedShaderEffect getShaderOutline(@NotNull Shader mode) {
        return switch (mode) {
            case Gradient -> GRADIENT_OUTLINE;
            case Smoke -> SMOKE_OUTLINE;
            case Snow -> SNOW_OUTLINE;
            case Fade -> FADE_OUTLINE;
            default -> DEFAULT_OUTLINE;
        };
    }

    public void setupShader(Shader shader, ManagedShaderEffect effect) {
        Shaders shaders = ModuleManager.shaders;
        if (shader == Shader.Gradient) {
            effect.setUniformValue("alpha0", shaders.glow.getValue() ? -1.0f : shaders.outlineColor.getValue().getAlpha() / 255.0f);
            effect.setUniformValue("alpha1", shaders.fillAlpha.getValue() / 255f);
            effect.setUniformValue("alpha2", shaders.alpha2.getValue() / 255f);
            effect.setUniformValue("lineWidth", shaders.lineWidth.getValue());
            effect.setUniformValue("oct", shaders.octaves.getValue());
            effect.setUniformValue("quality", shaders.quality.getValue());
            effect.setUniformValue("factor", shaders.factor.getValue());
            effect.setUniformValue("moreGradient", shaders.gradient.getValue());
            effect.setUniformValue("resolution", (float) mc.getWindow().getGuiScaledWidth(), (float) mc.getWindow().getGuiScaledHeight());
            effect.setUniformValue("time", time);
            effect.render(Render3DEngine.getTickDelta());
            time += 0.008f;
        } else if (shader == Shader.Smoke) {
            effect.setUniformValue("alpha0", shaders.glow.getValue() ? -1.0f : shaders.outlineColor.getValue().getAlpha() / 255.0f);
            effect.setUniformValue("alpha1", shaders.fillAlpha.getValue() / 255f);
            effect.setUniformValue("lineWidth", shaders.lineWidth.getValue());
            effect.setUniformValue("quality", shaders.quality.getValue());
            effect.setUniformValue("first", shaders.outlineColor.getValue().getGlRed(), shaders.outlineColor.getValue().getGlGreen(), shaders.outlineColor.getValue().getGlBlue(), shaders.outlineColor.getValue().getGlAlpha());
            effect.setUniformValue("second", shaders.outlineColor1.getValue().getGlRed(), shaders.outlineColor1.getValue().getGlGreen(), shaders.outlineColor1.getValue().getGlBlue());
            effect.setUniformValue("third", shaders.outlineColor2.getValue().getGlRed(), shaders.outlineColor2.getValue().getGlGreen(), shaders.outlineColor2.getValue().getGlBlue());
            effect.setUniformValue("ffirst", shaders.fillColor1.getValue().getGlRed(), shaders.fillColor1.getValue().getGlGreen(), shaders.fillColor1.getValue().getGlBlue(), shaders.fillColor1.getValue().getGlAlpha());
            effect.setUniformValue("fsecond", shaders.fillColor2.getValue().getGlRed(), shaders.fillColor2.getValue().getGlGreen(), shaders.fillColor2.getValue().getGlBlue());
            effect.setUniformValue("fthird", shaders.fillColor3.getValue().getGlRed(), shaders.fillColor3.getValue().getGlGreen(), shaders.fillColor3.getValue().getGlBlue());
            effect.setUniformValue("oct", shaders.octaves.getValue());
            effect.setUniformValue("resolution", (float) mc.getWindow().getGuiScaledWidth(), (float) mc.getWindow().getGuiScaledHeight());
            effect.setUniformValue("time", time);
            effect.render(Render3DEngine.getTickDelta());
            time += 0.008f;
        } else if (shader == Shader.Default) {
            effect.setUniformValue("alpha0", shaders.glow.getValue() ? -1.0f : shaders.outlineColor.getValue().getAlpha() / 255.0f);
            effect.setUniformValue("lineWidth", shaders.lineWidth.getValue());
            effect.setUniformValue("quality", shaders.quality.getValue());
            effect.setUniformValue("color", shaders.fillColor1.getValue().getGlRed(), shaders.fillColor1.getValue().getGlGreen(), shaders.fillColor1.getValue().getGlBlue(), shaders.fillColor1.getValue().getGlAlpha());
            effect.setUniformValue("outlinecolor", shaders.outlineColor.getValue().getGlRed(), shaders.outlineColor.getValue().getGlGreen(), shaders.outlineColor.getValue().getGlBlue(), shaders.outlineColor.getValue().getGlAlpha());
            effect.render(Render3DEngine.getTickDelta());
        } else if (shader == Shader.Snow) {
            effect.setUniformValue("color", shaders.fillColor1.getValue().getGlRed(), shaders.fillColor1.getValue().getGlGreen(), shaders.fillColor1.getValue().getGlBlue(), shaders.fillColor1.getValue().getGlAlpha());
            effect.setUniformValue("quality", shaders.quality.getValue());
            effect.setUniformValue("resolution", (float) mc.getWindow().getGuiScaledWidth(), (float) mc.getWindow().getGuiScaledHeight());
            effect.setUniformValue("time", time);
            effect.render(Render3DEngine.getTickDelta());
            time += 0.008f;
        } else if (shader == Shader.Fade) {
            effect.setUniformValue("alpha0", shaders.glow.getValue() ? -1.0f : shaders.outlineColor.getValue().getAlpha() / 255.0f);
            effect.setUniformValue("fillAlpha", shaders.fillAlpha.getValue() / 255f);
            effect.setUniformValue("lineWidth", shaders.lineWidth.getValue());
            effect.setUniformValue("quality", shaders.quality.getValue());
            effect.setUniformValue("outlinecolor", shaders.outlineColor.getValue().getGlRed(), shaders.outlineColor.getValue().getGlGreen(), shaders.outlineColor.getValue().getGlBlue(), shaders.outlineColor.getValue().getGlAlpha());
            effect.setUniformValue("primaryColor", shaders.fillColor1.getValue().getGlRed(), shaders.fillColor1.getValue().getGlGreen(), shaders.fillColor1.getValue().getGlBlue(), shaders.fillColor1.getValue().getAlpha());
            effect.setUniformValue("secondaryColor", shaders.fillColor2.getValue().getGlRed(), shaders.fillColor2.getValue().getGlGreen(), shaders.fillColor2.getValue().getGlBlue(), shaders.fillColor1.getValue().getAlpha());
            effect.setUniformValue("time", (System.currentTimeMillis() % 100000) / 1000f);
            effect.render(Render3DEngine.getTickDelta());
        }
    }

    public void reloadShaders() {
        DEFAULT = ShaderEffectManager.getInstance().manage(Identifier.fromNamespaceAndPath("thunderhack", "shaders/post/outline.json"));
        SMOKE = ShaderEffectManager.getInstance().manage(Identifier.fromNamespaceAndPath("thunderhack", "shaders/post/smoke.json"));
        GRADIENT = ShaderEffectManager.getInstance().manage(Identifier.fromNamespaceAndPath("thunderhack", "shaders/post/gradient.json"));
        SNOW = ShaderEffectManager.getInstance().manage(Identifier.fromNamespaceAndPath("thunderhack", "shaders/post/snow.json"));
        FADE = ShaderEffectManager.getInstance().manage(Identifier.fromNamespaceAndPath("thunderhack", "shaders/post/fade.json"));

        FADE_OUTLINE = ShaderEffectManager.getInstance().manage(Identifier.fromNamespaceAndPath("thunderhack", "shaders/post/fade.json"), managedShaderEffect -> {
            PostChain effect = managedShaderEffect.getShaderEffect();
            if (effect == null) return;
        });

        DEFAULT_OUTLINE = ShaderEffectManager.getInstance().manage(Identifier.fromNamespaceAndPath("thunderhack", "shaders/post/outline.json"), managedShaderEffect -> {
            PostChain effect = managedShaderEffect.getShaderEffect();
            if (effect == null) return;
        });

        SMOKE_OUTLINE = ShaderEffectManager.getInstance().manage(Identifier.fromNamespaceAndPath("thunderhack", "shaders/post/smoke.json"), managedShaderEffect -> {
            PostChain effect = managedShaderEffect.getShaderEffect();
            if (effect == null) return;
        });

        GRADIENT_OUTLINE = ShaderEffectManager.getInstance().manage(Identifier.fromNamespaceAndPath("thunderhack", "shaders/post/gradient.json"), managedShaderEffect -> {
            PostChain effect = managedShaderEffect.getShaderEffect();
            if (effect == null) return;
        });

        SNOW_OUTLINE = ShaderEffectManager.getInstance().manage(Identifier.fromNamespaceAndPath("thunderhack", "shaders/post/snow.json"), managedShaderEffect -> {
            PostChain effect = managedShaderEffect.getShaderEffect();
            if (effect == null) return;
        });
    }

    public static class ThunderHackFramebuffer {
        public int width;
        public int height;

        public ThunderHackFramebuffer(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public void resize(int width, int height, boolean updateViewport) {
            this.width = width;
            this.height = height;
        }
    }

    public boolean fullNullCheck() {
        if (GRADIENT == null || SMOKE == null || DEFAULT == null) {
            shaderBuffer = new ThunderHackFramebuffer(mc.getMainRenderTarget().width, mc.getMainRenderTarget().height);
            reloadShaders();
            return true;
        }

        return false;
    }

    public record RenderTask(Runnable task, Shader shader) {
    }

    public enum Shader {
        Default,
        Smoke,
        Gradient,
        Snow,
        Fade
    }
}