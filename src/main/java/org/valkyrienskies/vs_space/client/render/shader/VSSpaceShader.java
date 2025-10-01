package org.valkyrienskies.vs_space.client.render.shader;

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
import org.valkyrienskies.vs_space.VSSpace;

import java.io.IOException;
import java.util.function.Supplier;

public class VSSpaceShader {
    //核心着色器
    public static Supplier<ShaderInstance> StarRender;
    public static Supplier<ShaderInstance> PlanetRender;
    //后处理着色器
    public static PostChain StarLight;

    @Mod.EventBusSubscriber(modid = VSSpace.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RegisterShaders {
        @SubscribeEvent
        public static void RegisterShaders(RegisterShadersEvent event) throws IOException {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    new ResourceLocation(VSSpace.MODID, "star/star_render"),
                    DefaultVertexFormat.POSITION), shader -> StarRender = () -> shader);
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    new ResourceLocation(VSSpace.MODID, "planet/planet_render"),
                    DefaultVertexFormat.NEW_ENTITY), shader -> PlanetRender = () -> shader);
        }
    }

    @Mod.EventBusSubscriber(modid = VSSpace.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class RegisterEffects {
        @SubscribeEvent
        public static void RegisterEffects(TickEvent.ClientTickEvent clientTickEvent) {
            if (StarLight == null) {
                try {
                    StarLight = new PostChain(Minecraft.getInstance().getTextureManager(), Minecraft.getInstance().getResourceManager(), Minecraft.getInstance().getMainRenderTarget(),
                            new ResourceLocation(VSSpace.MODID, "shaders/post/star_lights.json"));
                    StarLight.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
                } catch (IOException ignored) {}
            }
        }
    }
}
