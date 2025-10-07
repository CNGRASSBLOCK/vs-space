package org.valkyrienskies.vs_space.mixin.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.vs_space.data.VSSpaceDataPack;

@Mixin(value = GameRenderer.class)
public class FarRendererFix {
    @Shadow private float renderDistance;

    /**
     * @author 草方块
     * @reason null
     * 防止光影云层反转
     */
    @Overwrite
    public float getDepthFar() {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return renderDistance * 4.0f;
        if (VSSpaceDataPack.SpaceWorld_ID.contains(clientLevel.dimension().location().toString())) return 1048576f;
        return renderDistance * 4.0f;
    }
}
