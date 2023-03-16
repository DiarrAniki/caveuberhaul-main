package diarr.caveuberhaul.mixin;


import diarr.caveuberhaul.FastNoiseLite;
import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.generate.MapGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Huge thanks to Worley and the Worley Caves mod https://www.curseforge.com/minecraft/mc-mods/worleys-caves for explaining how alot of this works.
//@Mixin(value= MapGenBase.class,remap = false)
@Mixin(value= MapGenBase.class,remap = false)
public class MapGenBaseMixin {

    private static float surfaceCutoff=1.2f;
    private static int easeInDepth;
    private static int lavaDepth = 10;
    @Shadow
    protected World worldObj;

    private static float caveThresLowerCheese = 0.45f;
    private static float caveThresUpperNoodle = 0.1f;
    private static float caveThresLowerNoodle = -0.1f;

    private static FastNoiseLite fNoise = new FastNoiseLite();
    private static FastNoiseLite fNoise2 = new FastNoiseLite();
    private static FastNoiseLite modifNoise = new FastNoiseLite();

    private static UberUtil uberUtil = new UberUtil();

    /**
     * @author me
     * @reason bleh
     */
    //@Inject(method = "generate",at=@At("TAIL"))
    /*@Overwrite
    public void generate(IChunkProvider ichunkprovider, World world, int baseChunkX, int baseChunkZ, short[] ashort0)//, CallbackInfo ci)
    {*/
    @Inject(method = "generate", at = @At("HEAD"),cancellable = true)
    public void generate(World world, int baseChunkX, int baseChunkZ, short[] ashort0, CallbackInfo ci)
    {
        this.worldObj = world;
        doNoiseCaveGen(worldObj,baseChunkX, baseChunkZ, ashort0);
        ci.cancel();
    }

    //@Shadow
   // protected void doGeneration(World world, int chunkX, int chunkZ, int baseChunkX, int baseChunkZ, short[] data) {};

