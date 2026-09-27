package thunder.hack.utility.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypeCompat;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.lwjgl.opengl.GL44C;

public final class RenderCompat {
    private static final RenderType WORLD_FILLED = RenderTypeCompat.create("th_world_filled", RenderPipelines.DEBUG_FILLED_BOX);
    private static final RenderType WORLD_FAN = RenderTypeCompat.create("th_world_fan", RenderPipelines.DEBUG_TRIANGLE_FAN);
    private static final RenderType GUI_COLOR = RenderTypeCompat.create("th_gui_color", RenderPipelines.GUI);
    private static final RenderType GUI_TEXTURED = RenderTypeCompat.create("th_gui_textured", RenderPipelines.GUI_TEXTURED);

    private RenderCompat() {
    }

    public static void drawScene(BufferBuilder bb) {
        MeshData mesh = bb.build();
        if (mesh == null) return;
        VertexFormat fmt = mesh.drawState().format();
        RenderType rt;
        if (fmt == DefaultVertexFormat.POSITION_COLOR_NORMAL) {
            rt = RenderTypes.LINES;
        } else {
            VertexFormat.Mode mode = mesh.drawState().mode();
            if (mode == VertexFormat.Mode.QUADS) rt = WORLD_FILLED;
            else if (mode == VertexFormat.Mode.TRIANGLE_FAN) rt = WORLD_FAN;
            else rt = WORLD_FILLED;
        }
        rt.draw(mesh);
    }

    public static void drawSceneGui(BufferBuilder bb) {
        MeshData mesh = bb.build();
        if (mesh == null) return;
        VertexFormat fmt = mesh.drawState().format();
        RenderType rt = (fmt == DefaultVertexFormat.POSITION_TEX || fmt == DefaultVertexFormat.POSITION_TEX_COLOR) ? GUI_TEXTURED : GUI_COLOR;
        rt.draw(mesh);
    }

    public static void beginScissor(int x, int y, int width, int height) {
        GL44C.glEnable(GL44C.GL_SCISSOR_TEST);
        GL44C.glScissor(x, y, width, height);
    }

    public static void endScissor() {
        GL44C.glDisable(GL44C.GL_SCISSOR_TEST);
    }
}