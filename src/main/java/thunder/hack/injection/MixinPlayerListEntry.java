package thunder.hack.injection;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thunder.hack.core.manager.client.ModuleManager;
import thunder.hack.utility.OptifineCapes;
import thunder.hack.utility.ThunderUtility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

@Mixin(PlayerInfo.class)
public class MixinPlayerListEntry {

    @Unique
    private boolean loadedCapeTexture;

    @Unique
    private Identifier customCapeTexture;

    @Inject(method = "<init>(Lcom/mojang/authlib/GameProfile;Z)V", at = @At("TAIL"))
    private void initHook(GameProfile profile, boolean secureChatEnforced, CallbackInfo ci) {
        getTexture(profile);
    }

    @Inject(method = "getSkin", at = @At("TAIL"), cancellable = true)
    private void getCapeTexture(CallbackInfoReturnable<PlayerSkin> cir) {
        if (customCapeTexture != null) {
            PlayerSkin prev = cir.getReturnValue();
            ClientAsset.Texture cape = new ClientAsset.ResourceTexture(customCapeTexture);
            PlayerSkin newTextures = new PlayerSkin(prev.body(), cape, cape, prev.model(), prev.secure());
            cir.setReturnValue(newTextures);
        }
    }

    @Unique
    private void getTexture(GameProfile profile) {
        if (loadedCapeTexture) return;
        loadedCapeTexture = true;

        Util.backgroundExecutor().execute(() -> {
            try {
                URL capesList = new URL("https://raw.githubusercontent.com/Pan4ur/THRecodeUtil/main/capes/capeBase.txt");
                BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String colune = inputLine.trim();
                    String name = colune.split(":")[0];
                    String cape = colune.split(":")[1];
                    if (Objects.equals(profile.name(), name)) {
                        customCapeTexture = Identifier.fromNamespaceAndPath("thunderhack", "textures/capes/" + cape + ".png");
                        return;
                    }
                }
            } catch (Exception ignored) {
            }

            for (String str : ThunderUtility.starGazer) {
                if (profile.name().toLowerCase().equals(str.toLowerCase()))
                    customCapeTexture = Identifier.fromNamespaceAndPath("thunderhack", "textures/capes/starcape.png");
            }

            if (ModuleManager.optifineCapes.isEnabled())
                OptifineCapes.loadPlayerCape(profile, id -> {
                    customCapeTexture = id;
                });
        });
    }
}