package diarr.caveuberhaul;

import net.minecraft.shared.Minecraft;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class MapGenNoiseCaves extends MapGenBase {

    public boolean[][][] cutoffValues;

    private static float surfaceCutoff=1.2f;
    private static int lavaDepth = 10;

    protected World worldObj;

    private static float coreThresCheese = 0.45f;
    private static float caveThresWorm = -0.055f;
    private static float caveThresNoodle = -0.085f;

    private static FastNoiseLite cavernNoise = new FastNoiseLite();
    private static FastNoiseLite wormCaveNoise = new FastNoiseLite();
    private static FastNoiseLite caveModifierNoise = new FastNoiseLite();

    private static UberUtil uberUtil = new UberUtil();

    private boolean isAlpha;

    public MapGenNoiseCaves(boolean isAlpha) {
        this.isAlpha = isAlpha;
    }

    public void generate(IChunkProvider ichunkprovider, World world, int baseChunkX, int baseChunkZ, short[] ashort0)
    {
        this.worldObj = world;
        cutoffValues = new boolean[16][256][16];
        generateNoiseCaves(worldObj,baseChunkX, baseChunkZ, ashort0);
    }

    private void generateNoiseCaves(World world,int baseChunkX,int baseChunkZ, short[]data)
    {
        int chunkMaxHeight = getMaxSurfaceHeight(data);

        //easeInDepth = chunkMaxHeight+4;
        float[][][] CheeseCave = uberUtil.getInterpolatedNoiseValue(uberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.025f,1.2f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin));
        float[][][] WormCave = uberUtil.getInterpolatedNoiseValue(uberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.012f,1.2f,world, wormCaveNoise, FastNoiseLite.NoiseType.OpenSimplex2));
        float[][][] WormCaveOffset = uberUtil.getInterpolatedNoiseValue(uberUtil.sampleNoise(baseChunkX,baseChunkZ,128,128,128,0.012f,1.2f,world, wormCaveNoise, FastNoiseLite.NoiseType.OpenSimplex2));
        float[][][] NoodleCave = uberUtil.getInterpolatedNoiseValue(uberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.021f,1.5f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin));
        float[][][] NoodleCaveOffset = uberUtil.getInterpolatedNoiseValue(uberUtil.sampleNoise(baseChunkX,baseChunkZ,128,8,128,0.021f,1.5f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin));
        float[][] ModifierNoise = uberUtil.getInterpolatedNoiseValue2D(uberUtil.sampleNoise2D(baseChunkX,baseChunkZ,0.009f,world, caveModifierNoise, FastNoiseLite.NoiseType.OpenSimplex2));

        double modifOffset = 0.6f;
        int depth = 0;
        Block currentBlock = null;

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z <16; ++z) {
                double modif = uberUtil.clamp((ModifierNoise[x][z]+modifOffset)*Math.pow(ModifierNoise[x][z]+modifOffset,4),0,1.05f);
                for (int y = Minecraft.WORLD_HEIGHT_BLOCKS-1; y >= 0; y--) {

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
                        currentBlock = Block.getBlock(data[x << Minecraft.WORLD_HEIGHT_BITS + 4 | z << Minecraft.WORLD_HEIGHT_BITS | y]);
                        //currentBiome =
                        // use isDigable to skip leaves/wood getting counted as surface
                        if (uberUtil.isRockBlock(currentBlock))
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
                        coreCavernNoiseCutoff = uberUtil.clamp(coreCavernNoiseCutoff-((32 - y) * 0.069f),0,.95f);
                    }
                    else if(y <= 16) {
                        coreCavernNoiseCutoff = uberUtil.clamp(coreCavernNoiseCutoff-(1-((16 - y) * 0.04f)),0,1f);
                    }
                    if (y < 14)
                    {
                        coreCavernNoiseCutoff += (14 - y) * 0.05;
                    }

                    // increase cutoff as we get closer to the minCaveHeight so it's not all flat floors
                    if (y < 32)
                    {
                        adjustedCheeseNoiseCutoffBetween += (32 - y) * 0.05;
                    }
                    //TODO find solution to decrease cave size near surface
                    if (y > chunkMaxHeight-32)
                    {
                        adjustedCheeseNoiseCutoffBetween /= uberUtil.clamp((32 - y) * 0.032,0,1);
                        noodleCavernNoiseCutoff *= uberUtil.clamp((32 - y) * 0.032,0,1);
                    }

                    noodleCavernNoiseCutoff *= modif;

                    //This leads to a very cool "pillar" like worldtype
                    /*coreCavernNoiseCutoff *= modif;
                    //adjustedCheeseNoiseCutoffBetween *=modif;
                    noiseValWormCave *= modif;
                    noiseValWormCaveOffset *= modif;
                    noiseValNoodleCave *=modif;
                    noiseValNoodleCaveOffset *=modif;*/

                    boolean bedrockFlag = data[x << Minecraft.WORLD_HEIGHT_BITS + 4 | z << Minecraft.WORLD_HEIGHT_BITS | y] == (short) Block.bedrock.blockID;
                    //boolean caveFlagWorm = (caveThresWorm > noiseValWormCave && noiseValWormCave > -caveThresWorm)&&(caveThresWorm > noiseValWormCaveOffset && noiseValWormCaveOffset > -caveThresWorm);
                    //boolean caveFlagNoodle = (caveThresNoodle  > noiseValNoodleCave && noiseValNoodleCave > -caveThresNoodle )&&(caveThresNoodle > noiseValNoodleCaveOffset && noiseValNoodleCaveOffset > -caveThresNoodle );
                    boolean caveFlagWorm =noiseValWormCave > wormCavernNoiseCutoff && noiseValWormCaveOffset > wormCavernNoiseCutoff;
                    boolean caveFlagNoodle = noiseValNoodleCave > noodleCavernNoiseCutoff && noiseValNoodleCaveOffset > noodleCavernNoiseCutoff;
                    boolean caveFlagChambers = noiseValCheese > adjustedCheeseNoiseCutoffBetween;
                    boolean caveFlagCoreCavern = noiseValCheese > coreCavernNoiseCutoff;
                    boolean waterFlag = Block.getBlock(data[x << Minecraft.WORLD_HEIGHT_BITS + 4 | z << Minecraft.WORLD_HEIGHT_BITS | y]) instanceof BlockFluid;

                    cutoffValues[x][y][z]=false;

                    //System.out.println(noiseValCheese+" "+adjustedCheeseNoiseCutoffBetween);
                    if ((caveFlagCoreCavern||caveFlagChambers||caveFlagNoodle||caveFlagWorm)&&!bedrockFlag&&!waterFlag)
                    {
                        if (!isFluidBlock(Block.getBlock(data[x << Minecraft.WORLD_HEIGHT_BITS + 4 | z << Minecraft.WORLD_HEIGHT_BITS | y+1]))|| y <= lavaDepth)
                        {
                            // if we are in the easeInDepth range or near sea level, do some extra checks for water before digging
                            if ((y > (Minecraft.WORLD_HEIGHT_BLOCKS/2 - 8) ) && y > lavaDepth)
                            {
                                if (x < 15)
                                    if (isFluidBlock(Block.getBlock(data[x+1 << Minecraft.WORLD_HEIGHT_BITS + 4 | z << Minecraft.WORLD_HEIGHT_BITS | y]))) {
                                        continue;
                                    }
                                if (x > 0)
                                    if (isFluidBlock(Block.getBlock(data[x-1 << Minecraft.WORLD_HEIGHT_BITS + 4 | z << Minecraft.WORLD_HEIGHT_BITS | y]))){
                                        continue;
                                    }
                                if (z < 15)
                                    if (isFluidBlock(Block.getBlock(data[x << Minecraft.WORLD_HEIGHT_BITS + 4 | z+1 << Minecraft.WORLD_HEIGHT_BITS | y]))){
                                        continue;
                                    }
                                if (z > 0)
                                    if (isFluidBlock(Block.getBlock(data[x << Minecraft.WORLD_HEIGHT_BITS + 4 | z-1 << Minecraft.WORLD_HEIGHT_BITS | y]))){
                                        continue;
                                    }
                            }

                            digBlock(data, x,y,z);
                        }
                    }
                }
            }
        }
    }

    private void digBlock(short[] data , int localX,int localY,int localZ)
    {
        if(localY<= lavaDepth)
        {
            data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY] = (short)Block.fluidLavaStill.blockID;
        } else
        {
            data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY]=0;
            this.cutoffValues[localX][localY][localZ] = true;
            if (data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY] == 0 && data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY-1] == Block.dirt.blockID)
            {
                data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY-1] = (short) Block.grass.blockID;
            }
        }

    }

    private int getMaxSurfaceHeight(short[] data)
    {
        int max = 0;
        int[][] testcords = {{2, 6}, {3, 11}, {7, 2}, {9, 13}, {12,4}, {13, 9}};

        for (int n = 0; n < testcords.length; n++)
        {
            int testmax = getSurfaceHeight(testcords[n][0], testcords[n][1],data);
            if(testmax > max)
            {
                max = testmax;
                if(max > 134)
                    return max;
            }
        }
        return max;
    }

    private int getSurfaceHeight(int localX, int localZ,short[] data)
    {
        // Using a recursive binary search to find the surface
        return uberUtil.recursiveBinarySurfaceSearchUp(localX, localZ, Minecraft.WORLD_HEIGHT_BLOCKS-1, 0,data);
    }

    private boolean isFluidBlock(Block block)
    {
        return block instanceof BlockFluid;
    }
}