    private void doNoiseCaveGen(World world, int baseChunkX, int baseChunkZ, short[] data)
    {
        //Issues seem to come from the y coordinate just leave it hard coded I guess lol. Also freezing caused by too inefficient code
        int xzScale = 4;
        float quarter = 0.25f;
        float half = 0.5f;
        Block currentBlock = null;
        Biome currentBiome = null;

        int chunkMaxHeight = getMaxSurfaceHeight(baseChunkX,baseChunkZ,data);

        easeInDepth = chunkMaxHeight+4;

        float[][][] samples = sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.025f,1.2f,world,fNoise);
        float[][][] samples2 = sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.025f,1.5f,world,fNoise2);
        float[][][] samples3 = sampleNoise(baseChunkX,baseChunkZ,128,8,128,0.025f,1.5f,world,fNoise2);

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
                                    if (uberUtil.isRockBlock(currentBlock) || isBiomeBlock(Biome.FOREST,currentBlock))
                                    {
                                        depth++;
                                    }
                                } else
                                {
                                    // already hit surface, simply increment depth counter
                                    depth++;
                                }

                                float coreCavernNoiseCutoff = caveThresLowerCheese;
                                float adjustedCheeseNoiseCutoffBottomTop = caveThresLowerCheese;
                                float adjustedCheeseNoiseCutoffBetween = caveThresLowerCheese;

                                //World Core caves
                                if(localY < 32 && localY > 16) {
                                    coreCavernNoiseCutoff = uberUtil.clamp(coreCavernNoiseCutoff-((32 - localY) * 0.069f),0,.95f);
                                }
                                else if(localY <= 16) {
                                    coreCavernNoiseCutoff = uberUtil.clamp(coreCavernNoiseCutoff-(1-((16 - localY) * 0.04f)),0,1f);
                                }
                                if (localY < 14)
                                {
                                    coreCavernNoiseCutoff += (14 - localY) * 0.05;
                                }

                                /*if(localY < chunkMaxHeight)
                                {
                                    adjustedCheeseNoiseCutoffBetween = caveThresLowerCheese+(caveThresLowerCheeseDeep - caveThresLowerCheese)*((chunkMaxHeight+32)-localY)/chunkMaxHeight;
                                    if (localY < 30 && localY > 24)
                                    {
                                        adjustedCheeseNoiseCutoffBetween += (30 - localY) * 0.07;
                                    }
                                }*/

                                /*if (depth < easeInDepth)
                                {
                                    // higher threshold at surface, normal threshold below easeInDepth
                                    adjustedCheeseNoiseCutoffBottomTop = ClampedLerp(caveThresLowerCheese, surfaceCutoff, (easeInDepth - depth) / easeInDepth);
                                }*/

                                // increase cutoff as we get closer to the minCaveHeight so it's not all flat floors
                                if (localY < 32)
                                {
                                    adjustedCheeseNoiseCutoffBetween += (32 - localY) * 0.05;
                                }


                                boolean bedrockFlag = data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY] == (short) Block.bedrock.id;
                                boolean caveFlagNoodle = (caveThresUpperNoodle > noiseValNoodleOriginal && noiseValNoodleOriginal > caveThresLowerNoodle)&&(caveThresUpperNoodle > noiseValNoodleOriginalOffset && noiseValNoodleOriginalOffset > caveThresLowerNoodle);
                                boolean caveFlagCheese = noiseValCheese > adjustedCheeseNoiseCutoffBottomTop || noiseValCheese > adjustedCheeseNoiseCutoffBetween;
                                boolean caveFlagChambers = noiseValCheese > adjustedCheeseNoiseCutoffBetween;
                                boolean caveFlagCoreCavern = noiseValCheese > coreCavernNoiseCutoff;
                                boolean waterFlag = Block.getBlock(data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY]) instanceof BlockFluid;

                                //System.out.println(noiseValCheese+" "+adjustedCheeseNoiseCutoffBetween);
                                if ((caveFlagChambers||caveFlagCoreCavern||caveFlagNoodle)&&!bedrockFlag&&!waterFlag)
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
                                            currentBiome = Biome.FOREST;

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

    private void digBlock(short[] data , Biome biome, int localX,int localY,int localZ, Block block)
    {
        //Block top = Block.getBlock(biome.topBlock);
        //Block filler = Block.getBlock(biome.fillerBlock);

       // if (uberUtil.isRockBlock(block) || block == top || block == filler)
        //{
            if(localY<= lavaDepth)
            {
                data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY] = (short)Block.fluidLavaStill.id;
            } else
            {
                data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY]=0;

                /*if (foundTop && Block.getBlock(data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY-1]) == filler)
                {
                    data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | localY-1] = (short)top.blockID;
                }*/
            }
       // }
    }

    public float[][][] sampleNoise(int chunkX, int chunkZ, int offX,int offY,int offZ,float freq,float yCrunch, World world, FastNoiseLite tNoise)
    {
        float[][][] noiseSamples = new float[5][130][5];
        float noise;
        modifNoise.SetSeed((int) world.getRandomSeed());
        modifNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        modifNoise.SetFrequency(0.007f);

        tNoise.SetSeed((int) world.getRandomSeed());
        tNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        tNoise.SetFrequency(freq);
        //tNoise.SetFrequency(freq);

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

                    noise = tNoise.GetNoise(realX+offX,(realY+offY)*yCrunch,realZ+offZ);
                    noiseSamples[x][y][z] = noise;
                    /*if (noise < caveThresLowerCheese||noise < caveThresLowerNoodle)
                    {
                        // if noise is below cutoff, adjust values of neighbors helps prevent caves fracturing during interpolation
                        if (x > 0)
                            noiseSamples[x - 1][y][z] = (noise * 0.2f) + (noiseSamples[x - 1][y][z] * 0.8f);
                        if (z > 0)
                            noiseSamples[x][y][z - 1] = (noise * 0.2f) + (noiseSamples[x][y][z - 1] * 0.8f);
                        // more heavily adjust y above 'air block' noise values to give players more head room
                        /f (y < 128)
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
        return uberUtil.recursiveBinarySurfaceSearchUp(localX, localZ, Minecraft.WORLD_HEIGHT_BLOCKS-1, 0,data);
    }

    private boolean isBiomeBlock(Biome biome, Block block)
    {
        return block == Block.getBlock(biome.topBlock);
    }

    private boolean isTopBlock(Biome biome, Block block)
    {
        return block == Block.getBlock(biome.topBlock);
    }

    private boolean isFluidBlock(Block block)
    {
        return block instanceof BlockFluid;
    }

    public float smoothUnion(float a, float b, float delta) {
        float h = uberUtil.clamp(0.5F + 0.5F * (b - a) / delta, 0, 1);
        return uberUtil.lerp(b, a, h) - delta * h * (1 - h);
    }

    private float ClampedLerp(float a,float b,float t)
    {
        return uberUtil.clamp(uberUtil.lerp(a,b,t),-1,1);
    }
}