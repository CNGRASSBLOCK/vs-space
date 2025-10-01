package org.valkyrienskies.vs_space.client.render.shader.UBO;

import org.lwjgl.BufferUtils;
import org.valkyrienskies.vs_space.classes.CelestialBody;
import org.valkyrienskies.vs_space.classes.CelestialBodys.Planet;
import org.valkyrienskies.vs_space.classes.CelestialBodys.Star;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class CelestialBodyDataUBO extends UniformBufferObject {
    public CelestialBodyDataUBO(int bindingPoint) {
        super(bindingPoint, 2592);
        //star: int + 16 * (vec3 + vec4) + planet: int + 64 * (vec3 + float)
    }

    public void UpDateCelestialBodyList(List<CelestialBody> celestialBodyList) {
        List<Star> StarList = new ArrayList<>();
        for (CelestialBody celestialBody : celestialBodyList) if (celestialBody instanceof Star star) StarList.add(star);
        if (StarList.size() > 16) StarList.subList(16, StarList.size()).clear();
        List<Planet> PlanetList = new ArrayList<>();
        for (CelestialBody celestialBody : celestialBodyList) if (celestialBody instanceof Planet planet) PlanetList.add(planet);
        if (PlanetList.size() > 64) PlanetList.subList(64, PlanetList.size()).clear();

        ByteBuffer buffer = BufferUtils.createByteBuffer(getSize());

        //Star的
        buffer.putInt(StarList.size());
        buffer.putInt(0).putInt(0).putInt(0); //补齐int
        for (Star star : StarList) {
            buffer.putFloat((float) star.getPos().x());
            buffer.putFloat((float) star.getPos().y());
            buffer.putFloat((float) star.getPos().z());
            buffer.putFloat(0f); //补齐Pos
            buffer.putFloat(star.RGB[0]);
            buffer.putFloat(star.RGB[1]);
            buffer.putFloat(star.RGB[2]);
            buffer.putFloat(1f); //补齐Color
        }
        for (int i = 0; i < 16 - StarList.size(); i++) { //补齐列表的
            buffer.putFloat(0f);
            buffer.putFloat(0f);
            buffer.putFloat(0f);
            buffer.putFloat(0f);
            buffer.putFloat(0f);
            buffer.putFloat(0f);
            buffer.putFloat(0f);
            buffer.putFloat(0f);
        }

        //Planet的
        buffer.putInt(PlanetList.size());
        buffer.putInt(0).putInt(0).putInt(0); //补齐int
        for (Planet planet : PlanetList) {
            buffer.putFloat((float) planet.getPos().x());
            buffer.putFloat((float) planet.getPos().y());
            buffer.putFloat((float) planet.getPos().z());
            buffer.putFloat(0f); //补齐Pos
            buffer.putFloat((float) planet.getRadius());
            buffer.putFloat(0f); //补齐R
            buffer.putFloat(0f); //补齐R
            buffer.putFloat(0f); //补齐R
        }
        for (int i = 0; i < 64 - PlanetList.size(); i++) { //补齐列表的
            buffer.putFloat(0f);
            buffer.putFloat(0f);
            buffer.putFloat(0f);
            buffer.putFloat(0f);
            buffer.putFloat(0f);
            buffer.putFloat(0f);
            buffer.putFloat(0f);
            buffer.putFloat(0f);
        }

        buffer.flip();
        update(buffer);
    }
}

