package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.gen.FastNoiseLite;
import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.MapGenBase;
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
    public void doGeneration(World world, int baseChunkX, int baseChunkZ, short[] ashort0, CallbackInfo ci)
    {
        this.worldObj = world;
        cutoffValues = new boolean[16][256][16];
        generateNoiseCaves(worldObj,baseChunkX, baseChunkZ, ashort0);
        ci.cancel();
    }

    @Unique
    private void generateNoiseCaves(World world, int baseChunkX, int baseChunkZ, short[]data)
    {
        int chunkMaxHeight = getMaxSurfaceHeight(data,world);

        //easeInDepth = chunkMaxHeight+4;
        float[][][] CheeseCave = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.023f,1.2f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin),world);
        float[][][] WormCave = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.012f,1.2f,world, wormCaveNoise, FastNoiseLite.NoiseType.OpenSimplex2),world);
        float[][][] WormCaveOffset = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,128,128,128,0.012f,1.2f,world, wormCaveNoise, FastNoiseLite.NoiseType.OpenSimplex2),world);
        float[][][] NoodleCave = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.021f,1.5f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin),world);
        float[][][] NoodleCaveOffset = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,128,8,128,0.021f,1.5f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin),world);
        float[][] ModifierNoise = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(baseChunkX,baseChunkZ,0.009f,world, caveModifierNoise, FastNoiseLite.NoiseType.OpenSimplex2));

        double modifOffset = 0.6f;
        int depth = 0;
        Block currentBlock;

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z <16; ++z) {
                double modif = UberUtil.clamp((ModifierNoise[x][z]+modifOffset)*Math.pow(ModifierNoise[x][z]+modifOffset,4),0,1.05f);
                for (int y = world.getHeightBlocks()-1; y >= 0; y--) {

                    float noiseValCheese = CheeseCave[x][y][z];

                    float noiseValWormCave = Math.abs(WormCave[x][y][z])*-1;

                    float noiseValWormCaveOffset = Math.abs(WormCaveOffset[x][y][z])*-1;

                    float noiseValNoodleCave = Math.abs(NoodleCave[x][y][z])*-1;
                    //float noiseValNoodleCave = Math.abs(noiseValCheese)*-1;

                    float noiseValNoodleCaveOffset = Math.abs(NoodleCaveOffset[x][y][z])*-1;

                    float coreCavernNoiseCutoff = coreThresCheese;
                    float adjustedCheeseNoiseCutoffBetween = coreThresCheese;

                    float noodleCavernNoiseCutoff = caveThresNoodle;
                    float wormCavernNoiseCutoff = caveThresWorm;

                    if (depth == 0)
                    {
                        // only checks depth once per 4x4 subchunk
                        currentBlock = Block.getBlock(data[x << world.getHeightBits() + 4 | z << world.getHeightBits() | y]);
                        //currentBiome =
                        // use isDigable to skip leaves/wood getting counted as surface
                        if (UberUtil.isRockBlock(currentBlock))
                        {
                            depth++;
                        }
                    } else
                    {
                        // already hit surface, simply increment depth counter
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

                    boolean bedrockFlag = data[x << world.getHeightBits() + 4 | z << world.getHeightBits() | y] == (short) Block.bedrock.id;
                    //boolean caveFlagWorm = (caveThresWorm > noiseValWormCave && noiseValWormCave > -caveThresWorm)&&(caveThresWorm > noiseValWormCaveOffset && noiseValWormCaveOffset > -caveThresWorm);
                    //boolean caveFlagNoodle = (caveThresNoodle  > noiseValNoodleCave && noiseValNoodleCave > -caveThresNoodle )&&(caveThresNoodle > noiseValNoodleCaveOffset && noiseValNoodleCaveOffset > -caveThresNoodle );
                    boolean caveFlagWorm =noiseValWormCave > wormCavernNoiseCutoff && noiseValWormCaveOffset > wormCavernNoiseCutoff;
                    boolean caveFlagNoodle = noiseValNoodleCave > noodleCavernNoiseCutoff && noiseValNoodleCaveOffset > noodleCavernNoiseCutoff;
                    boolean caveFlagChambers = noiseValCheese > adjustedCheeseNoiseCutoffBetween;
                    boolean caveFlagCoreCavern = noiseValCheese > coreCavernNoiseCutoff;
                    boolean waterFlag = Block.getBlock(data[x << world.getHeightBits() + 4 | z << world.getHeightBits() | y]) instanceof BlockFluid;

                    //System.out.println(noiseValCheese+" "+adjustedCheeseNoiseCutoffBetween);
                    if ((caveFlagCoreCavern||caveFlagChambers||caveFlagNoodle||caveFlagWorm)&&!bedrockFlag&&!waterFlag)
                    {
                        if (!isFluidBlock(Block.getBlock(data[x << world.getHeightBits() + 4 | z << world.getHeightBits() | y+1]))|| y <= lavaDepth)
                        {
                            // if we are in the easeInDepth range or near sea level, do some extra checks for water before digging
                            if ((y > (world.getHeightBlocks()/2 - 8) ) && y > lavaDepth)
                            {
                                if (x < 15)
                                    if (isFluidBlock(Block.getBlock(data[x+1 << world.getHeightBits() + 4 | z << world.getHeightBits() | y]))) {
                                        continue;
                                    }
                                if (x > 0)
                                    if (isFluidBlock(Block.getBlock(data[x-1 << world.getHeightBits() + 4 | z << world.getHeightBits() | y]))){
                                        continue;
                                    }
                                if (z < 15)
                                    if (isFluidBlock(Block.getBlock(data[x << world.getHeightBits() + 4 | z+1 << world.getHeightBits() | y]))){
                                        continue;
                                    }
                                if (z > 0)
                                    if (isFluidBlock(Block.getBlock(data[x << world.getHeightBits() + 4 | z-1 << world.getHeightBits() | y]))){
                                        continue;
                                    }
                            }

                            digBlock(data, x,y,z,world);
                        }
                    }
                }
            }
        }
    }

    @Unique
    private void digBlock(short[] data , int localX, int localY, int localZ, World world)
    {
            if(localY<= lavaDepth)
            {
                data[localX << world.getHeightBits() + 4 | localZ << world.getHeightBits() | localY] = (short)Block.fluidLavaStill.id;
            } else
            {
                data[localX << world.getHeightBits() + 4 | localZ << world.getHeightBits() | localY]=0;

                if (data[localX << world.getHeightBits() + 4 | localZ << world.getHeightBits() | localY] == 0 && data[localX << world.getHeightBits() + 4 | localZ << world.getHeightBits() | localY-1] == Block.dirt.id)
                {
                    data[localX << world.getHeightBits() + 4 | localZ << world.getHeightBits() | localY-1] = (short) Block.grass.id;
                }
            }
            this.cutoffValues[localX][localY][localZ] = true;
    }

    @Unique
    private int getMaxSurfaceHeight(short[] data, World world)
    {
        int max = 0;
        int[][] testcords = {{2, 6}, {3, 11}, {7, 2}, {9, 13}, {12,4}, {13, 9}};

        for (int[] testcord : testcords) {
            int testmax = getSurfaceHeight(testcord[0], testcord[1], data, world);
            if (testmax > max) {
                max = testmax;
                if (max > 134)
                    return max;
            }
        }
        return max;
    }

    @Unique
    private int getSurfaceHeight(int localX, int localZ, short[] data, World world)
    {
        // Using a recursive binary search to find the surface
        return UberUtil.recursiveBinarySurfaceSearchUp(localX, localZ, world.getHeightBlocks()-1, 0,data,world);
    }

    @Unique
    private boolean isFluidBlock(Block block)
    {
        return block instanceof BlockFluid;
    }
}