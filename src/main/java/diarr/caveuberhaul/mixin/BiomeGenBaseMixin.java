package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.features.WorldGenTreeShapeDefaulti;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value= Biome.class,remap = false)
public class BiomeGenBaseMixin {
    @Inject(method = "getRandomWorldGenForTrees", at = @At("HEAD"),cancellable = true)
        public void getRandomWorldGenForTreesMix(Random random, CallbackInfoReturnable<WorldFeature> cir) {
            cir.setReturnValue ((random.nextInt(10) == 0 ? new WorldFeatureTreeFancy(Block.leavesOak.id, Block.logOak.id) : random.nextFloat()<=0.00001f?new WorldGenTreeShapeDefaulti(Block.logOak.id, 4):new WorldFeatureTreeFancy(Block.leavesOak.id, Block.logOak.id, 4)));
        }
}
