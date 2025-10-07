package org.cn_grass_block.space.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.*;
import org.cn_grass_block.space.classes.CelestialBody;
import org.cn_grass_block.space.client.ClientCelestialBodyPool;
import org.cn_grass_block.space.client.render.shader.UBO.CelestialBodyDataUBO;
import org.cn_grass_block.space.client.render.shader.SpaceShader;
import org.cn_grass_block.space.data.SpaceDataPack;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RenderMainline {
    public static List<CelestialBody> RenderCelestialBodyList = new ArrayList<>();
    public static CelestialBodyDataUBO celestialBodyDataUBO = null;

    @SubscribeEvent
    public static void onWorldRender(RenderLevelStageEvent event) {
        if (!SpaceDataPack.SpaceWorld_ID.contains(Minecraft.getInstance().level.dimension().location().toString())) return;
        if (celestialBodyDataUBO == null) celestialBodyDataUBO = new CelestialBodyDataUBO(0);
        celestialBodyDataUBO.UpDateCelestialBodyList(new ArrayList<>(RenderCelestialBodyList));

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            try {
                RenderCelestialBodyList = ClientCelestialBodyPool.GetSpaceWorld("space:solar_system");
            } catch (NullPointerException a) {}
            RenderSkyBox(event.getPoseStack());
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            celestialBodyDataUBO.bindToShader(SpaceShader.PlanetRender.get().getId(), "CelestialBodyData");
            for (Object celestialBody : RenderCelestialBodyList)
                if (celestialBody instanceof CelestialBody celestialBody1)
                    celestialBody1.render(event.getPoseStack(), event.getProjectionMatrix());

            ProjectionMatrix.set(RenderSystem.getProjectionMatrix());
            ModelViewMatrix.set(RenderSystem.getModelViewMatrix());
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            RenderEffect(event.getPartialTick());
        }
    }

    private static ResourceLocation sky_box_text = new ResourceLocation("space:textures/sky_box/space_skybox.png");
    private static void RenderSkyBox(PoseStack poseStack) {
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderTexture(0, sky_box_text);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf(0.514589197008, 0.856291832413, 0.053894143178, 0.000000000000));

        for(int i = 0; i < 6; ++i) {
            poseStack.pushPose();
            if (i == 1) poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            if (i == 2) poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            if (i == 3) poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            if (i == 4) poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            if (i == 5) poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));

            Matrix4f matrix4f = poseStack.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).uv(i / 6f, 0.0F).color(255, 255, 255, 255).endVertex();
            bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).uv(i / 6f, 1.0F).color(255, 255, 255, 255).endVertex();
            bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).uv((i + 1) / 6f, 1.0F).color(255, 255, 255, 255).endVertex();
            bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).uv((i + 1) / 6f, 0.0F).color(255, 255, 255, 255).endVertex();
            tesselator.end();
            poseStack.popPose();
        }

        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static final Matrix4f ProjectionMatrix = new Matrix4f();
    private static final Matrix4f ModelViewMatrix = new Matrix4f();
    private static Vector2i ScreenSize = new Vector2i();
    private static void RenderEffect(float PartialTick) {
        if (celestialBodyDataUBO == null) return;

        Matrix4f ProjMat = ProjectionMatrix;
        Matrix4f ModelViewMat = ModelViewMatrix;
        Vec3 CameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        ModelViewMat.rotateY((float) Math.toRadians(Minecraft.getInstance().gameRenderer.getMainCamera().getYRot() + 180));
        ModelViewMat.rotateLocalX((float) Math.toRadians(Minecraft.getInstance().gameRenderer.getMainCamera().getXRot()));
        ModelViewMat.translate((float) -CameraPos.x(), (float) -CameraPos.y(), (float) -CameraPos.z());



        PostPass StarBloomPostPass = SpaceShader.StarBloom.passes.get(0);
        StarBloomPostPass.addAuxAsset("depth", () -> Minecraft.getInstance().getMainRenderTarget().getDepthTextureId(), ScreenSize.x(), ScreenSize.y());
        EffectInstance StarBloomEffect = StarBloomPostPass.getEffect();
        StarBloomEffect.apply();
        StarBloomEffect.getUniform("iProjMat").set(new Matrix4f(ProjMat).invert());
        StarBloomEffect.getUniform("iModelViewMat").set(new Matrix4f(ModelViewMat).invert());
        StarBloomEffect.getUniform("CameraPos").set((float) CameraPos.x(), (float) CameraPos.y(), (float) CameraPos.z());
        celestialBodyDataUBO.bindToShader(StarBloomEffect.getId(), "CelestialBodyData");

        PostPass PlanetAtmospherePostPass = SpaceShader.PlanetAtmosphere.passes.get(0);
        PlanetAtmospherePostPass.addAuxAsset("depth", () -> Minecraft.getInstance().getMainRenderTarget().getDepthTextureId(), ScreenSize.x(), ScreenSize.y());
        EffectInstance PlanetAtmosphereEffect = PlanetAtmospherePostPass.getEffect();
        PlanetAtmosphereEffect.apply();
        PlanetAtmosphereEffect.getUniform("iProjMat").set(new Matrix4f(ProjMat).invert());
        PlanetAtmosphereEffect.getUniform("iModelViewMat").set(new Matrix4f(ModelViewMat).invert());
        PlanetAtmosphereEffect.getUniform("CameraPos").set((float) CameraPos.x(), (float) CameraPos.y(), (float) CameraPos.z());
        celestialBodyDataUBO.bindToShader(PlanetAtmosphereEffect.getId(), "CelestialBodyData");

        Vector2i ThisScreenSize = new Vector2i(Minecraft.getInstance().getWindow().getScreenWidth(), Minecraft.getInstance().getWindow().getScreenHeight());
        if (!ThisScreenSize.equals(ScreenSize) && ThisScreenSize.x + ThisScreenSize.y != 0) {
            ScreenSize = ThisScreenSize;
            SpaceShader.StarBloom.resize(ScreenSize.x(), ScreenSize.y());
            SpaceShader.PlanetAtmosphere.resize(ScreenSize.x(), ScreenSize.y());
        }
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();
        SpaceShader.StarBloom.process(PartialTick);
        SpaceShader.PlanetAtmosphere.process(PartialTick);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }
}