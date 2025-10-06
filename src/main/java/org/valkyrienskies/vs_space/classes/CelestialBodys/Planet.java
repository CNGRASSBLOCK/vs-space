package org.valkyrienskies.vs_space.classes.CelestialBodys;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.valkyrienskies.vs_space.classes.CelestialBody;
import org.valkyrienskies.vs_space.client.render.shader.VSSpaceShader;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class Planet extends CelestialBody {
    private double AtmosphericHeight = 10;
    private Vector4d AtmosphericColor = new Vector4d();

    public Planet(String name, Vector3d pos, Quaterniond rotate, double radius, ResourceLocation planet_surface) {
        super(name, pos, rotate, radius);
        this.planet_surface = planet_surface;
        this.planet_surface_night = null;
    }

    public Planet(String name, Vector3d pos, Quaterniond rotate, double radius, ResourceLocation planet_surface, ResourceLocation planet_surface_night) {
        super(name, pos, rotate, radius);
        this.planet_surface = planet_surface;
        this.planet_surface_night = planet_surface_night;
    }

    public void setAtmosphericHeight(double atmospheric_height) { this.AtmosphericHeight = atmospheric_height; }
    public double getAtmosphericHeight() { return this.AtmosphericHeight; }
    public void setAtmosphericColor(Vector4d atmospheric_color) { this.AtmosphericColor = new Vector4d(atmospheric_color); }
    public Vector4d getAtmosphericColor() { return new Vector4d(this.AtmosphericColor); }

    private ResourceLocation planet_surface = new ResourceLocation("vs_space:textures/celestial_body/planet/earth/earth_surface.png");
    private ResourceLocation planet_surface_night = new ResourceLocation("vs_space:textures/celestial_body/planet/earth/earth_surface_night.png");
    @Override
    public void render(PoseStack poseStack, Matrix4f projectionMatrix) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        RenderSystem.enableBlend();
        RenderSystem.setShaderFogStart(Integer.MAX_VALUE);
        RenderSystem.setShaderFogEnd(Integer.MAX_VALUE);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        poseStack.pushPose();
        poseStack.translate(pos.x() - camPos.x(), pos.y() - camPos.y(), pos.z() - camPos.z());
        RenderModel(this.pos, new Vector3d(camPos.x(), camPos.y(), camPos.z()), this.rotate, poseStack.last().pose(), projectionMatrix);
        poseStack.popPose();

        RenderSystem.disableBlend();
    }

    private VertexBuffer vertexBuffer = null;
    private void RenderModel(Vector3d pos, Vector3d camPos, Quaterniond rotate, Matrix4f matrix, Matrix4f projectionMatrix) {
        if (vertexBuffer == null) {
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
            for (int y = -16; y <= 15; y += 1) {
                double y_angle_start = y * (Math.PI / 32);
                double y_angle_end = (y + 1) * (Math.PI / 32);
                for (int xz = 0; xz <= 63; xz += 1) {
                    double xz_angle_start = xz * (Math.PI / 32);
                    double xz_angle_end = (xz + 1) * (Math.PI / 32);
                    bufferBuilder.vertex(Math.sin(xz_angle_start) * Math.cos(y_angle_start) * this.radius, Math.sin(y_angle_start) * this.radius, Math.cos(xz_angle_start) * Math.cos(y_angle_start) * this.radius).color(1f, 1f, 1f, 1f).uv(xz / 64f,(-y + 16) / 32f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal((float) (Math.sin(xz_angle_start) * Math.cos(y_angle_start)), (float) (Math.sin(y_angle_start)), (float) (Math.cos(xz_angle_start) * Math.cos(y_angle_start))).endVertex();
                    bufferBuilder.vertex(Math.sin(xz_angle_end) * Math.cos(y_angle_start) * this.radius, Math.sin(y_angle_start) * this.radius, Math.cos(xz_angle_end) * Math.cos(y_angle_start) * this.radius).color(1f, 1f, 1f, 1f).uv((xz + 1) / 64f, (-y + 16) / 32f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal((float) (Math.sin(xz_angle_end) * Math.cos(y_angle_start)), (float) (Math.sin(y_angle_start)), (float) (Math.cos(xz_angle_end) * Math.cos(y_angle_start))).endVertex();
                    bufferBuilder.vertex(Math.sin(xz_angle_end) * Math.cos(y_angle_end) * this.radius, Math.sin(y_angle_end) * this.radius, Math.cos(xz_angle_end) * Math.cos(y_angle_end) * this.radius).color(1f, 1f, 1f, 1f).uv((xz + 1) / 64f, (-y + 15) / 32f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal((float) (Math.sin(xz_angle_end) * Math.cos(y_angle_end)), (float) (Math.sin(y_angle_end)), (float) (Math.cos(xz_angle_end) * Math.cos(y_angle_end))).endVertex();
                    bufferBuilder.vertex(Math.sin(xz_angle_start) * Math.cos(y_angle_end) * this.radius, Math.sin(y_angle_end) * this.radius, Math.cos(xz_angle_start) * Math.cos(y_angle_end) * this.radius).color(1f, 1f, 1f, 1f).uv(xz / 64f, (-y + 15) / 32f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal((float) (Math.sin(xz_angle_start) * Math.cos(y_angle_end)), (float) (Math.sin(y_angle_end)), (float) (Math.cos(xz_angle_start) * Math.cos(y_angle_end))).endVertex();}
            }
            vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            vertexBuffer.bind();
            vertexBuffer.upload(bufferBuilder.end());
            VertexBuffer.unbind();
        }
        VSSpaceShader.PlanetRender.get().safeGetUniform("PlanetPos").set((float) pos.x(), (float) pos.y(), (float) pos.z());

        RenderSystem.setShaderTexture(0, planet_surface);
        if (planet_surface_night != null) RenderSystem.setShaderTexture(1, planet_surface_night);

        vertexBuffer.bind();
        vertexBuffer.drawWithShader(matrix, projectionMatrix, VSSpaceShader.PlanetRender.get());
        VertexBuffer.unbind();
    }
}
