package org.embeddedt.embeddium.impl.mixin.core.model.colors;

import it.unimi.dsi.fastutil.objects.*;
import org.embeddedt.embeddium.impl.Embeddium;
import org.embeddedt.embeddium.impl.model.color.interop.BlockColorsExtended;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockColors.class)
public class BlockColorsMixin implements BlockColorsExtended {
    // We're keeping a copy as we need to be able to iterate over the entry pairs, rather than just the values.
    @Unique
    private final Reference2ReferenceMap<Block, BlockColor> blocksToColor = new Reference2ReferenceOpenHashMap<>();

    @Unique
    private final ReferenceSet<Block> overridenBlocks = new ReferenceOpenHashSet<>();

    @Inject(method = "register", at = @At("HEAD"))
    private void preRegisterColorProvider(BlockColor provider, Block[] blocks, CallbackInfo ci) {
        // Happens with Quark. Why??
        if(provider != null) {
            // Synchronize so the inevitable crash mods cause will come from the vanilla map
            synchronized (this.blocksToColor) {
                for (Block block : blocks) {
                    // There will be one provider already registered for vanilla blocks, if we are replacing it,
                    // it means a mod is using custom logic and we need to disable per-vertex coloring
                    if (this.blocksToColor.put(block, provider) != null) {
                        this.overridenBlocks.add(block);
                        Embeddium.logger().info("Block {} had its color provider replaced and will not use per-vertex coloring", BuiltInRegistries.BLOCK.getKey(block));
                    }
                }
            }
        }
    }

    @Override
    public Reference2ReferenceMap<Block, BlockColor> sodium$getProviders() {
        return Reference2ReferenceMaps.unmodifiable(this.blocksToColor);
    }

    @Override
    public ReferenceSet<Block> sodium$getOverridenVanillaBlocks() {
        return ReferenceSets.unmodifiable(this.overridenBlocks);
    }
}
