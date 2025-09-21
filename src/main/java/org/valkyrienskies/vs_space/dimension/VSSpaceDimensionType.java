package org.valkyrienskies.vs_space.dimension;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.stringtemplate.v4.ST;
import org.valkyrienskies.vs_space.data.VSSpaceDataPack;

import java.util.*;

public class VSSpaceDimensionType {
    public static final DeferredRegister<DimensionType> DimensionTypes = DeferredRegister.create(Registries.DIMENSION_TYPE, "vs_space");
    public static final Map<String, DimensionType> DimensionTypeList = new HashMap();

    public static void register(IEventBus modEventBus) {
        VSSpaceDataPack.SpaceWorld_ID.add("aaa");
        VSSpaceDataPack.SpaceWorld_ID.add("bbb");
        VSSpaceDataPack.SpaceWorld_ID.add("ccc");
        for (String worldId : VSSpaceDataPack.SpaceWorld_ID) {
            final DimensionType SpaceDimensionType =
                    new DimensionType(
                        OptionalLong.of(6000L),//时间
                        false,//天空光照
                        false,//天花板
                        false,//超温
                        true,//是否自然
                        1.0,//坐标缩放
                        false,//床能不能用
                        false,//重生锚能不能用
                        0,//最小Y
                        256,//最大Y
                        256,//最大逻辑Y
                        TagKey.create(Registries.BLOCK, new ResourceLocation("minecraft", "infiniburn_overworld")),
                        new ResourceLocation("minecraft", "overworld"),
                        0,
                        new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 1), 0)
                    );

            DimensionTypeList.put(worldId, SpaceDimensionType);
        }

        for (String worldId : DimensionTypeList.keySet()) {
            DimensionTypes.register(worldId, () -> DimensionTypeList.get(worldId));
        }

        DimensionTypes.register(modEventBus);
    }
}
