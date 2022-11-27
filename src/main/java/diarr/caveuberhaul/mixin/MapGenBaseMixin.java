package diarr.caveuberhaul.mixin;

import net.minecraft.shared.Minecraft;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import diarr.caveuberhaul.FastNoiseLite;

//Huge thanks to Worley and the Worley Caves mod https://www.curseforge.com/minecraft/mc-mods/worleys-caves for explaining how alot of this works.
@Mixin(value= MapGenBase.class,remap = false)
public class MapGenBaseMixin {
    private static float surfaceCutoff=1.2f;
    private static int easeInDepth;
    private static int lavaDepth = 10;

    private static float caveThresLowerCheese = 0.65f;
    private static float caveThresLowerCheeseDeep = 0.3f;
    private static float caveThresUpperNoodle = 0.08f;
    private static float caveThresLowerNoodle = -0.08f;

    private static FastNoiseLite fNoise = new FastNoiseLite();
    private static FastNoiseLite fNoise2 = new FastNoiseLite();

    /**
     * @author me
     * @reason bleh
     */
    //@Inject(method = "generate",at=@At("TAIL"))
    @Overwrite
    public void generate(IChunkProvider ichunkprovider, World world, int baseChunkX, int baseChunkZ, short[] ashort0)//, CallbackInfo ci)
    {
        doNoiseCaveGen(ichunkprovider,world,baseChunkX, baseChunkZ, ashort0);
    }

    @Shadow
    protected void doGeneration(World world, int chunkX, int chunkZ, int baseChunkX, int baseChunkZ, short[] data) {};

