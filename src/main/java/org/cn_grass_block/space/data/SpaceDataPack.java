package org.cn_grass_block.space.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.cn_grass_block.space.Space;
import org.cn_grass_block.space.classes.CelestialBodys.Planet;
import org.cn_grass_block.space.classes.CelestialBodys.Star;
import org.cn_grass_block.space.client.ClientCelestialBodyPool;
import org.cn_grass_block.space.server.ServerCelestialBodyPool;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Space.MODID)
public class SpaceDataPack {
    public static List<String> SpaceWorld_ID = new ArrayList<>();

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<>() {
            @Override protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) { return new Object(); }

            @Override
            protected void apply(Object object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                SpaceWorld_ID.clear();

                Map<ResourceLocation, Resource> resources = resourceManager.listResources("space_data", path -> true);
                for (ResourceLocation resourceLocation : resources.keySet()) {
                    String WorldID = Space.MODID + ":" + resourceLocation.getPath().split("/")[1];
                    if (!SpaceWorld_ID.contains(WorldID)) { SpaceWorld_ID.add(WorldID); }

                    JsonObject jsonObject = loadJson(resourceManager, resourceLocation);

                    String[] PathList = resourceLocation.getPath().split("/");
                    String name = PathList[PathList.length - 1].replace(".json", "");

                    if (name.equals("type")) continue;

                    Vector3d pos = new Vector3d();
                    List<JsonElement> pos_list = jsonObject.getAsJsonArray("pos").asList();
                    if (pos_list.size() == 3) pos = new Vector3d(pos_list.get(0).getAsDouble(), pos_list.get(1).getAsDouble(), pos_list.get(2).getAsDouble());
                    Quaterniond rotate = new Quaterniond();
                    List<JsonElement> rotate_list = jsonObject.getAsJsonArray("rotate").asList();
                    if (pos_list.size() == 4) rotate = new Quaterniond(rotate_list.get(0).getAsDouble(), rotate_list.get(1).getAsDouble(), rotate_list.get(2).getAsDouble(), rotate_list.get(3).getAsDouble());
                    double radius = jsonObject.get("scale").getAsDouble();

                    String type = jsonObject.get("type").getAsString();
                    if (type.equals("star")) {
                        double temperature = jsonObject.get("temperature").getAsDouble();

                        Star star = new Star(name, pos, rotate, radius, temperature);
                        ClientCelestialBodyPool.PutCelestialBody(WorldID, star);
                        ServerCelestialBodyPool.PutCelestialBody(WorldID, star);
                    } else if (type.equals("planet")) {
                        Planet planet;
                        if (jsonObject.has("night_texture"))
                            planet = new Planet(name, pos, rotate, radius, new ResourceLocation(jsonObject.get("texture").getAsString()), new ResourceLocation(jsonObject.get("night_texture").getAsString()));
                        else
                            planet = new Planet(name, pos, rotate, radius, new ResourceLocation(jsonObject.get("texture").getAsString()));

                        ClientCelestialBodyPool.PutCelestialBody(WorldID, planet);
                        ServerCelestialBodyPool.PutCelestialBody(WorldID, planet);
                    }
                }
            }
        });
    }

    @Nullable
    public static JsonObject loadJson(ResourceManager resourceManager, ResourceLocation loc) {
        try {
            try (InputStream inputStream = resourceManager.getResource(loc).get().open()) { return JsonParser.parseReader(new InputStreamReader(inputStream)).getAsJsonObject();}
        } catch (IOException ignored) {
            return null;
        }
    }
}
