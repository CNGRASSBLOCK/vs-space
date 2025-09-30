package org.valkyrienskies.vs_space.classes.CelestialBodys;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.valkyrienskies.vs_space.classes.CelestialBody;

public class BlackHole extends CelestialBody {
    public BlackHole(String name, Vector3d pos, Quaterniond rotate, double radius) {
        super(name, pos, rotate, radius);
    }
}
