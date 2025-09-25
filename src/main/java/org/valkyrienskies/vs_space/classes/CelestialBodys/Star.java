package org.valkyrienskies.vs_space.classes.CelestialBodys;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
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
    private void BuildModel(Vector3d pos, Vector3d camPos, Quaterniond rotate, MultiBufferSource buffer, Matrix4f matrix) {
        VertexConsumer consumer = buffer.getBuffer(VSSpaceRenderType.StarRender());

        VSSpaceShader.StarRender.get().safeGetUniform("StarColor").set(RGB[0], RGB[1], RGB[2], 1f);

        if (VertexPos.isEmpty()) {
            for (int y = -8; y <= 7; y += 1) {
                double y_angle_start = y * (Math.PI / 16);
                double y_angle_end = (y + 1) * (Math.PI / 16);
                for (int xz = 0; xz <= 31; xz += 1) {
                    double xz_angle_start = xz * (Math.PI / 16);
                    double xz_angle_end = (xz + 1) * (Math.PI / 16);
                    VertexPos.add(new Vector3d(Math.sin(xz_angle_start) * Math.cos(y_angle_start) * this.radius,Math.sin(y_angle_start) * this.radius, Math.cos(xz_angle_start) * Math.cos(y_angle_start) * this.radius));
                    VertexPos.add(new Vector3d(Math.sin(xz_angle_end) * Math.cos(y_angle_start) * this.radius, Math.sin(y_angle_start) * this.radius, Math.cos(xz_angle_end) * Math.cos(y_angle_start) * this.radius));
                    VertexPos.add(new Vector3d(Math.sin(xz_angle_end) * Math.cos(y_angle_end) * this.radius, Math.sin(y_angle_end) * this.radius, Math.cos(xz_angle_end) * Math.cos(y_angle_end) * this.radius));
                    VertexPos.add(new Vector3d(Math.sin(xz_angle_start) * Math.cos(y_angle_end) * this.radius, Math.sin(y_angle_end) * this.radius, Math.cos(xz_angle_start) * Math.cos(y_angle_end) * this.radius));
                }
            }
        }

        Vector3d this_pos;
        for (int i = 0; i < VertexPos.size() - 4; i += 4) {
            this_pos = new Vector3d(VertexPos.get(i)).rotate(rotate).add(pos).sub(camPos);
            consumer.vertex(matrix, (float) this_pos.x(), (float) this_pos.y(), (float) this_pos.z()).endVertex();
            this_pos = new Vector3d(VertexPos.get(i + 1)).rotate(rotate).add(pos).sub(camPos);
            consumer.vertex(matrix, (float) this_pos.x(), (float) this_pos.y(), (float) this_pos.z()).endVertex();
            this_pos = new Vector3d(VertexPos.get(i + 2)).rotate(rotate).add(pos).sub(camPos);
            consumer.vertex(matrix, (float) this_pos.x(), (float) this_pos.y(), (float) this_pos.z()).endVertex();
            this_pos = new Vector3d(VertexPos.get(i + 3)).rotate(rotate).add(pos).sub(camPos);
            consumer.vertex(matrix, (float) this_pos.x(), (float) this_pos.y(), (float) this_pos.z()).endVertex();
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
