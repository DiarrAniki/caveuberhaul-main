package diarr.caveuberhaul;

import diarr.caveuberhaul.blocks.BlockStalagmite;
import diarr.caveuberhaul.blocks.BlockStalagtite;
import diarr.caveuberhaul.gen.FastNoiseLite;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.block.BlockLeavesBase;
import net.minecraft.core.block.BlockLog;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkSection;

public class UberUtil {
    public static float[][][] getInterpolatedNoiseValue(float[][][] NoiseSamples, World world) {
        //Issues seem to come from the y coordinate just leave it hard coded I guess lol. Also freezing caused by too inefficient code
        float[][][] vals = new float[16][world.getHeightBlocks()][16];
        int xzScale = 4;
        float quarter = 0.25f;
        float half = 0.5f;

        for (int x = 0; x < xzScale; ++x) {
            for (int z = 0; z < xzScale; ++z) {
                //int depth =0;

                for (int y = world.getHeightBlocks() / 2 - 1; y >= 0; y--) {

                    float x0y0z0 = NoiseSamples[x][y][z];
                    float x0y0z1 = NoiseSamples[x][y][z + 1];
                    float x1y0z0 = NoiseSamples[x + 1][y][z];
                    float x1y0z1 = NoiseSamples[x + 1][y][z + 1];
                    float x0y1z0 = NoiseSamples[x][y + 1][z];
                    float x0y1z1 = NoiseSamples[x][y + 1][z + 1];
                    float x1y1z0 = NoiseSamples[x + 1][y + 1][z];
                    float x1y1z1 = NoiseSamples[x + 1][y + 1][z + 1];

                    // how much to increment noise along y value linear interpolation from start y and end y
                    float noiseStepY00 = (x0y1z0 - x0y0z0) * -half;
                    float noiseStepY01 = (x0y1z1 - x0y0z1) * -half;
                    float noiseStepY10 = (x1y1z0 - x1y0z0) * -half;
                    float noiseStepY11 = (x1y1z1 - x1y0z1) * -half;

                    // noise values of 4 corners at y=0
                    float noiseStartX0 = x0y0z0;
                    float noiseStartX1 = x0y0z1;
                    float noiseEndX0 = x1y0z0;
                    float noiseEndX1 = x1y0z1;

                    for (int suby = 1; suby >= 0; suby--) {
                        int localY = suby + y * 2;

                        float noiseStartZ = noiseStartX0;
                        float noiseEndZ = noiseStartX1;

                        // how much to increment X values, linear interpolation
                        float noiseStepX0 = (noiseEndX0 - noiseStartX0) * quarter;
                        float noiseStepX1 = (noiseEndX1 - noiseStartX1) * quarter;

                        for (int subx = 0; subx < 4; subx++) {
                            int localX = subx + x * 4;

                            // how much to increment Z values, linear interpolation
                            float noiseStepZ = (noiseEndZ - noiseStartZ) * quarter;

                            // Y and X already interpolated, just need to interpolate final 4 Z block to get final noise value
                            float noiseValue = noiseStartZ;

                            for (int subz = 0; subz < 4; subz++) {
                                int localZ = subz + z * 4;

                                noiseValue += noiseStepZ;
                                vals[localX][localY][localZ] = noiseValue;

                            }
                            noiseStartZ += noiseStepX0;
                            noiseEndZ += noiseStepX1;

                        }
                        noiseStartX0 += noiseStepY00;
                        noiseStartX1 += noiseStepY01;
                        noiseEndX0 += noiseStepY10;
                        noiseEndX1 += noiseStepY11;
                    }
                }
            }
        }
        return vals;
    }

    public static float[][] getInterpolatedNoiseValue2D(float[][] NoiseSamples) {
        //Issues seem to come from the y coordinate just leave it hard coded I guess lol. Also freezing caused by too inefficient code
        float[][] vals = new float[16][16];
        int xzScale = 4;
        float quarter = 0.25f;

        for (int x = 0; x < xzScale; ++x) {
            for (int z = 0; z < xzScale; ++z) {

                float x0y0z0 = NoiseSamples[x][z];
                float x0y0z1 = NoiseSamples[x][z + 1];
                float x1y0z0 = NoiseSamples[x + 1][z];

                // noise values of 4 corners at y=0
                float noiseEndX1 = NoiseSamples[x + 1][z + 1];

                float noiseStartZ = x0y0z0;
                float noiseEndZ = x0y0z1;

                // how much to increment X values, linear interpolation
                float noiseStepX0 = (x1y0z0 - x0y0z0) * quarter;
                float noiseStepX1 = (noiseEndX1 - x0y0z1) * quarter;

                for (int subx = 0; subx < 4; subx++) {
                    int localX = subx + x * 4;

                    // how much to increment Z values, linear interpolation
                    float noiseStepZ = (noiseEndZ - noiseStartZ) * quarter;

                    // Y and X already interpolated, just need to interpolate final 4 Z block to get final noise value
                    float noiseValue = noiseStartZ;

                    for (int subz = 0; subz < 4; subz++) {
                        int localZ = subz + z * 4;

                        noiseValue += noiseStepZ;
                        vals[localX][localZ] = noiseValue;

                    }
                    noiseStartZ += noiseStepX0;
                    noiseEndZ += noiseStepX1;

                }
            }
        }
        return vals;
    }

