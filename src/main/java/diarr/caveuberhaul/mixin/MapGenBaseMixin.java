package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.gen.cavebiomes.CaveBiomeProvider;
import diarr.caveuberhaul.gen.FastNoiseLite;
import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkSection;
import net.minecraft.core.world.generate.MapGenBase;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Huge thanks to Worley and the Worley Caves mod https://www.curseforge.com/minecraft/mc-mods/worleys-caves for explaining how a lot of this works.
//@Mixin(value= MapGenBase.class,remap = false)
@Mixin(value= MapGenBase.class,remap = false)
public class MapGenBaseMixin {

    @Unique
    public boolean[][][] cutoffValues;

//    @Unique
//    private static final float surfaceCutoff=1.2f;
    @Unique
    private static final int lavaDepth = 10;
    @Shadow
    protected World worldObj;

    @Unique
    private static final float coreThresCheese = 0.45f;
    @Unique
    private static final float caveThresWorm = -0.055f;
    @Unique
    private static final float caveThresNoodle = -0.085f;

    @Unique
    private static final FastNoiseLite cavernNoise = new FastNoiseLite();
    @Unique
    private static final FastNoiseLite wormCaveNoise = new FastNoiseLite();
    @Unique
    private static final FastNoiseLite caveModifierNoise = new FastNoiseLite();

    //private static UberUtil uberUtil = new UberUtil();

    @Inject(method = "generate", at = @At("HEAD"),cancellable = true)
    public void doGeneration(World world, int baseChunkX, int baseChunkZ, ChunkGeneratorResult result, CallbackInfo ci)
    {
        this.worldObj = world;
        cutoffValues = new boolean[16][256][16];
        generateNoiseCaves(worldObj, world.getChunkFromChunkCoords(baseChunkX, baseChunkZ));
        ci.cancel();
    }

