package org.valkyrienskies.vs_space.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class VSSpaceRenderType extends RenderType {
    public VSSpaceRenderType(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) { super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_); }

    private static final RenderType StarRender = create("star_render", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(VSSpaceShader.StarRender)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(false));
    private static final Function<ResourceLocation, RenderType> PlanetRender = Util.memoize((p_286149_) -> create("planet_render", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
            true, true,
            CompositeState.builder()
                    .setShaderState(new ShaderStateShard(VSSpaceShader.PlanetRender))
                    .setTextureState(new RenderStateShard.TextureStateShard(p_286149_, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(NO_LIGHTMAP)
                    .createCompositeState(false)));

    public static RenderType StarRender() { return StarRender; }
    public static RenderType PlanetRender(ResourceLocation resourceLocation) { return PlanetRender.apply(resourceLocation); }

    public static class LightmapStateShard extends RenderStateShard.BooleanStateShard {
        public LightmapStateShard(boolean p_110271_) {
            super("lightmap", () -> {
                if (p_110271_) {
                    Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
                }

            }, () -> {
                if (p_110271_) {
                    Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
                }

            }, p_110271_);
        }
    }
}
