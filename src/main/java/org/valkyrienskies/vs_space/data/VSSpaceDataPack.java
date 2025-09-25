package org.valkyrienskies.vs_space.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.vs_space.VSSpace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = VSSpace.MODID)
public class VSSpaceDataPack {
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
                    String WorldID = VSSpace.MODID + ":" + resourceLocation.getPath().split("/")[1];
                    if (!SpaceWorld_ID.contains(WorldID)) SpaceWorld_ID.add(WorldID);
                }
            }
        });
    }
}
