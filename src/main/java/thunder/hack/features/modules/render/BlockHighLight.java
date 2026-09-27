package thunder.hack.features.modules.render;

import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;

import static thunder.hack.utility.render.Render3DEngine.*;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BlockHighLight extends Module {
    public BlockHighLight() {
        super("BlockHighLight", Category.RENDER);
    }

    private final Setting<Mode> mode = new Setting("Mode", Mode.Outline);
    private final Setting<ColorSetting> color = new Setting<>("Color", new ColorSetting(0xFFFFFFFF));
    private final Setting<Float> lineWidth = new Setting<>("LineWidth", 1F, 0f, 5F);

    private enum Mode {
        Both, BothSide, Fill, FilledSide, Outline, OutlinedSide
    }

    public void onRender3D(PoseStack stack) {
        if (mc.hitResult == null) return;
        if (mc.hitResult.getType() != HitResult.Type.BLOCK) return;
        if (!(mc.hitResult instanceof BlockHitResult bhr)) return;

        switch (mode.getValue()) {
            case Both -> {
                drawBoxOutline(new AABB(bhr.getBlockPos()), Render2DEngine.injectAlpha(color.getValue().getColorObject(), 255), lineWidth.getValue());
                drawFilledBox(stack, new AABB(bhr.getBlockPos()), color.getValue().getColorObject());
            }
            case BothSide -> {
                drawSideOutline(new AABB(bhr.getBlockPos()), Render2DEngine.injectAlpha(color.getValue().getColorObject(),255), lineWidth.getValue(),bhr.getDirection());
                drawFilledSide(stack,new AABB(bhr.getBlockPos()),color.getValue().getColorObject(),bhr.getDirection());
            }
            case Fill -> drawFilledBox(stack,new AABB(bhr.getBlockPos()),color.getValue().getColorObject());
            case FilledSide -> drawFilledSide(stack,new AABB(bhr.getBlockPos()),color.getValue().getColorObject(),bhr.getDirection());

            case Outline ->  drawBoxOutline(new AABB(bhr.getBlockPos()), Render2DEngine.injectAlpha(color.getValue().getColorObject(),255), lineWidth.getValue());
            case OutlinedSide -> drawSideOutline(new AABB(bhr.getBlockPos()), Render2DEngine.injectAlpha(color.getValue().getColorObject(),255), lineWidth.getValue(),bhr.getDirection());
        }
    }
}
