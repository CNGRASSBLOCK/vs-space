package org.valkyrienskies.vs_space.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.KHRDebug;
import org.stringtemplate.v4.ST;
import org.valkyrienskies.vs_space.classes.CelestialBody;
import org.valkyrienskies.vs_space.classes.CelestialBodys.Planet;
import org.valkyrienskies.vs_space.classes.CelestialBodys.Star;
import org.valkyrienskies.vs_space.client.render.shader.UBO.CelestialBodyDataUBO;
import org.valkyrienskies.vs_space.client.render.shader.VSSpaceShader;
import org.valkyrienskies.vs_space.data.VSSpaceDataPack;

import java.lang.Math;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RenderMainline {
    public static List<CelestialBody> RenderCelestialBodyList = new ArrayList<>();
    public static CelestialBodyDataUBO celestialBodyDataUBO = null;

    static {
        RenderCelestialBodyList.add(new Planet("a", new Vector3d(0, 100, 0), new Quaterniond(), 50));
        Star star = new Star("b", new Vector3d(1000, 900, 0), new Quaterniond(), 400);
        star.setTemperature(12000);
        RenderCelestialBodyList.add(star);
        Star star1 = new Star("c", new Vector3d(-1000, 300, 0), new Quaterniond(), 100);
        star1.setTemperature(3000);
        RenderCelestialBodyList.add(star1);
    }

    @SubscribeEvent
    public static void onWorldRender(RenderLevelStageEvent event) {
        if (!VSSpaceDataPack.SpaceWorld_ID.contains(Minecraft.getInstance().level.dimension().location().toString())) return;
        if (celestialBodyDataUBO == null) {
            celestialBodyDataUBO = new CelestialBodyDataUBO(0);
            celestialBodyDataUBO.UpDateCelestialBodyList(new ArrayList<>(RenderCelestialBodyList));
        }

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            RenderSkyBox(event.getPoseStack());
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            celestialBodyDataUBO.bindToShader(VSSpaceShader.PlanetRender.get().getId(), "CelestialBodyData");
            for (CelestialBody celestialBody : RenderCelestialBodyList) celestialBody.render(event.getPoseStack(), event.getProjectionMatrix());

            ProjectionMatrix.set(RenderSystem.getProjectionMatrix());
            ModelViewMatrix.set(RenderSystem.getModelViewMatrix());
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            RenderEffect(event.getPartialTick());
        }
    }

    private static void RenderSkyBox(PoseStack poseStack) {
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderTexture(0, new ResourceLocation("vs_space:textures/sky_box/space_skybox.png"));
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

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

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static final Matrix4f ProjectionMatrix = new Matrix4f();
    private static final Matrix4f ModelViewMatrix = new Matrix4f();
    private static void RenderEffect(float PartialTick) {
        if (celestialBodyDataUBO == null) return;

        Matrix4f ProjMat = new Matrix4f(ProjectionMatrix);
        Matrix4f ModelViewMat = new Matrix4f(ModelViewMatrix);
        Vec3 CameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        ModelViewMat.rotateY((float) Math.toRadians(Minecraft.getInstance().gameRenderer.getMainCamera().getYRot() + 180));
        ModelViewMat.rotateLocalX((float) Math.toRadians(Minecraft.getInstance().gameRenderer.getMainCamera().getXRot()));
        ModelViewMat.translate((float) -CameraPos.x(), (float) -CameraPos.y(), (float) -CameraPos.z());

        PostPass postPass = VSSpaceShader.StarBloom.passes.get(0);
        postPass.addAuxAsset("depth", () -> Minecraft.getInstance().getMainRenderTarget().getDepthTextureId(), Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        EffectInstance effect = postPass.getEffect();
        effect.getUniform("iProjMat").set(new Matrix4f(ProjMat).invert());
        effect.getUniform("iModelViewMat").set(new Matrix4f(ModelViewMat).invert());
        effect.getUniform("CameraPos").set((float) CameraPos.x(), (float) CameraPos.y(), (float) CameraPos.z());
        celestialBodyDataUBO.bindToShader(effect.getId(), "CelestialBodyData");
        Vector2i ScreenSize = new Vector2i(Minecraft.getInstance().getWindow().getScreenWidth(), Minecraft.getInstance().getWindow().getScreenHeight());
        if (ScreenSize.x() == 0 || ScreenSize.y() == 0) ScreenSize = new Vector2i(1,1);
        VSSpaceShader.StarBloom.resize(ScreenSize.x(), ScreenSize.y());
        VSSpaceShader.StarBloom.process(PartialTick);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }
}