package org.valkyrienskies.vs_space.client;

import org.valkyrienskies.vs_space.classes.CelestialBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientCelestialBodyPool {
    public static Map<String, CelestialBody> CelestialBodyPool = new HashMap<>();

    public static List<String> GetSpaceWorld() { return CelestialBodyPool.keySet().stream().toList(); }
}
