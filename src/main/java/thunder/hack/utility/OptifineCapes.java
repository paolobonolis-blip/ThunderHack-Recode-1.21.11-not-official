package thunder.hack.utility;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class OptifineCapes {
    /**
     * author: @dragonostic
     * of-capes
     */

    public interface ReturnCapeTexture {
        void response(Identifier id);
    }

    public static void loadPlayerCape(GameProfile player, ReturnCapeTexture response) {
        try {
            String uuid = player.id().toString();
            DynamicTexture nIBT = getCapeFromURL(String.format("http://s.optifine.net/capes/%s.png", player.name()));
            Identifier capeTexture = Identifier.withDefaultNamespace("th-cape-" + uuid);
            Minecraft.getInstance().getTextureManager().register(capeTexture, nIBT);
            response.response(capeTexture);
        } catch (Exception ignored) {
        }
    }

    public static DynamicTexture getCapeFromURL(String capeStringURL) {
        try {
            URL capeURL = new URL(capeStringURL);
            return getCapeFromStream(capeURL.openStream());
        } catch (IOException e) {
            return null;
        }
    }

    public static DynamicTexture getCapeFromStream(InputStream image) {
        NativeImage cape = null;
        try {
            cape = NativeImage.read(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cape != null) {
            return new DynamicTexture(() -> "th-cape", parseCape(cape));
        }
        return null;
    }

    public static NativeImage parseCape(NativeImage image) {
        int imageWidth = 64;
        int imageHeight = 32;
        int imageSrcWidth = image.getWidth();
        int srcHeight = image.getHeight();

        for (int imageSrcHeight = image.getHeight(); imageWidth < imageSrcWidth || imageHeight < imageSrcHeight; imageHeight *= 2) {
            imageWidth *= 2;
        }

        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < imageSrcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                imgNew.setPixelABGR(x, y, image.getPixel(x, y));
            }
        }
        image.close();
        return imgNew;
    }
}
