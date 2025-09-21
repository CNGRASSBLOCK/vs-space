package org.valkyrienskies.vs_space.classes.CelestialBodys;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
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
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.valkyrienskies.vs_space.classes.CelestialBody;

import java.lang.Math;

public class Star extends CelestialBody{
    protected double temperature = 0; //开尔文
    private final float[] RGB = new float[]{1f, 1f, 1f};


    public Star(String name, Vector3d pos, Quaterniond rotate, double radius) {
        super(name, pos, rotate, radius);
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
        int[] IRGB = TemperatureToRGB(temperature);
        RGB[0] = IRGB[0] / 255f;
        RGB[1] = IRGB[1] / 255f;
        RGB[2] = IRGB[2] / 255f;
    }
    public double getTemperature() { return temperature; }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        poseStack.pushPose();
        poseStack.translate(this.pos.x() - camPos.x, this.pos.y() - camPos.y, this.pos.z() - camPos.z);

        BuildModel(buffer, poseStack.last().pose());

        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private void BuildModel(MultiBufferSource buffer, Matrix4f matrix) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentCull(new ResourceLocation("vs_space:textures/celestial_body/star/star.png")));
        for (int y = -16; y <= 15; y += 1) {
            float y_angle_start = y * (float) (Math.PI / 16);
            float y_angle_end = (y + 1) * (float) (Math.PI / 16);
            for (int xz = 0; xz <= 63; xz += 1) {
                float xz_angle_start = xz * (float) (Math.PI / 16);
                float xz_angle_end = (xz + 1) * (float) (Math.PI / 16);
                consumer.vertex(matrix, (float) (Math.sin(xz_angle_start) * Math.cos(y_angle_start) * this.radius), (float) (Math.sin(y_angle_start) * this.radius), (float) (Math.cos(xz_angle_start) * Math.cos(y_angle_start) * this.radius)).color(this.RGB[0], this.RGB[1], this.RGB[2], 1f).uv(xz / 64f, y / 32f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal((float) (Math.sin(xz_angle_start) * Math.cos(y_angle_start)), (float) (Math.sin(y_angle_start)), (float) (Math.cos(xz_angle_start) * Math.cos(y_angle_start))).endVertex();
                consumer.vertex(matrix, (float) (Math.sin(xz_angle_end) * Math.cos(y_angle_start) * this.radius), (float) (Math.sin(y_angle_start) * this.radius), (float) (Math.cos(xz_angle_end) * Math.cos(y_angle_start) * this.radius)).color(this.RGB[0], this.RGB[1], this.RGB[2], 1f).uv(xz / 64f, (y + 1) / 32f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal((float) (Math.sin(xz_angle_end) * Math.cos(y_angle_start)), (float) (Math.sin(y_angle_start)), (float) (Math.cos(xz_angle_end) * Math.cos(y_angle_start))).endVertex();
                consumer.vertex(matrix, (float) (Math.sin(xz_angle_end) * Math.cos(y_angle_end) * this.radius), (float) (Math.sin(y_angle_end) * this.radius), (float) (Math.cos(xz_angle_end) * Math.cos(y_angle_end) * this.radius)).color(this.RGB[0], this.RGB[1], this.RGB[2], 1f).uv((xz + 1) / 64f, (y + 1) / 32f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal((float) (Math.sin(xz_angle_end) * Math.cos(y_angle_end)), (float) (Math.sin(y_angle_end)), (float) (Math.cos(xz_angle_end) * Math.cos(y_angle_end))).endVertex();
                consumer.vertex(matrix, (float) (Math.sin(xz_angle_start) * Math.cos(y_angle_end) * this.radius), (float) (Math.sin(y_angle_end) * this.radius), (float) (Math.cos(xz_angle_start) * Math.cos(y_angle_end) * this.radius)).color(this.RGB[0], this.RGB[1], this.RGB[2], 1f).uv((xz + 1) / 64f, y / 32f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xF000F0).normal((float) (Math.sin(xz_angle_start) * Math.cos(y_angle_end)), (float) (Math.sin(y_angle_end)), (float) (Math.cos(xz_angle_start) * Math.cos(y_angle_end))).endVertex();
            }
        }
    }

    private static int[] TemperatureToRGB(double kelvin) {
        kelvin = Math.max(1000, Math.min(40000, kelvin));
        kelvin /= 100;

        double red, green, blue;

        if (kelvin <= 66) {
            red = 255;
        } else {
            red = 329.698727446 * Math.pow(kelvin - 60, -0.1332047592);
            red = Math.max(0, Math.min(255, red));
        }

        if (kelvin <= 66) {
            green = 99.4708025861 * Math.log(kelvin) - 161.1195681661;
        } else {
            green = 288.1221695283 * Math.pow(kelvin - 60, -0.0755148492);
        }
        green = Math.max(0, Math.min(255, green));

        if (kelvin >= 66) {
            blue = 255;
        } else if (kelvin <= 19) {
            blue = 0;
        } else {
            blue = 138.5177312231 * Math.log(kelvin - 10) - 305.0447927307;
            blue = Math.max(0, Math.min(255, blue));
        }

        return new int[] {(int) red, (int) green, (int) blue};
    }
}