    public static float[][][] sampleNoise(int chunkX, int chunkZ, int offX, int offY, int offZ, float freq, float yCrunch, World world, FastNoiseLite tNoise, FastNoiseLite.NoiseType nType) {
        float[][][] noiseSamples = new float[5][130][5];
        float noise;

        tNoise.SetSeed((int) world.getRandomSeed());
        tNoise.SetNoiseType(nType);
        tNoise.SetFrequency(freq);

        for (int x = 0; x < 5; x++) {
            int realX = x * 4 + chunkX * 16;
            for (int z = 0; z < 5; z++) {
                int realZ = z * 4 + chunkZ * 16;

                // loop from top down for y values so we can adjust noise above current y later on
                for (int y = world.getHeightBlocks() / 2; y >= 0; y--) {
                    float realY = y * 2;

                    noise = tNoise.GetNoise(realX + offX, (realY + offY) * yCrunch, realZ + offZ);
                    noiseSamples[x][y][z] = noise;
                    /*if (noise < coreThresCheese||noise < caveThresLowerNoodle)
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

    public static float[][] sampleNoise2D(int chunkX, int chunkZ, float freq, World world, FastNoiseLite tNoise, FastNoiseLite.NoiseType nType) {
        float[][] noiseSamples = new float[5][5];
        float noise;

        tNoise.SetSeed((int) world.getRandomSeed());
        tNoise.SetNoiseType(nType);
        tNoise.SetFrequency(freq);
        //tNoise.SetFrequency(freq);

        for (int x = 0; x < 5; x++) {
            int realX = x * 4 + chunkX * 16;
            for (int z = 0; z < 5; z++) {
                int realZ = z * 4 + chunkZ * 16;

                // loop from top down for y values so we can adjust noise above current y later on
                noise = tNoise.GetNoise(realX, realZ);
                noiseSamples[x][z] = noise;
            }
        }
        return noiseSamples;
    }

    // Recursive binary search, this search always converges on the surface in 8 in cycles for the range 255 >= y >= 0
    public static int recursiveBinarySurfaceSearchUp(int localX, int localZ, int searchTop, int searchBottom, short[] data, World world) {
        int top = searchTop;
        if (searchTop > searchBottom) {
            int searchMid = (searchBottom + searchTop) / 2;
            if (isRockBlock(Block.getBlock(data[ChunkSection.makeBlockIndex(localX, searchMid, localZ)]))) {
                top = recursiveBinarySurfaceSearchUp(localX, localZ, searchTop, searchMid + 1, data, world);
            } else {
                top = recursiveBinarySurfaceSearchUp(localX, localZ, searchMid, searchBottom, data, world);
            }
        }
        return top;
    }

    public static boolean isRockBlock(Block block) {
        // Replace anything that's made of rock which should hopefully work for most modded type stones (and maybe not break everything)
        if (block == null) {
            return false;
        } else {
            return block.blockMaterial == Material.stone;
        }
    }

    public static boolean solidBlockExists(int x, int y, int z, World world) {
        return !world.isAirBlock(x, y, z) && !(world.getBlock(x, y, z) instanceof BlockFluid);
    }

    public static boolean solidBlockExistsNoBedrock(int x, int y, int z, World world) {
        return solidBlockExists(x, y, z, world) && world.getBlock(x, y, z).blockMaterial == Material.stone;
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static float lerp(float a, float b, double t) {
        return (float) (a + (b - a) * t);
    }

    public static float lerp(int a, int b, float t) {
        return (a + (b - a) * t);
    }
    public static float lerp(double a, double b, float t) {
        return (float) (a + (b - a) * t);
    }

    public static float lerp(int a, int b, double t) {
        return (float) (a + (b - a) * t);
    }
    public static int clampedLerp(int a, int b, int t,int min, int max)
    {
        return clamp((a+(b-a)*t),min,max);
    }
    public  static float clampedLerp(float a, float b, double t,float min, float max)
    {
        return clamp(lerp(a,b,t),min,max);
    }
    public  static float clampedLerp(float a, float b, float t,float min, float max)
    {
        return clamp(lerp(a,b,t),min,max);
    }
    public static float clampedLerp(int a, int b, double t,int min, int max)
    {
        return clamp(lerp(a,b,t),min,max);
    }
    public static float clamp(float val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }
    public static double clamp(double val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }

    public static int clamp(int val, int min, int max) {
        return val < min ? min : val > max ? max : val;
    }

    public static double distanceAB(int x1,int y1,int z1,int x2, int y2, int z2)
    {
        int difX = x1 - x2;
        int difY = y1 - y2;
        int difZ = z1 - z2;
        return Math.sqrt(difX * difX + difY * difY + difZ * difZ);
    }

    public static boolean isSurroundedAndFreeAbove(int x, int y, int z, World world)
    {
        return (isNeitherAirNorWater(x-1,y,z,world)
                && isNeitherAirNorWater(x+1,y,z,world)
                && isNeitherAirNorWater(x,y,z-1,world)
                && isNeitherAirNorWater(x,y,z+1,world)
                && world.isAirBlock(x,y+1,z));
    }

    public static boolean isSurroundedFreeAboveNoLava(int x, int y, int z, World world)
    {
        return (isNeitherAirNorLava(x-1,y,z,world) && isNeitherAirNorLava(x+1,y,z,world)  && isNeitherAirNorLava(x,y,z-1,world)  && isNeitherAirNorLava(x,y,z+1,world)  && world.isAirBlock(x,y+1,z));
    }

    public static boolean isSurrounded(int x,int y,int z, World world)
    {
        return isSolid(x-1,y,z,world)&&isSolid(x+1,y,z,world)&&isSolid(x,y,z-1,world)&&isSolid(x,y,z+1,world)&&isSolid(x,y-1,z,world)&&isSolid(x,y+1,z,world);
    }

    public static boolean isSolid(int x,int y,int z, World world)
    {
        return !world.isAirBlock(x,y,z)&&world.getBlock(x,y,z).blockMaterial.isSolid();
    }

    public static boolean isNeitherAirNorWater(int x,int y,int z, World world)
    {
        return !(world.isAirBlock(x,y,z) || world.getBlock(x,y,z).blockMaterial == Material.water);
    }

    public static boolean isNeitherAirNorLava(int x, int y, int z, World world)
    {
        return !(world.isAirBlock(x,y,z) || world.getBlock(x,y,z).blockMaterial == Material.lava);
    }

    public static int getFloor(int x, int y, int z, int limit, World world)
    {
        if(world.isAirBlock(x,y,z))
        {
            for (int height = y; height >= y - limit; height--) {
                if (height>9&&world.isAirBlock(x,height,z) && solidBlockExists(x,height-1,z,world)&&!(world.getBlock(x,height-1,z)instanceof BlockLeavesBase)) {
                    return height;
                }
            }
        }
        return 0;
    }


    public static int getCeiling(int x, int y, int z, int limit, World world)
    {
        if(world.isAirBlock(x,y,z))
        {
            for (int height = y; height <= y + limit; height++) {
                if (height>9&&height<world.getHeightBlocks()&&world.isAirBlock(x,height,z) && solidBlockExists(x,height+1,z,world)&&!(world.getBlock(x,height+1,z)instanceof BlockLeavesBase)) {
                    return height;
                }
            }
        }
        return 0;
    }

    public static int getMaxSurfaceHeight(short[] data, World world)
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

    public static int getSurfaceHeight(int localX, int localZ,short[] data,World world)
    {
        // Using a recursive binary search to find the surface
        return UberUtil.recursiveBinarySurfaceSearchUp(localX, localZ, world.getHeightBlocks()-1, 0,data,world);
    }

    public static int getPillarBlock(int x, int y, int z, World world)
    {
        int blockId = world.getBlockId(x,y,z);
        Block block = Block.getBlock(blockId);

        if(block == Block.basalt||block == Block.stone||block == Block.granite||block == Block.limestone|| block == Block.obsidian)
        {
            return blockId;
        }
        else if(block == CaveUberhaul.flowstone||block == CaveUberhaul.flowstonePillar||block instanceof BlockStalagmite || block instanceof BlockStalagtite)
        {
            return CaveUberhaul.flowstonePillar.id;
        }
        else if(block instanceof BlockLeavesBase ||block instanceof BlockLog)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
    public static void setBlockDirectely(Chunk chunk, int x, int y, int z, int id){
        chunk.getSection(y/ChunkSection.SECTION_SIZE_Y).setBlock(x, y % 16, z, id);
    }
}
