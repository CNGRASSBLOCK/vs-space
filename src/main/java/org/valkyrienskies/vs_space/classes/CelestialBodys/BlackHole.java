package org.valkyrienskies.vs_space.classes.CelestialBodys;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;
import org.valkyrienskies.vs_space.classes.CelestialBody;
import org.valkyrienskies.vs_space.client.render.shader.VSSpaceShader;

public class BlackHole extends CelestialBody {
    public BlackHole(String name, Vector3d pos, Quaterniond rotate, double radius) {
        super(name, pos, rotate, radius);
    }

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

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private VertexBuffer vertexBuffer = null;
    private void RenderModel(Vector3d pos, Vector3d camPos, Quaterniond rotate, Matrix4f matrix, Matrix4f projectionMatrix) {
        if (vertexBuffer == null) {
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
            for (int y = -8; y <= 7; y += 1) {
                double y_angle_start = y * (Math.PI / 16);
                double y_angle_end = (y + 1) * (Math.PI / 16);
                for (int xz = 0; xz <= 31; xz += 1) {
                    double xz_angle_start = xz * (Math.PI / 16);
                    double xz_angle_end = (xz + 1) * (Math.PI / 16);
                    bufferBuilder.vertex((float) (Math.sin(xz_angle_start) * Math.cos(y_angle_start) * this.radius), (float) (Math.sin(y_angle_start) * this.radius), (float) (Math.cos(xz_angle_start) * Math.cos(y_angle_start) * this.radius)).color(1f, 1f, 1f, 1f).uv(xz / 32f,(-y + 8) / 16f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal((float) (Math.sin(xz_angle_start) * Math.cos(y_angle_start)), (float) (Math.sin(y_angle_start)), (float) (Math.cos(xz_angle_start) * Math.cos(y_angle_start))).endVertex();
                    bufferBuilder.vertex((float) (Math.sin(xz_angle_end) * Math.cos(y_angle_start) * this.radius), (float) (Math.sin(y_angle_start) * this.radius), (float) (Math.cos(xz_angle_end) * Math.cos(y_angle_start) * this.radius)).color(1f, 1f, 1f, 1f).uv((xz + 1) / 32f, (-y + 8) / 16f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal((float) (Math.sin(xz_angle_end) * Math.cos(y_angle_start)), (float) (Math.sin(y_angle_start)), (float) (Math.cos(xz_angle_end) * Math.cos(y_angle_start))).endVertex();
                    bufferBuilder.vertex((float) (Math.sin(xz_angle_end) * Math.cos(y_angle_end) * this.radius), (float) (Math.sin(y_angle_end) * this.radius), (float) (Math.cos(xz_angle_end) * Math.cos(y_angle_end) * this.radius)).color(1f, 1f, 1f, 1f).uv((xz + 1) / 32f, (-y + 7) / 16f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal((float) (Math.sin(xz_angle_end) * Math.cos(y_angle_end)), (float) (Math.sin(y_angle_end)), (float) (Math.cos(xz_angle_end) * Math.cos(y_angle_end))).endVertex();
                    bufferBuilder.vertex((float) (Math.sin(xz_angle_start) * Math.cos(y_angle_end) * this.radius), (float) (Math.sin(y_angle_end) * this.radius), (float) (Math.cos(xz_angle_start) * Math.cos(y_angle_end) * this.radius)).color(1f, 1f, 1f, 1f).uv(xz / 32f, (-y + 7) / 16f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal((float) (Math.sin(xz_angle_start) * Math.cos(y_angle_end)), (float) (Math.sin(y_angle_end)), (float) (Math.cos(xz_angle_start) * Math.cos(y_angle_end))).endVertex();}
            }
            vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            vertexBuffer.bind();
            vertexBuffer.upload(bufferBuilder.end());
            VertexBuffer.unbind();
        }
        vertexBuffer.bind();
        vertexBuffer.drawWithShader(matrix, projectionMatrix, VSSpaceShader.StarRender.get());
        VertexBuffer.unbind();
    }
}
