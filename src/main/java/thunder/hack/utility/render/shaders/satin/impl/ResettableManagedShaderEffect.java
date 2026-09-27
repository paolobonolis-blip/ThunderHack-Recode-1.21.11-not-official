package thunder.hack.utility.render.shaders.satin.impl;

import com.mojang.logging.LogUtils;
import thunder.hack.utility.render.shaders.satin.api.managed.ManagedFramebuffer;
import thunder.hack.utility.render.shaders.satin.api.managed.ManagedShaderEffect;
import thunder.hack.utility.render.shaders.satin.api.managed.uniform.SamplerUniformV2;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceProvider;

public final class ResettableManagedShaderEffect extends ResettableManagedShaderBase<PostChain> implements ManagedShaderEffect {

    private final Consumer<ManagedShaderEffect> initCallback;
    private final Map<String, FramebufferWrapper> managedTargets;
    private final Map<String, ManagedSamplerUniformV2> managedSamplers = new HashMap<>();

    public ResettableManagedShaderEffect(Identifier location, Consumer<ManagedShaderEffect> initCallback) {
        super(location);
        this.initCallback = initCallback;
        this.managedTargets = new HashMap<>();
    }

    @Override
    public PostChain getShaderEffect() {
        return getShaderOrLog();
    }

    @Override
    protected PostChain parseShader(ResourceProvider resourceFactory, Minecraft mc, Identifier location) throws IOException {
        return null;
    }

    @Override
    public void setup(int windowWidth, int windowHeight) {
        if (this.shader == null) {
            return;
        }

        this.initCallback.accept(this);
    }

    @Override
    public void render(float tickDelta) {
    }

    @Override
    public ManagedFramebuffer getTarget(String name) {
        return this.managedTargets.computeIfAbsent(name, n -> new FramebufferWrapper(n));
    }

    @Override
    public void setUniformValue(String uniformName, int value) {
        this.findUniform1i(uniformName).set(value);
    }

    @Override
    public void setUniformValue(String uniformName, float value) {
        this.findUniform1f(uniformName).set(value);
    }

    @Override
    public void setUniformValue(String uniformName, float value0, float value1) {
        this.findUniform2f(uniformName).set(value0, value1);
    }

    @Override
    public void setUniformValue(String uniformName, float value0, float value1, float value2) {
        this.findUniform3f(uniformName).set(value0, value1, value2);
    }

    @Override
    public void setUniformValue(String uniformName, float value0, float value1, float value2, float value3) {
        this.findUniform4f(uniformName).set(value0, value1, value2, value3);
    }

    @Override
    public SamplerUniformV2 findSampler(String samplerName) {
        return manageUniform(this.managedSamplers, ManagedSamplerUniformV2::new, samplerName, "sampler");
    }

    @Override
    protected boolean setupUniform(ManagedUniformBase uniform, PostChain shader) {
        return uniform.findUniformTargets(java.util.Collections.emptyList());
    }

    @Override
    protected void logInitError(IOException e) {
        LogUtils.getLogger().error("Could not create screen shader {}", this.getLocation(), e);
    }

    private PostChain getShaderOrLog() {
        if (!this.isInitialized() && !this.isErrored()) {
            this.initializeOrLog(Minecraft.getInstance().getResourceManager());
        }
        return this.shader;
    }
}