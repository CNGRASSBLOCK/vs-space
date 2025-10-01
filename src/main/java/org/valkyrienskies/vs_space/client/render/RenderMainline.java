package org.valkyrienskies.vs_space.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.stringtemplate.v4.ST;
import org.valkyrienskies.vs_space.classes.CelestialBody;
import org.valkyrienskies.vs_space.classes.CelestialBodys.Planet;
import org.valkyrienskies.vs_space.classes.CelestialBodys.Star;
import org.valkyrienskies.vs_space.client.render.shader.UBO.CelestialBodyDataUBO;
import org.valkyrienskies.vs_space.client.render.shader.VSSpaceShader;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RenderMainline {
    public static List<CelestialBody> RenderCelestialBodyList = new ArrayList<>();
    static {
        RenderCelestialBodyList.add(new Planet("a",new Vector3d(0,0,0), new Quaterniond(), 5));
        Star star = new Star("b",new Vector3d(100,0,0), new Quaterniond(), 20);
        star.setTemperature(6000);
        RenderCelestialBodyList.add(star);
        Star star1 = new Star("c",new Vector3d(100,0,100), new Quaterniond(), 20);
        star1.setTemperature(3000);
        RenderCelestialBodyList.add(star1);
    }

    @SubscribeEvent
    public static void onWorldRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) return;

        CelestialBodyDataUBO celestialBodyDataUBO = new CelestialBodyDataUBO(0); //上传数据
        celestialBodyDataUBO.UpDateCelestialBodyList(new ArrayList<>(RenderCelestialBodyList));
        celestialBodyDataUBO.bindToShader(VSSpaceShader.PlanetRender.get().getId(), "CelestialBodyData");

        for (CelestialBody celestialBody : RenderCelestialBodyList) celestialBody.render(event.getPoseStack(), event.getProjectionMatrix());

        VSSpaceShader.StarLight.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        VSSpaceShader.StarLight.process(event.getPartialTick());
    }
}
