package me.jellysquid.mods.sodium.mixin.features.world_ticking;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(ClientLevel.class)
public class MixinClientLevel {
    @Shadow
    private void lambda$doAnimateTick$8(BlockPos.MutableBlockPos pos, AmbientParticleSettings settings) {
        throw new AssertionError();
    }

    /**
     * @author embeddedt
     * @reason Avoid allocating a capturing lambda for each ticked block position. The original allocated method arg
     * is discarded by the JIT.
     */
    @Redirect(method = "doAnimateTick", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
    private void addBiomeParticleWithoutAlloc(Optional<AmbientParticleSettings> particleSettings, Consumer<AmbientParticleSettings> allocatedLambda, int p_233613_, int p_233614_, int p_233615_, int p_233616_, RandomSource p_233617_, @Nullable Block p_233618_, BlockPos.MutableBlockPos p_233619_) {
        //noinspection OptionalIsPresent
        if(particleSettings.isPresent()) {
            lambda$doAnimateTick$8(p_233619_, particleSettings.get());
        }
    }
}