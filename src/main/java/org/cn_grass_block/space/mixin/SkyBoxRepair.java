package org.cn_grass_block.space.mixin;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.cn_grass_block.space.data.SpaceDataPack;

@Mixin(LevelRenderer.class)
public abstract class SkyBoxRepair {
    @Inject(method = "renderEndSky", at = @At("HEAD"), cancellable = true) private void renderEndSky(PoseStack p_109781_, CallbackInfo ci) { if (SpaceDataPack.SpaceWorld_ID.contains(Minecraft.getInstance().level.dimension().location().toString())) ci.cancel(); }
}