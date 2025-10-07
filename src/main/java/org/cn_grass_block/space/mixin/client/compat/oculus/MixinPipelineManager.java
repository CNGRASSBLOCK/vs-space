package org.cn_grass_block.space.mixin.client.compat.oculus;


import net.irisshaders.iris.pipeline.PipelineManager;
import net.irisshaders.iris.pipeline.VanillaRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.cn_grass_block.space.data.SpaceDataPack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PipelineManager.class)
public class MixinPipelineManager {
    @Inject(method = "preparePipeline", at = @At("HEAD"), cancellable = true,remap = false)
    private void preparePipeline(NamespacedId currentDimension, CallbackInfoReturnable<WorldRenderingPipeline> cir) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if(clientLevel != null) {
            if (SpaceDataPack.SpaceWorld_ID.contains(clientLevel.dimension().location().toString())) {
                cir.setReturnValue(new VanillaRenderingPipeline());
            }
        }
    }
}
