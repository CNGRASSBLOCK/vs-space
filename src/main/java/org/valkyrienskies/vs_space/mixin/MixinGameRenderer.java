package org.valkyrienskies.vs_space.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GameRenderer.class)
public class MixinGameRenderer {
    @Shadow private float renderDistance;

    /**
     * @author 草方块
     * @reason null
     * 防止光影云层反转
     */
    @Overwrite
    public float getDepthFar() { return Float.POSITIVE_INFINITY; }
}
