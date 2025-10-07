package org.cn_grass_block.space.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation("vs_orbit", "main"), () -> "1.0", "1.0"::equals, "1.0"::equals);

    public static void register() {
        //INSTANCE.registerMessage(0, SyncCelestialBodyPacket.class, SyncCelestialBodyPacket::encode, SyncCelestialBodyPacket::decode, SyncCelestialBodyPacket::handle);
    }
}