    private void doNoiseCaveGen(IChunkProvider ichunkprovider, World world, int baseChunkX, int baseChunkZ, short[] data)
    {
        //Issues seem to come from the y coordinate just leave it hard coded I guess lol. Also freezing caused by too inefficient code
        int xzScale = 4;
        float quarter = 0.25f;
        float half = 0.5f;
        Block currentBlock = null;
        BiomeGenBase currentBiome = null;

        int chunkMaxHeight = getMaxSurfaceHeight(baseChunkX,baseChunkZ,data);

        easeInDepth = chunkMaxHeight+4;

        float[][][] samples = sampleNoise(baseChunkX,baseChunkZ,chunkMaxHeight,0,0,0,0.03f,1.8f,world,fNoise);
        float[][][] samples2 = sampleNoise(baseChunkX,baseChunkZ,chunkMaxHeight,0,0,0,0.02f,1.5f,world,fNoise2);
        float[][][] samples3 = sampleNoise(baseChunkX,baseChunkZ,chunkMaxHeight,128,8,128,0.02f,1.5f,world,fNoise2);

        for (int x = 0; x < xzScale; ++x) {
            for (int z = 0; z < xzScale; ++z) {
                int depth =0;
                for (int y = Minecraft.WORLD_HEIGHT_BLOCKS/2-1; y >= 0; y--) {

                    float x0y0z0 = samples[x][y][z];
                    float x0y0z1 = samples[x][y][z + 1];
                    float x1y0z0 = samples[x + 1][y][z];
                    float x1y0z1 = samples[x + 1][y][z + 1];
                    float x0y1z0 = samples[x][y + 1][z];
                    float x0y1z1 = samples[x][y + 1][z + 1];
                    float x1y1z0 = samples[x + 1][y + 1][z];
                    float x1y1z1 = samples[x + 1][y + 1][z + 1];

                    //System.out.println(x0y0z0+" "+x0y0z1+" "+x1y0z0+" "+x1y0z1+" "+x0y1z0+" "+x0y1z1+" "+x1y1z0+" "+x1y1z1);

                    float x0y0z0_2 = samples2[x][y][z];
                    float x0y0z1_2 = samples2[x][y][z + 1];
                    float x1y0z0_2 = samples2[x + 1][y][z];
                    float x1y0z1_2 = samples2[x + 1][y][z + 1];
                    float x0y1z0_2 = samples2[x][y + 1][z];
                    float x0y1z1_2 = samples2[x][y + 1][z + 1];
                    float x1y1z0_2 = samples2[x + 1][y + 1][z];
                    float x1y1z1_2 = samples2[x + 1][y + 1][z + 1];


                    float x0y0z0_3 = samples3[x][y][z];
                    float x0y0z1_3 = samples3[x][y][z + 1];
                    float x1y0z0_3 = samples3[x + 1][y][z];
                    float x1y0z1_3 = samples3[x + 1][y][z + 1];
                    float x0y1z0_3 = samples3[x][y + 1][z];
                    float x0y1z1_3 = samples3[x][y + 1][z + 1];
                    float x1y1z0_3 = samples3[x + 1][y + 1][z];
                    float x1y1z1_3 = samples3[x + 1][y + 1][z + 1];

                    // how much to increment noise along y value linear interpolation from start y and end y
                    float noiseStepY00 = (x0y1z0 - x0y0z0) * -half;
                    float noiseStepY01 = (x0y1z1 - x0y0z1) * -half;
                    float noiseStepY10 = (x1y1z0 - x1y0z0) * -half;
                    float noiseStepY11 = (x1y1z1 - x1y0z1) * -half;


                    float noiseStepY00_2 = (x0y1z0_2 - x0y0z0_2) * -half;
                    float noiseStepY01_2 = (x0y1z1_2 - x0y0z1_2) * -half;
                    float noiseStepY10_2 = (x1y1z0_2 - x1y0z0_2) * -half;
                    float noiseStepY11_2 = (x1y1z1_2 - x1y0z1_2) * -half;


                    float noiseStepY00_3 = (x0y1z0_3 - x0y0z0_3) * -half;
                    float noiseStepY01_3 = (x0y1z1_3 - x0y0z1_3) * -half;
                    float noiseStepY10_3 = (x1y1z0_3 - x1y0z0_3) * -half;
                    float noiseStepY11_3 = (x1y1z1_3 - x1y0z1_3) * -half;


                    // noise values of 4 corners at y=0
                    float noiseStartX0 = x0y0z0;
                    float noiseStartX1 = x0y0z1;
                    float noiseEndX0 = x1y0z0;
                    float noiseEndX1 = x1y0z1;


                    float noiseStartX0_2 = x0y0z0_2;
                    float noiseStartX1_2 = x0y0z1_2;
                    float noiseEndX0_2 = x1y0z0_2;
                    float noiseEndX1_2 = x1y0z1_2;


                    float noiseStartX0_3 = x0y0z0_3;
                    float noiseStartX1_3 = x0y0z1_3;
                    float noiseEndX0_3 = x1y0z0_3;
                    float noiseEndX1_3 = x1y0z1_3;

                    for (int suby=1; suby>=0;suby--) {
                        int localY = suby + y * 2;

                        float noiseStartZ = noiseStartX0;
                        float noiseEndZ = noiseStartX1;


                        float noiseStartZ_2 = noiseStartX0_2;
                        float noiseEndZ_2 = noiseStartX1_2;


                        float noiseStartZ_3 = noiseStartX0_3;
                        float noiseEndZ_3 = noiseStartX1_3;

                        // how much to increment X values, linear interpolation
                        float noiseStepX0 = (noiseEndX0 - noiseStartX0) * quarter;
                        float noiseStepX1 = (noiseEndX1 - noiseStartX1) * quarter;


                        float noiseStepX0_2 = (noiseEndX0_2 - noiseStartX0_2) * quarter;
                        float noiseStepX1_2 = (noiseEndX1_2 - noiseStartX1_2) * quarter;


                        float noiseStepX0_3 = (noiseEndX0_3 - noiseStartX0_3) * quarter;
                        float noiseStepX1_3 = (noiseEndX1_3 - noiseStartX1_3) * quarter;

                        for (int subx = 0; subx < 4; subx++) {
                            int localX = subx+x*4;

                            // how much to increment Z values, linear interpolation
                            float noiseStepZ = (noiseEndZ - noiseStartZ) * quarter;


                            float noiseStepZ_2 = (noiseEndZ_2 - noiseStartZ_2) * quarter;


                            float noiseStepZ_3 = (noiseEndZ_3 - noiseStartZ_3) * quarter;

                            // Y and X already interpolated, just need to interpolate final 4 Z block to get final noise value
                            float noiseValCheese = noiseStartZ;
                            //System.out.println(noiseValCheese);


                            float noiseValNoodleOriginal = noiseStartZ_2;


                            float noiseValNoodleOriginalOffset = noiseStartZ_3;


                            for (int subz = 0; subz < 4; subz++) {
                                int localZ = subz + z * 4;

                                if (depth == 0)
                                {
                                    // only checks depth once per 4x4 subchunk
                                    currentBlock = Block.getBlock(data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY]);
                                    //currentBiome =
                                    // use isDigable to skip leaves/wood getting counted as surface
                                    if (canReplaceBlock(currentBlock) || isBiomeBlock(BiomeGenBase.forest,currentBlock))
                                    {
                                        depth++;
                                    }
                                } else
                                {
                                    // already hit surface, simply increment depth counter
                                    depth++;
                                }

                                float adjustedCheeseNoiseCutoffBottomTop = caveThresLowerCheese;
                                float adjustedCheeseNoiseCutoffBetween = caveThresLowerCheese;

                                if(localY < chunkMaxHeight)
                                {
                                    adjustedCheeseNoiseCutoffBetween = caveThresLowerCheese+(caveThresLowerCheeseDeep - caveThresLowerCheese)*((chunkMaxHeight+32)-localY)/chunkMaxHeight;
                                    if (localY < 30 && localY > 24)
                                    {
                                        adjustedCheeseNoiseCutoffBetween += (30 - localY) * 0.07;
                                    }
                                }

                                if(localY < 32 && localY >= 16) {
                                    adjustedCheeseNoiseCutoffBottomTop -= (32 - localY) * 0.04;
                                }
                                else if(localY <= 16) {
                                    adjustedCheeseNoiseCutoffBottomTop -= 1-((16 - localY) * 0.1);
                                }

                                if (depth < easeInDepth)
                                {
                                    // higher threshold at surface, normal threshold below easeInDepth
                                    adjustedCheeseNoiseCutoffBottomTop = ClampedLerp(caveThresLowerCheese, surfaceCutoff, (easeInDepth - (float) depth) / easeInDepth);
                                }

                                // increase cutoff as we get closer to the minCaveHeight so it's not all flat floors
                                if (localY < 5)
                                {
                                    adjustedCheeseNoiseCutoffBetween += (5 - localY) * 0.05;
                                }


                                boolean bedrockFlag = data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY] == (short) Block.bedrock.blockID;
                                boolean caveFlagNoodle = (caveThresUpperNoodle > noiseValNoodleOriginal && noiseValNoodleOriginal > caveThresLowerNoodle)&&(caveThresUpperNoodle > noiseValNoodleOriginalOffset && noiseValNoodleOriginalOffset > caveThresLowerNoodle);
                                boolean caveFlagCheese = noiseValCheese > adjustedCheeseNoiseCutoffBottomTop || noiseValCheese > adjustedCheeseNoiseCutoffBetween;
                                boolean waterFlag = Block.getBlock(data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY]) instanceof BlockFluid;

                                //System.out.println(noiseValCheese+" "+adjustedCheeseNoiseCutoffBetween);
                                if ((caveFlagCheese||caveFlagNoodle)&&!bedrockFlag&&!waterFlag)
                                {
                                    if (!isFluidBlock(Block.getBlock(data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY+1]))|| localY <= lavaDepth)
                                    {
                                        // if we are in the easeInDepth range or near sea level, do some extra checks for water before digging
                                        if ((localY > (Minecraft.WORLD_HEIGHT_BLOCKS/2 - 8) ) && localY > lavaDepth)
                                        {
                                            if (localX < 15)
                                                if (isFluidBlock(Block.getBlock(data[localX+1 << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY]))) {
                                                    continue;
                                                }
                                            if (localX > 0)
                                                if (isFluidBlock(Block.getBlock(data[localX-1 << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY]))){
                                                    continue;
                                                }
                                            if (localZ < 15)
                                                if (isFluidBlock(Block.getBlock(data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ+1 << Minecraft.WORLD_HEIGHT_BITS | localY]))){
                                                    continue;
                                                }
                                            if (localZ > 0)
                                                if (isFluidBlock(Block.getBlock(data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ-1 << Minecraft.WORLD_HEIGHT_BITS | localY]))){
                                                    continue;
                                                }
                                        }

                                        if(currentBlock == null)
                                            currentBlock = Block.getBlock(data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY]);
                                        if(currentBiome == null)
                                            currentBiome = BiomeGenBase.forest;

                                        //boolean foundTopBlock = isTopBlock(currentBiome, currentBlock);
                                        digBlock(data, currentBiome, localX,localY,localZ, currentBlock);
                                    }

                                }
                                noiseValCheese += noiseStepZ;


