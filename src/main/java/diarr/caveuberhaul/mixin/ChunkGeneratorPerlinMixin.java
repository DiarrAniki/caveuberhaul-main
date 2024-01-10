package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.CaveUberhaul;
import diarr.caveuberhaul.gen.MapGenNoiseCaves;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.MapGenBase;
import net.minecraft.core.world.generate.MapGenCaves;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;
import net.minecraft.core.world.generate.chunk.perlin.ChunkGeneratorPerlin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//Huge thanks to Worley and the Worley Caves mod https://www.curseforge.com/minecraft/mc-mods/worleys-caves for explaining how alot of this works.
@Mixin(value= ChunkGeneratorPerlin.class,remap = false)
public abstract class ChunkGeneratorPerlinMixin extends ChunkGenerator {
    @Unique
    protected MapGenNoiseCaves caveGen = new MapGenNoiseCaves(false);
    @Unique
    protected MapGenBase caveGen2 = new MapGenCaves(false);
    public ChunkGeneratorPerlinMixin(World world, ChunkDecorator decorator) {
        super(world, decorator);
    }
    @Redirect(method = "doBlockGeneration", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/generate/MapGenBase;generate(Lnet/minecraft/core/world/World;IILnet/minecraft/core/world/generate/chunk/ChunkGeneratorResult;)V"))
    public void provideChunk(MapGenBase instance, World world, int baseChunkX, int baseChunkZ, ChunkGeneratorResult result){
        caveGen.generate(this.world, baseChunkX, baseChunkZ, result);
        if(CaveUberhaul.config.getBoolean("Additional_Old_Caves")) {
            caveGen2.generate(this.world, baseChunkX, baseChunkZ, result);
        }
    }
}
