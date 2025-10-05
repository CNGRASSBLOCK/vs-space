package org.valkyrienskies.vs_space.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.vs_space.data.VSSpaceDataPack;

@Mixin(LevelRenderer.class)
public abstract class SkyBoxRepair {
    @Inject(method = "renderEndSky", at = @At("HEAD"), cancellable = true) private void renderEndSky(PoseStack p_109781_, CallbackInfo ci) { ci.cancel(); }
}