                                noiseValNoodleOriginal += noiseStepZ_2;


                                noiseValNoodleOriginalOffset += noiseStepZ_3;
                            }
                            noiseStartZ += noiseStepX0;
                            noiseEndZ += noiseStepX1;


                            noiseStartZ_2 += noiseStepX0_2;
                            noiseEndZ_2 += noiseStepX1_2;


                            noiseStartZ_3 += noiseStepX0_3;
                            noiseEndZ_3 += noiseStepX1_3;
                        }
                        noiseStartX0 += noiseStepY00;
                        noiseStartX1 += noiseStepY01;
                        noiseEndX0 += noiseStepY10;
                        noiseEndX1 += noiseStepY11;


                        noiseStartX0_2 += noiseStepY00_2;
                        noiseStartX1_2 += noiseStepY01_2;
                        noiseEndX0_2 += noiseStepY10_2;
                        noiseEndX1_2 += noiseStepY11_2;


                        noiseStartX0_3 += noiseStepY00_3;
                        noiseStartX1_3 += noiseStepY01_3;
                        noiseEndX0_3 += noiseStepY10_3;
                        noiseEndX1_3 += noiseStepY11_3;
                    }
                }
            }
        }
    }

    private void digBlock(short[] data , BiomeGenBase biome, int localX,int localY,int localZ, Block block)
    {
        Block top = Block.getBlock(biome.topBlock);
        Block filler = Block.getBlock(biome.fillerBlock);

        if (canReplaceBlock(block) || block == top || block == filler)
        {
            if(localY<= lavaDepth)
            {
                data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY] = (short)Block.fluidLavaStill.blockID;
            } else
            {
                data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY]=0;

                /*if (foundTop && Block.getBlock(data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY-1]) == filler)
                {
                    data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY-1] = (short)top.blockID;
                }*/
            }
        }
    }

    private int getMaxSurfaceHeight(int chunkX,int chunkZ,short[] data)
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
        return recursiveBinarySurfaceSearch(localX, localZ, Minecraft.WORLD_HEIGHT_BLOCKS-1, 0,data);
    }

    // Recursive binary search, this search always converges on the surface in 8 in cycles for the range 255 >= y >= 0
    private int recursiveBinarySurfaceSearch(int localX, int localZ, int searchTop, int searchBottom,short[] data)
    {
        int top = searchTop;
        if (searchTop > searchBottom)
        {
            int searchMid = (searchBottom + searchTop) / 2;
            if (canReplaceBlock(Block.getBlock(data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | searchMid])))
            {
                top = recursiveBinarySurfaceSearch(localX, localZ, searchTop, searchMid + 1,data);
            } else
            {
                top = recursiveBinarySurfaceSearch(localX, localZ, searchMid, searchBottom,data);
            }
        }
        return top;
    }

    private boolean isBiomeBlock(BiomeGenBase biome, Block block)
    {
        return block == Block.getBlock(biome.topBlock);
    }

    private boolean isTopBlock(BiomeGenBase biome, Block block)
    {
        return block == Block.getBlock(biome.topBlock);
    }

    private boolean isFluidBlock(Block block)
    {
        return block instanceof BlockFluid;
    }

    public float[][][] sampleNoise(int chunkX, int chunkZ, int maxSurfaceHeight, int offX,int offY,int offZ,float freq,float yCrunch, World world, FastNoiseLite tNoise)
    {
        float[][][] noiseSamples = new float[5][130][5];
        float noise;
        tNoise.SetSeed((int) world.getRandomSeed());
        tNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        tNoise.SetFrequency(freq);

        for (int x = 0; x < 5; x++)
        {
            int realX = x * 4 + chunkX * 16;
            for (int z = 0; z < 5; z++)
            {
                int realZ = z * 4 + chunkZ * 16;

                // loop from top down for y values so we can adjust noise above current y later on
                for (int y = Minecraft.WORLD_HEIGHT_BLOCKS/2; y >= 0; y--)
                {
                    float realY = y * 2;

                    // doubling the y frequency to get some more caves
                    noise = tNoise.GetNoise(realX+offX,(realY+offY)*yCrunch,realZ+offZ);
                    noiseSamples[x][y][z] = noise;
                    /*if (noise > caveThresLowerCheese)
                    {
                        // if noise is below cutoff, adjust values of neighbors helps prevent caves fracturing during interpolation
                        if (x > 0)
                            noiseSamples[x - 1][y][z] = (noise * 0.2f) + (noiseSamples[x - 1][y][z] * 0.8f);
                        if (z > 0)
                            noiseSamples[x][y][z - 1] = (noise * 0.2f) + (noiseSamples[x][y][z - 1] * 0.8f);

                        // more heavily adjust y above 'air block' noise values to give players more head room
                        if (y < 128)
                        {
                            float noiseAbove = noiseSamples[x][y + 1][z];
                            if (noise > noiseAbove)
                                noiseSamples[x][y + 1][z] = (noise * 0.8F) + (noiseAbove * 0.2F);
                            if (y < 127)
                            {
                                float noiseTwoAbove = noiseSamples[x][y + 2][z];
                                if (noise > noiseTwoAbove)
                                    noiseSamples[x][y + 2][z] = (noise * 0.35F) + (noiseTwoAbove * 0.65F);
                            }
                        }

                    }*/
                }
            }
        }
        return noiseSamples;
    }

    protected boolean canReplaceBlock(Block block)
    {
        // Replace anything that's made of rock which should hopefully work for most modded type stones (and maybe not break everything)
        if(block == null)
        {
            return false;
        }
        else {
            return block.blockMaterial == Material.rock;
        }
    }

    private float Lerp(float a,float b, float t)
    {
        return a+(b-a)*t;
    }

    /*private BiomeGenBase GuessBiome(Block block)
    {
        switch (block.blockID)
        {
            case (short)Block.sand.blockID:
            {}
        }
    }*/

    public float smoothUnion(float a, float b, float delta) {
        float h = clamp(0.5F + 0.5F * (b - a) / delta, 0, 1);
        return Lerp(b, a, h) - delta * h * (1 - h);
    }

    public float clamp(float val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }

    private float ClampedLerp(float a,float b,float t)
    {
        return clamp(Lerp(a,b,t),-1,1);
    }
}
