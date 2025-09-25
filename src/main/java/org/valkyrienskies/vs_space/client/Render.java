package org.valkyrienskies.vs_space.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.valkyrienskies.vs_space.classes.CelestialBodys.Planet;
import org.valkyrienskies.vs_space.classes.CelestialBodys.Star;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Render {
    private static Star star = new Star("0", new Vector3d(30000,0,0), new Quaterniond(), 10000);
    private static Planet earth = new Planet("0", new Vector3d(-300,0,0), new Quaterniond(), 100);

    @SubscribeEvent
    public static void onWorldRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        star.setTemperature(20000);
        star.render(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource());
        earth.render(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource());
    }
}
