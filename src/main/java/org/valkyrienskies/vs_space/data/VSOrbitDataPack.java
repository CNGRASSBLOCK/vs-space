package org.valkyrienskies.vs_space.data;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "vs_orbit")
public class VSOrbitDataPack {
    public static List<String> SpaceWorld_ID = new ArrayList<>();
    
    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Object>() {
            @Override protected Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) { return new Object(); }

            @Override
            protected void apply(Object object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                Map<ResourceLocation, Resource> resources = resourceManager.listResources("space_data", path -> true;
                for (ResourceLocation resourceLocation : resources.keySet()) SpaceWorld_ID.add(resourceLocation.getPath());
            }
        });
    }
}
