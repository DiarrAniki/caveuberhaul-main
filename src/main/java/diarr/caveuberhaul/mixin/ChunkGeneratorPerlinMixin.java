package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.gen.CaveBiomeProvider;
import diarr.caveuberhaul.gen.FastNoiseLite;
import diarr.caveuberhaul.gen.MapGenNoiseCaves;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.MapGenBase;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.perlin.ChunkGeneratorPerlin;
import net.minecraft.core.world.generate.chunk.perlin.SurfaceGenerator;
import net.minecraft.core.world.generate.chunk.perlin.TerrainGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//Huge thanks to Worley and the Worley Caves mod https://www.curseforge.com/minecraft/mc-mods/worleys-caves for explaining how alot of this works.
@Mixin(value= ChunkGeneratorPerlin.class,remap = false)
public class ChunkGeneratorPerlinMixin extends ChunkGenerator {
    protected MapGenBase caveGen = new MapGenNoiseCaves(false);

    @Shadow
    private  TerrainGenerator terrainGenerator;
    @Shadow
    private  SurfaceGenerator surfaceGenerator;

    public ChunkGeneratorPerlinMixin(World world, ChunkDecorator decorator) {
        super(world, decorator);
    }

    @Shadow
    protected short[] doBlockGeneration(Chunk chunk) {
        return new short[0];
    }

    @Inject(method = "doBlockGeneration", at = @At("HEAD"),cancellable = true)
    public void provideChunk(Chunk chunk, CallbackInfoReturnable<short[]> cir) {
        double[] densityMap = this.terrainGenerator.getDensityGenerator().generateDensityMap(chunk);
        short[] blocks = this.terrainGenerator.generateTerrain(chunk, densityMap);
        this.surfaceGenerator.generateSurface(chunk, blocks);
        this.caveGen.generate(this.world, chunk.xPosition, chunk.zPosition, blocks);
        cir.setReturnValue(blocks);
    }
}
