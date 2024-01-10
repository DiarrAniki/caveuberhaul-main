package diarr.caveuberhaul.gen;

import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.MapGenBase;

public class MapGenNoiseCaves extends MapGenBase {

    private static final int lavaDepth = 10;

    protected World worldObj;

    private static final float coreThresCheese = 0.45f;
    private static final float caveThresWorm = -0.055f;
    private static final float caveThresNoodle = -0.085f;

    private static final FastNoiseLite cavernNoise = new FastNoiseLite();
    private static final FastNoiseLite wormCaveNoise = new FastNoiseLite();
    private static final FastNoiseLite caveModifierNoise = new FastNoiseLite();

    private final boolean isAlpha;

    public MapGenNoiseCaves(boolean isAlpha) {
        this.isAlpha = isAlpha;
    }

    public void generate(World world, int baseChunkX, int baseChunkZ, short[] ashort0)
    {
        this.worldObj = world;
        generateNoiseCaves(worldObj,baseChunkX, baseChunkZ, ashort0);
    }

    private void generateNoiseCaves(World world,int baseChunkX,int baseChunkZ, short[]data)
    {
        int chunkMaxHeight = getMaxSurfaceHeight(data,world);

        float[][][] CheeseCave = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.023f,1.2f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin),world);
        float[][][] WormCave = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.012f,1.2f,world, wormCaveNoise, FastNoiseLite.NoiseType.OpenSimplex2),world);
        float[][][] WormCaveOffset = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,128,128,128,0.012f,1.2f,world, wormCaveNoise, FastNoiseLite.NoiseType.OpenSimplex2),world);
        float[][][] NoodleCave = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.021f,1.5f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin),world);
        float[][][] NoodleCaveOffset = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,128,8,128,0.021f,1.5f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin),world);
        float[][] ModifierNoise = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(baseChunkX,baseChunkZ,0.008f,world, caveModifierNoise, FastNoiseLite.NoiseType.Perlin));

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z <16; ++z) {
                double modif = UberUtil.clamp(ModifierNoise[x][z],-0.015f,1f);
                int coreCavernBlockHeight = (int) (32+6*modif);

                for (int y = world.getHeightBlocks()-1; y >= 0; y--) {

                    float noiseValCheese = CheeseCave[x][y][z];

                    float noiseValWormCave = Math.abs(WormCave[x][y][z])*-1;
                    float noiseValWormCaveOffset = Math.abs(WormCaveOffset[x][y][z])*-1;

                    float noiseValNoodleCave = Math.abs(NoodleCave[x][y][z])*-1;
                    float noiseValNoodleCaveOffset = Math.abs(NoodleCaveOffset[x][y][z])*-1;

                    float coreCavernNoiseCutoff = coreThresCheese;
                    float adjustedCheeseNoiseCutoffBetween = coreThresCheese;

                    float noodleCavernNoiseCutoff = caveThresNoodle;
                    float wormCavernNoiseCutoff = caveThresWorm;



                    //World Core caves
                    if(y < coreCavernBlockHeight && y > 16) {
                        coreCavernNoiseCutoff = UberUtil.clamp(coreCavernNoiseCutoff-((coreCavernBlockHeight - y) * (0.069f )),0,.95f);
                    }
                    else if(y <= 16) {
                        coreCavernNoiseCutoff = UberUtil.clamp(coreCavernNoiseCutoff-(1-((16 - y) * 0.04f)),0,1f);
                    }
                    if (y < 14)
                    {
                        coreCavernNoiseCutoff += (14 - y) * 0.04;
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

                    //noiseValWormCave -= modif;
                    //noiseValWormCaveOffset -= modif;
                    noiseValNoodleCave -= modif;
                    noiseValNoodleCaveOffset -= modif;
                    noiseValCheese -= modif;

                    //This leads to a very cool "pillar" like worldtype
                    /*coreCavernNoiseCutoff *= modif;
                    //adjustedCheeseNoiseCutoffBetween *=modif;
                    noiseValWormCave *= modif;
                    noiseValWormCaveOffset *= modif;
                    noiseValNoodleCave *=modif;
                    noiseValNoodleCaveOffset *=modif;*/

                    boolean caveFlagWorm =noiseValWormCave > wormCavernNoiseCutoff && noiseValWormCaveOffset > wormCavernNoiseCutoff;
                    boolean caveFlagNoodle = noiseValNoodleCave > noodleCavernNoiseCutoff && noiseValNoodleCaveOffset > noodleCavernNoiseCutoff;
                    boolean caveFlagChambers = noiseValCheese > adjustedCheeseNoiseCutoffBetween;
                    boolean caveFlagCoreCavern = noiseValCheese > coreCavernNoiseCutoff;

                    boolean bedrockFlag = data[x << UberUtil.getHeightBits() + 4 | z << UberUtil.getHeightBits() | y] == (short) Block.bedrock.id;
                    boolean waterFlag = Block.getBlock(data[x << UberUtil.getHeightBits() + 4 | z << UberUtil.getHeightBits() | y]) instanceof BlockFluid;

                    if ((caveFlagCoreCavern||caveFlagChambers||caveFlagNoodle||caveFlagWorm)&&!bedrockFlag&&!waterFlag)
                    {
                        if (!isFluidBlock(Block.getBlock(data[x << UberUtil.getHeightBits() + 4 | z << UberUtil.getHeightBits() | y+1]))|| y <= lavaDepth)
                        {
                            // if we are in the easeInDepth range or near sea level, do some extra checks for water before digging
                            if ((y > (world.getHeightBlocks()/2 - 8) ) && y > lavaDepth)
                            {
                                if (x < 15)
                                    if (isFluidBlock(Block.getBlock(data[x+1 << UberUtil.getHeightBits() + 4 | z << UberUtil.getHeightBits() | y]))) {
                                        continue;
                                    }
                                if (x > 0)
                                    if (isFluidBlock(Block.getBlock(data[x-1 << UberUtil.getHeightBits() + 4 | z << UberUtil.getHeightBits() | y]))){
                                        continue;
                                    }
                                if (z < 15)
                                    if (isFluidBlock(Block.getBlock(data[x << UberUtil.getHeightBits() + 4 | z+1 << UberUtil.getHeightBits() | y]))){
                                        continue;
                                    }
                                if (z > 0)
                                    if (isFluidBlock(Block.getBlock(data[x << UberUtil.getHeightBits() + 4 | z-1 << UberUtil.getHeightBits() | y]))){
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

    private void digBlock(short[] data , int localX,int localY,int localZ,World world)
    {
        if(localY<= lavaDepth)
        {
            data[localX << UberUtil.getHeightBits() + 4 | localZ << UberUtil.getHeightBits() | localY] = (short)Block.fluidLavaStill.id;
        } else
        {
            data[localX << UberUtil.getHeightBits() + 4 | localZ << UberUtil.getHeightBits() | localY]=0;
            if (data[localX << UberUtil.getHeightBits() + 4 | localZ << UberUtil.getHeightBits() | localY] == 0 && data[localX << UberUtil.getHeightBits() + 4 | localZ << UberUtil.getHeightBits() | localY-1] == Block.dirt.id)
            {
                data[localX << UberUtil.getHeightBits() + 4 | localZ << UberUtil.getHeightBits() | localY-1] = (short) Block.grass.id;
            }
        }

    }

    private int getMaxSurfaceHeight(short[] data,World world)
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

    private int getSurfaceHeight(int localX, int localZ,short[] data,World world)
    {
        // Using a recursive binary search to find the surface
        return UberUtil.recursiveBinarySurfaceSearchUp(localX, localZ, world.getHeightBlocks()-1, 0,data,world);
    }

    private boolean isFluidBlock(Block block)
    {
        return block instanceof BlockFluid;
    }
}
