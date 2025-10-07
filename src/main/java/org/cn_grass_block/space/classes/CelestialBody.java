package org.cn_grass_block.space.classes;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class CelestialBody {
    protected final String name;
    protected final Vector3d pos;
    protected final Quaterniond rotate;
    protected double radius;

    public CelestialBody(String name, Vector3d pos, Quaterniond rotate, double radius) {
        this.name = name;
        this.pos = new Vector3d(pos);
        this.rotate = new Quaterniond(rotate);
        this.radius = radius;
    }

    public String getName() { return name; }
    public Vector3d getPos() { return new Vector3d(pos); }
    public Quaterniond getRotate() { return new Quaterniond(rotate); }
    public double getRadius() { return radius; }
    public void moveTo(Vector3d vector3d) { this.pos.set(new Vector3d(vector3d)); }
    public void rotateTo(Quaterniond rotate) { this.rotate.set(new Quaterniond(rotate)); }
    public void setRadius(double radius) { this.radius = radius; }

    public void render(PoseStack poseStack, Matrix4f projectionMatrix) {}
}
