package thunder.hack.utility.render.shaders;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import thunder.hack.utility.render.shaders.satin.api.managed.ManagedCoreShader;
import thunder.hack.utility.render.shaders.satin.api.managed.ShaderEffectManager;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.SamplerUniform;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform1f;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform2f;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.Uniform4f;

import java.awt.*;
import net.minecraft.resources.Identifier;

import static thunder.hack.features.modules.Module.mc;

public class BlurProgram {
    private Uniform2f uSize;
    private Uniform2f uLocation;
    private Uniform1f radius;
    private Uniform2f inputResolution;
    private Uniform1f brightness;
    private Uniform1f quality;
    private Uniform4f color1;
    private SamplerUniform sampler;

    public static final ManagedCoreShader BLUR = ShaderEffectManager.getInstance()
            .manageCoreShader(Identifier.fromNamespaceAndPath("thunderhack", "blur"), DefaultVertexFormat.POSITION);

    public BlurProgram() {
        setup();
    }

    public void setParameters(float x, float y, float width, float height, float r, Color c1, float blurStrenth, float blurOpacity) {
        float i = (float) mc.getWindow().getGuiScale();
        radius.set(r * i);
        uLocation.set(x * i, -y * i + mc.getWindow().getGuiScaledHeight() * i - height * i);
        uSize.set(width * i, height * i);
        brightness.set(blurOpacity);
        quality.set(blurStrenth);
        color1.set(c1.getRed() / 255f, c1.getGreen() / 255f, c1.getBlue() / 255f, 1f);
    }

    public void use() {
    }

    protected void setup() {
        this.inputResolution = BLUR.findUniform2f("InputResolution");
        this.brightness = BLUR.findUniform1f("Brightness");
        this.quality = BLUR.findUniform1f("Quality");
        this.color1 = BLUR.findUniform4f("color1");
        this.uSize = BLUR.findUniform2f("uSize");
        this.uLocation = BLUR.findUniform2f("uLocation");
        this.radius = BLUR.findUniform1f("radius");
        sampler = BLUR.findSampler("InputSampler");
    }
}