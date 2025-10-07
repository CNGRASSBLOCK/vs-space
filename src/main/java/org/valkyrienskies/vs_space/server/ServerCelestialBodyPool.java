package org.valkyrienskies.vs_space.server;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.apache.logging.log4j.core.jmx.Server;
import org.valkyrienskies.vs_space.classes.CelestialBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerCelestialBodyPool {
    private static final Map<String, List<CelestialBody>> CelestialBodyPool = new HashMap<>();

    public static List<String> GetSpaceWorld() { return CelestialBodyPool.keySet().stream().toList(); }

    public static void PutCelestialBody(ServerLevel world, CelestialBody celestialBody) { PutCelestialBody(world.dimension().location().toString(), celestialBody); }
    public static void PutCelestialBody(String WorldID, CelestialBody celestialBody) { if (CelestialBodyPool.containsKey(WorldID)) CelestialBodyPool.get(WorldID).add(celestialBody); }
    public static void RemoveCelestialBody(ServerLevel world, CelestialBody celestialBody) { RemoveCelestialBody(world.dimension().location().toString(), celestialBody); }
    public static void RemoveCelestialBody(String WorldID, CelestialBody celestialBody) { if (CelestialBodyPool.containsKey(WorldID)) CelestialBodyPool.get(WorldID).remove(celestialBody); }
}