    @Unique
    private void generateNoiseCaves(World world, Chunk chunk)
    {
        int chunkMaxHeight = getMaxSurfaceHeight(chunk);
        int baseChunkX = chunk.xPosition;
        int baseChunkZ = chunk.zPosition;
        new CaveBiomeProvider(world, baseChunkX, baseChunkZ);

        //easeInDepth = chunkMaxHeight+4;
        float[][][] CheeseCave = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.023f,1.2f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin),world);
        float[][][] WormCave = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.012f,1.2f,world, wormCaveNoise, FastNoiseLite.NoiseType.OpenSimplex2),world);
        float[][][] WormCaveOffset = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,128,128,128,0.012f,1.2f,world, wormCaveNoise, FastNoiseLite.NoiseType.OpenSimplex2),world);
        float[][][] NoodleCave = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.021f,1.5f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin),world);
        float[][][] NoodleCaveOffset = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,128,8,128,0.021f,1.5f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin),world);
        float[][] ModifierNoise = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(baseChunkX,baseChunkZ,0.01f,world, caveModifierNoise, FastNoiseLite.NoiseType.OpenSimplex2));

        double modifOffset = 0.6f;
        int depth = 0;
        Block currentBlock;

        for (int s = 0; s < Chunk.CHUNK_SECTIONS; s++) {
            for (int x = 0; x < Chunk.CHUNK_SIZE_X; ++x) {
                for (int z = 0; z < Chunk.CHUNK_SIZE_Z; ++z) {
                    //double modif = UberUtil.clamp((ModifierNoise[x][z]+modifOffset)*Math.pow(ModifierNoise[x][z]+modifOffset,2),0,1.1f);
                    double modif = UberUtil.clamp((ModifierNoise[x][z]+1)/2,0,1.05f);
                    for (int _y = ChunkSection.SECTION_SIZE_Y - 1; _y >= 0; _y--) {
                        int y = s * ChunkSection.SECTION_SIZE_Y + _y;
                        float noiseValCheese = CheeseCave[x][y][z];

                        float noiseValWormCave = Math.abs(WormCave[x][y][z])*-1;
                        float noiseValWormCaveOffset = Math.abs(WormCaveOffset[x][y][z])*-1;

                        float noiseValNoodleCave = Math.abs(NoodleCave[x][y][z])*-1;
                        float noiseValNoodleCaveOffset = Math.abs(NoodleCaveOffset[x][y][z])*-1;

                        float coreCavernNoiseCutoff = coreThresCheese;
                        float adjustedCheeseNoiseCutoffBetween = coreThresCheese;

                        float noodleCavernNoiseCutoff = caveThresNoodle;
                        float wormCavernNoiseCutoff = caveThresWorm;

                        if (depth == 0)
                        {
                            // only checks depth once per 4x4 subchunk
                            currentBlock = Block.getBlock(chunk.getBlockID(x, y, z));
                            if (UberUtil.isRockBlock(currentBlock))
                            {
                                depth++;
                            }
                        } else
                        {
                            depth++;
                        }

                        //World Core caves
                        if(y < 32 && y > 16) {
                            coreCavernNoiseCutoff = UberUtil.clamp(coreCavernNoiseCutoff-((32 - y) * 0.069f),0,.95f);
                        }
                        else if(y <= 16) {
                            coreCavernNoiseCutoff = UberUtil.clamp(coreCavernNoiseCutoff-(1-((16 - y) * 0.04f)),0,1f);
                        }
                        if (y < 14)
                        {
                            coreCavernNoiseCutoff += (14 - y) * 0.05;
                        }

                        // increase cutoff as we get closer to the minCaveHeight, so it's not all flat floors
                        if (y < 32)
                        {
                            adjustedCheeseNoiseCutoffBetween += (32 - y) * 0.05;
                        }
                        //TODO find solution to decrease cave size near surface
                        if (y > chunkMaxHeight-32)
                        {
                            adjustedCheeseNoiseCutoffBetween /= UberUtil.clamp((32 - y) * 0.032,0,1);
                            noodleCavernNoiseCutoff *= UberUtil.clamp((32 - y) * 0.032,0,1);
                        }

                        noodleCavernNoiseCutoff *= modif;

                        //This leads to a very cool "pillar" like worldtype
                    /*coreCavernNoiseCutoff *= modif;
                    //adjustedCheeseNoiseCutoffBetween *=modif;
                    noiseValWormCave *= modif;
                    noiseValWormCaveOffset *= modif;
                    noiseValNoodleCave *=modif;
                    noiseValNoodleCaveOffset *=modif;*/

                        boolean bedrockFlag = chunk.getBlockID(x, y, z) == (short) Block.bedrock.id;

                        boolean caveFlagWorm =noiseValWormCave > wormCavernNoiseCutoff && noiseValWormCaveOffset > wormCavernNoiseCutoff;
                        boolean caveFlagNoodle = noiseValNoodleCave > noodleCavernNoiseCutoff && noiseValNoodleCaveOffset > noodleCavernNoiseCutoff;
                        boolean caveFlagChambers = noiseValCheese > adjustedCheeseNoiseCutoffBetween;
                        boolean caveFlagCoreCavern = noiseValCheese > coreCavernNoiseCutoff;

                        boolean waterFlag = Block.getBlock(chunk.getBlockID(x, y, z)) instanceof BlockFluid;

                        //System.out.println(noiseValCheese+" "+adjustedCheeseNoiseCutoffBetween);
                        if ((caveFlagCoreCavern||caveFlagChambers||caveFlagNoodle||caveFlagWorm)&&!bedrockFlag&&!waterFlag)
                        {
                            if (!isFluidBlock(Block.getBlock(chunk.getBlockID(x, y + 1, z)))|| y <= lavaDepth)
                            {
                                // if we are in the easeInDepth range or near sea level, do some extra checks for water before digging
                                if ((y > (world.getHeightBlocks()/2 - 8) ) && y > lavaDepth)
                                {
                                    if (x < 15)
                                        if (isFluidBlock(Block.getBlock(chunk.getBlockID(x + 1, y, z)))) {
                                            continue;
                                        }
                                    if (x > 0)
                                        if (isFluidBlock(Block.getBlock(chunk.getBlockID(x - 1, y, z)))){
                                            continue;
                                        }
                                    if (z < 15)
                                        if (isFluidBlock(Block.getBlock(chunk.getBlockID(x, y, z + 1)))){
                                            continue;
                                        }
                                    if (z > 0)
                                        if (isFluidBlock(Block.getBlock(chunk.getBlockID(x, y, z - 1)))){
                                            continue;
                                        }
                                }

                                digBlock(chunk, x,y,z);
                            }
                        }
                    }
                }
            }
        }

    }

    @Unique
    private void digBlock(Chunk chunk, int localX, int localY, int localZ)
    {
            if(localY<= lavaDepth)
            {
                UberUtil.setBlockDirectely(chunk, localX, localY, localZ, Block.fluidLavaStill.id);
            } else
            {
                UberUtil.setBlockDirectely(chunk, localX, localY, localZ, 0);

                if (chunk.getBlockID(localX, localY - 1, localZ) == Block.dirt.id)
                {
                    UberUtil.setBlockDirectely(chunk, localX, localY - 1, localZ, Block.grass.id);
                }
            }
            this.cutoffValues[localX][localY][localZ] = true;
    }

    @Unique
    private int getMaxSurfaceHeight(Chunk chunk)
    {
        int max = 0;
        for (int x = 0; x < Chunk.CHUNK_SIZE_X; x++){
            for (int z = 0; z < Chunk.CHUNK_SIZE_Z; z++){
                int height = chunk.getHeightValue(x, z);
                if (height > max){
                    max = height;
                }
            }
        }
        return max;
    }
    @Unique
    private boolean isFluidBlock(Block block)
    {
        return block instanceof BlockFluid;
    }
}