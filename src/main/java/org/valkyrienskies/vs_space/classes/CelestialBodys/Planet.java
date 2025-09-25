package org.valkyrienskies.vs_space.classes.CelestialBodys;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.valkyrienskies.vs_space.classes.CelestialBody;
import org.valkyrienskies.vs_space.client.render.VSSpaceRenderType;
import org.valkyrienskies.vs_space.client.render.VSSpaceShader;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class Planet extends CelestialBody {
    public Planet(String name, Vector3d pos, Quaterniond rotate, double radius) {
        super(name, pos, rotate, radius);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        RenderSystem.enableBlend();
        RenderSystem.setShaderFogStart(Integer.MAX_VALUE);
        RenderSystem.setShaderFogEnd(Integer.MAX_VALUE);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        BuildModel(this.pos, new Vector3d(camPos.x(), camPos.y(), camPos.z()), this.rotate, buffer, poseStack.last().pose());

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private final List<Vector3d> VertexPos = new ArrayList<>();
    private final List<Vector2f> UVPos = new ArrayList<>();
    private final List<Vector3f> Normal = new ArrayList<>();
    private void BuildModel(Vector3d pos, Vector3d camPos, Quaterniond rotate, MultiBufferSource buffer, Matrix4f matrix) {
        VertexConsumer consumer = buffer.getBuffer(VSSpaceRenderType.PlanetRender(new ResourceLocation("vs_space:textures/celestial_body/planet/earth/earth_surface.png")));

        VSSpaceShader.StarRender.get().safeGetUniform("CameraPos").set((float) camPos.x(), (float) camPos.y(), (float) camPos.z());

        if (VertexPos.isEmpty()) {
            for (int y = -8; y <= 7; y += 1) {
                double y_angle_start = y * (Math.PI / 16);
                double y_angle_end = (y + 1) * (Math.PI / 16);
                for (int xz = 0; xz <= 31; xz += 1) {
                    double xz_angle_start = xz * (Math.PI / 16);
                    double xz_angle_end = (xz + 1) * (Math.PI / 16);
                    VertexPos.add(new Vector3d(Math.sin(xz_angle_start) * Math.cos(y_angle_start) * this.radius,Math.sin(y_angle_start) * this.radius, Math.cos(xz_angle_start) * Math.cos(y_angle_start) * this.radius));
                    UVPos.add(new Vector2f(xz / 32f,(-y + 8) / 16f));
                    Normal.add(new Vector3f((float) (Math.sin(xz_angle_start) * Math.cos(y_angle_start)), (float) (Math.sin(y_angle_start)), (float) (Math.cos(xz_angle_start) * Math.cos(y_angle_start))));

                    VertexPos.add(new Vector3d(Math.sin(xz_angle_end) * Math.cos(y_angle_start) * this.radius, Math.sin(y_angle_start) * this.radius, Math.cos(xz_angle_end) * Math.cos(y_angle_start) * this.radius));
                    UVPos.add(new Vector2f((xz + 1) / 32f, (-y + 8) / 16f));
                    Normal.add(new Vector3f((float) (Math.sin(xz_angle_end) * Math.cos(y_angle_start)), (float) (Math.sin(y_angle_start)), (float) (Math.cos(xz_angle_end) * Math.cos(y_angle_start))));

                    VertexPos.add(new Vector3d(Math.sin(xz_angle_end) * Math.cos(y_angle_end) * this.radius, Math.sin(y_angle_end) * this.radius, Math.cos(xz_angle_end) * Math.cos(y_angle_end) * this.radius));
                    UVPos.add(new Vector2f((xz + 1) / 32f, (-y + 7) / 16f));
                    Normal.add(new Vector3f((float) (Math.sin(xz_angle_end) * Math.cos(y_angle_end)), (float) (Math.sin(y_angle_end)), (float) (Math.cos(xz_angle_end) * Math.cos(y_angle_end))));

                    VertexPos.add(new Vector3d(Math.sin(xz_angle_start) * Math.cos(y_angle_end) * this.radius, Math.sin(y_angle_end) * this.radius, Math.cos(xz_angle_start) * Math.cos(y_angle_end) * this.radius));
                    UVPos.add(new Vector2f(xz / 32f, (-y + 7) / 16f));
                    Normal.add(new Vector3f((float) (Math.sin(xz_angle_start) * Math.cos(y_angle_end)), (float) (Math.sin(y_angle_end)), (float) (Math.cos(xz_angle_start) * Math.cos(y_angle_end))));
                }
            }
        }

        Vector3d this_pos;
        for (int i = 0; i < VertexPos.size() - 3; i += 4) {
            this_pos = new Vector3d(VertexPos.get(i)).rotate(rotate).add(pos).sub(camPos);
            consumer.vertex(matrix, (float) this_pos.x(), (float) this_pos.y(), (float) this_pos.z()).color(1f, 1f, 1f, 1f).uv(UVPos.get(i).x(), UVPos.get(i).y()).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(Normal.get(i).x(), Normal.get(i).y(), Normal.get(i).z()).endVertex();
            this_pos = new Vector3d(VertexPos.get(i + 1)).rotate(rotate).add(pos).sub(camPos);
            consumer.vertex(matrix, (float) this_pos.x(), (float) this_pos.y(), (float) this_pos.z()).color(1f, 1f, 1f, 1f).uv(UVPos.get(i + 1).x(), UVPos.get(i + 1).y()).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(Normal.get(i + 1).x(), Normal.get(i + 1).y(), Normal.get(i + 1).z()).endVertex();
            this_pos = new Vector3d(VertexPos.get(i + 2)).rotate(rotate).add(pos).sub(camPos);
            consumer.vertex(matrix, (float) this_pos.x(), (float) this_pos.y(), (float) this_pos.z()).color(1f, 1f, 1f, 1f).uv(UVPos.get(i + 2).x(), UVPos.get(i + 2).y()).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(Normal.get(i + 2).x(), Normal.get(i + 2).y(), Normal.get(i + 2).z()).endVertex();
            this_pos = new Vector3d(VertexPos.get(i + 3)).rotate(rotate).add(pos).sub(camPos);
            consumer.vertex(matrix, (float) this_pos.x(), (float) this_pos.y(), (float) this_pos.z()).color(1f, 1f, 1f, 1f).uv(UVPos.get(i + 3).x(), UVPos.get(i + 3).y()).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal(Normal.get(i + 3).x(), Normal.get(i + 3).y(), Normal.get(i + 3).z()).endVertex();
        }
    }
}
