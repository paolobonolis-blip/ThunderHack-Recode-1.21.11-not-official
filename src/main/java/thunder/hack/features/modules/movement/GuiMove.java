package thunder.hack.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.inventory.ContainerInput;
import thunder.hack.events.impl.EventClickSlot;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.player.MovementUtility;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class GuiMove extends Module {
    public GuiMove() {
        super("GuiMove", Category.MOVEMENT);
    }

    private final Setting<Bypass> clickBypass = new Setting<>("Bypass", Bypass.None);
    private final Setting<Boolean> rotateOnArrows = new Setting<>("RotateOnArrows", true);
    private final Setting<Boolean> sneak = new Setting<>("sneak", false);

    private final Queue<ServerboundContainerClickPacket> storedClicks = new LinkedList<>();
    private AtomicBoolean pause = new AtomicBoolean();

    @Override
    public void onUpdate() {
        if (mc.screen != null && !(mc.screen instanceof ChatScreen)) {
            for (KeyMapping k : new KeyMapping[]{mc.options.keyUp, mc.options.keyDown, mc.options.keyLeft, mc.options.keyRight, mc.options.keyJump, mc.options.keySprint})
                k.setDown(isKeyPressed(InputConstants.getKey(k.saveString()).getValue()));

            float deltaX = 0;
            float deltaY = 0;

            if (rotateOnArrows.getValue()) {
                if (isKeyPressed(264))
                    deltaY += 30f;

                if (isKeyPressed(265))
                    deltaY -= 30f;

                if (isKeyPressed(262))
                    deltaX += 30f;

                if (isKeyPressed(263))
                    deltaX -= 30f;

                if (deltaX != 0 || deltaY != 0)
                    mc.player.turn(deltaX, deltaY);
            }

            if (sneak.getValue())
                mc.options.keyShift.setDown(isKeyPressed(InputConstants.getKey(mc.options.keyShift.saveString()).getValue()));
        }
    }

    @EventHandler
    public void onClickSlot(EventClickSlot e) {
        if (clickBypass.is(Bypass.DisableClicks) && (MovementUtility.isMoving() || mc.options.keyJump.isDown()))
            e.cancel();
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send e) {
        if (!MovementUtility.isMoving() || !mc.options.keyJump.isDown() || pause.get())
            return;

        if (e.getPacket() instanceof ServerboundContainerClickPacket click) {
            switch (clickBypass.getValue()) {
                case GrimSwap -> {
                    if (click.containerInput() != ContainerInput.PICKUP && click.containerInput() != ContainerInput.PICKUP_ALL)
                        sendPacket(new ServerboundContainerClosePacket(0));
                }

                case StrictNCP -> {
                    if (mc.player.onGround() && !mc.level.getBlockCollisions(mc.player, mc.player.getBoundingBox().move(0.0, 0.0656, 0.0)).iterator().hasNext()) {
                        if (mc.player.isSprinting())
                            sendPacket(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.STOP_SPRINTING));
                        sendPacket(new ServerboundMovePlayerPacket.Pos(mc.player.getX(), mc.player.getY() + 0.0656, mc.player.getZ(), false, false));
                    }
                }

                case StrictNCP2 -> {
                    if (mc.player.onGround() && !mc.level.getBlockCollisions(mc.player, mc.player.getBoundingBox().move(0.0, 0.000000271875, 0.0)).iterator().hasNext()) {
                        if (mc.player.isSprinting())
                            sendPacket(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.STOP_SPRINTING));
                        sendPacket(new ServerboundMovePlayerPacket.Pos(mc.player.getX(), mc.player.getY() + 0.000000271875, mc.player.getZ(), false, false));
                    }
                }

                case MatrixNcp -> {
                    sendPacket(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.STOP_SPRINTING));
                    mc.options.keyUp.setDown(false);
                    MovementUtility.setKeyboardInput(false, false, false, false);
                }

                case Delay -> {
                    storedClicks.add(click);
                    e.cancel();
                }
            }
        }

        if (e.getPacket() instanceof ServerboundContainerClosePacket) {
            if (clickBypass.is(Bypass.Delay)) {
                pause.set(true);
                while (!storedClicks.isEmpty())
                    sendPacket(storedClicks.poll());
                pause.set(false);
            }
        }
    }

    @EventHandler
    public void onPacketSendPost(PacketEvent.SendPost e) {
        if (e.getPacket() instanceof ServerboundContainerClickPacket) {
            if (mc.player.isSprinting() && clickBypass.is(Bypass.StrictNCP))
                sendPacket(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.START_SPRINTING));
        }
    }

    private enum Bypass {
        DisableClicks, None, StrictNCP, GrimSwap, MatrixNcp, Delay, StrictNCP2
    }
}