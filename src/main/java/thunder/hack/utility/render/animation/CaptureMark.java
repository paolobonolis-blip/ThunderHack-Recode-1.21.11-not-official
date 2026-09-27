package thunder.hack.utility.render.animation;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import thunder.hack.features.modules.client.HudEditor;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;
import thunder.hack.utility.render.RenderCompat;
import thunder.hack.utility.render.TextureStorage;

import static thunder.hack.features.modules.Module.mc;

public class CaptureMark {
    private static float espValue = 1f, prevEspValue;
    private static float espSpeed = 1f;
    private static boolean flipSpeed;

    public static void render(Entity target) {
        Camera camera = mc.gameRenderer.getMainCamera();

        double tPosX = Render2DEngine.interpolate(target.xOld, target.getX(), Render3DEngine.getTickDelta()) - camera.position().x;
        double tPosY = Render2DEngine.interpolate(target.yOld, target.getY(), Render3DEngine.getTickDelta()) - camera.position().y;
        double tPosZ = Render2DEngine.interpolate(target.zOld, target.getZ(), Render3DEngine.getTickDelta()) - camera.position().z;

        PoseStack matrices = new PoseStack();
        matrices.mulPose(Axis.XP.rotationDegrees(camera.xRot()));
        matrices.mulPose(Axis.YP.rotationDegrees(camera.yRot() + 180.0F));
        matrices.translate(tPosX, (tPosY + target.getEyeHeight(target.getPose()) / 2f), tPosZ);
        matrices.mulPose(Axis.YP.rotationDegrees(-camera.yRot()));
        matrices.mulPose(Axis.XP.rotationDegrees(camera.xRot()));
        matrices.mulPose(Axis.ZP.rotationDegrees(Render2DEngine.interpolateFloat(prevEspValue, espValue, Render3DEngine.getTickDelta())));
        matrices.translate(-0.75, -0.75, -0.01);
        Matrix4f matrix = matrices.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(matrix, 0, 1.5f, 0).setUv(0f, 1f).setColor(HudEditor.getColor(90).getRGB());
        buffer.addVertex(matrix, 1.5f, 1.5f, 0).setUv(1f, 1f).setColor(HudEditor.getColor(0).getRGB());
        buffer.addVertex(matrix, 1.5f, 0, 0).setUv(1f, 0).setColor(HudEditor.getColor(180).getRGB());
        buffer.addVertex(matrix, 0, 0, 0).setUv(0, 0).setColor(HudEditor.getColor(270).getRGB());
        RenderCompat.drawSceneGui(buffer);
    }

    public static void tick() {
        prevEspValue = espValue;
        espValue += espSpeed;
        if (espSpeed > 25) flipSpeed = true;
        if (espSpeed < -25) flipSpeed = false;
        espSpeed = flipSpeed ? espSpeed - 0.5f : espSpeed + 0.5f;
    }
}
