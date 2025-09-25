package org.valkyrienskies.vs_space.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.vs_space.VSSpace;

import java.io.IOException;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = VSSpace.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VSSpaceShader {
    public static Supplier<ShaderInstance> StarRender;
    public static Supplier<ShaderInstance> PlanetRender;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(
                event.getResourceProvider(),
                new ResourceLocation(VSSpace.MODID, "star/star_render"),
                DefaultVertexFormat.POSITION),
                shader -> StarRender = () -> shader
        );

        event.registerShader(new ShaderInstance(
                        event.getResourceProvider(),
                        new ResourceLocation(VSSpace.MODID, "planet/planet_render"),
                        DefaultVertexFormat.NEW_ENTITY),
                shader -> PlanetRender = () -> shader
        );
    }
}
