package org.cn_grass_block.space.mixin.server;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.cn_grass_block.space.data.SpaceDataPack;

@Mixin(LivingEntity.class)
public abstract class NoVoidDamage {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void NoVoidDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Level level = ((LivingEntity) (Object) this).level();
        if (source.is(DamageTypes.FELL_OUT_OF_WORLD) && SpaceDataPack.SpaceWorld_ID.contains(level.dimension().location().toString())) cir.setReturnValue(false);
    }
}