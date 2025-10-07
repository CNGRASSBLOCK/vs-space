package org.cn_grass_block.space.client.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.cn_grass_block.space.Space;

import java.io.IOException;
import java.util.function.Supplier;

public class SpaceShader {
    //核心着色器
    public static Supplier<ShaderInstance> StarRender;
    public static Supplier<ShaderInstance> PlanetRender;
    public static Supplier<ShaderInstance> BlackHoleRender;
    //后处理着色器
    public static PostChain StarBloom;
    public static PostChain PlanetAtmosphere;
    public static PostChain BlackHoleGravity;

    @Mod.EventBusSubscriber(modid = Space.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RegisterShaders {
        @SubscribeEvent
        public static void RegisterShaders(RegisterShadersEvent event) throws IOException {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    new ResourceLocation(Space.MODID, "star/star_render"),
                    DefaultVertexFormat.POSITION), shader -> StarRender = () -> shader);
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    new ResourceLocation(Space.MODID, "planet/planet_render"),
                    DefaultVertexFormat.NEW_ENTITY), shader -> PlanetRender = () -> shader);
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    new ResourceLocation(Space.MODID, "black_hole/black_hole_render"),
                    DefaultVertexFormat.POSITION), shader -> BlackHoleRender = () -> shader);
        }
    }

    @Mod.EventBusSubscriber(modid = Space.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class RegisterEffects {
        @SubscribeEvent
        public static void RegisterEffects(TickEvent.ClientTickEvent clientTickEvent) {
            try {
                if (StarBloom == null) {
                    StarBloom = new PostChain(Minecraft.getInstance().getTextureManager(), Minecraft.getInstance().getResourceManager(), Minecraft.getInstance().getMainRenderTarget(),
                            new ResourceLocation(Space.MODID, "shaders/post/star/star_bloom.json"));
                    StarBloom.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
                }
                if (PlanetAtmosphere == null) {
                    PlanetAtmosphere = new PostChain(Minecraft.getInstance().getTextureManager(), Minecraft.getInstance().getResourceManager(), Minecraft.getInstance().getMainRenderTarget(),
                            new ResourceLocation(Space.MODID, "shaders/post/planet/planet_atmosphere.json"));
                    PlanetAtmosphere.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
                }
                if (BlackHoleGravity == null) {
                    BlackHoleGravity = new PostChain(Minecraft.getInstance().getTextureManager(), Minecraft.getInstance().getResourceManager(), Minecraft.getInstance().getMainRenderTarget(),
                            new ResourceLocation(Space.MODID, "shaders/post/black_hole/black_hole_gravity.json"));
                    BlackHoleGravity.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
                }
            } catch (Throwable throwable) {
                Space.LOGGER.error(throwable.getMessage()); }
        }
    }
}
