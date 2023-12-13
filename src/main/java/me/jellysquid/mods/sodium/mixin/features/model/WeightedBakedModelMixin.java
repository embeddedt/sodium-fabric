package me.jellysquid.mods.sodium.mixin.features.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.*;

@Mixin(WeightedBakedModel.class)
public class WeightedBakedModelMixin {
    @Shadow
    @Final
    private List<Weighted.Present<BakedModel>> models;

    @Shadow
    @Final
    private int totalWeight;

    /**
     * @author JellySquid
     * @reason Avoid excessive object allocations
     */
    @Overwrite
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random, ModelData modelData, RenderLayer renderLayer) {
        Weighted.Present<BakedModel> quad = getAt(this.models, Math.abs((int) random.nextLong()) % this.totalWeight);

        if (quad != null) {
            return quad.getData()
                    .getQuads(state, face, random, modelData, renderLayer);
        }

        return Collections.emptyList();
    }

    /**
     * @author embeddedt
     * @reason Avoid excessive object allocations
     */
    @Overwrite
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull Random rand, @NotNull ModelData data) {
        Weighted.Present<BakedModel> quad = getAt(this.models, Math.abs((int) rand.nextLong()) % this.totalWeight);

        if (quad != null) {
            return quad.getData().getRenderTypes(state, rand, data);
        }

        return ChunkRenderTypeSet.none();
    }

    @Unique
    private static <T extends Weighted> T getAt(List<T> pool, int totalWeight) {
        int i = 0;
        int len = pool.size();

        T weighted;

        do {
            if (i >= len) {
                return null;
            }

            weighted = pool.get(i++);
            totalWeight -= weighted.getWeight().getValue();
        } while (totalWeight >= 0);

        return weighted;
    }
}