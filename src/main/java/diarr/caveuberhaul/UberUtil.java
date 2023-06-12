package diarr.caveuberhaul;

import net.minecraft.shared.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.BlockFluid;
import net.minecraft.src.Material;
import net.minecraft.src.World;

public class UberUtil
{

    public float[][][] getInterpolatedNoiseValue(float[][][] NoiseSamples)
    {
        //Issues seem to come from the y coordinate just leave it hard coded I guess lol. Also freezing caused by too inefficient code
        float[][][] vals = new float[16][Minecraft.WORLD_HEIGHT_BLOCKS][16];
        int xzScale = 4;
        float quarter = 0.25f;
        float half = 0.5f;

        for (int x = 0; x < xzScale; ++x) {
            for (int z = 0; z < xzScale; ++z) {
                //int depth =0;

                for (int y = Minecraft.WORLD_HEIGHT_BLOCKS/2-1; y >= 0; y--) {

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

                    for (int suby=1; suby>=0;suby--) {
                        int localY = suby + y * 2;

                        float noiseStartZ = noiseStartX0;
                        float noiseEndZ = noiseStartX1;

                        // how much to increment X values, linear interpolation
                        float noiseStepX0 = (noiseEndX0 - noiseStartX0) * quarter;
                        float noiseStepX1 = (noiseEndX1 - noiseStartX1) * quarter;

                        for (int subx = 0; subx < 4; subx++) {
                            int localX = subx+x*4;

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

    public float[][] getInterpolatedNoiseValue2D(float[][] NoiseSamples)
    {
        //Issues seem to come from the y coordinate just leave it hard coded I guess lol. Also freezing caused by too inefficient code
        float[][] vals = new float[16][16];
        int xzScale = 4;
        float quarter = 0.25f;

        for (int x = 0; x < xzScale; ++x) {
            for (int z = 0; z < xzScale; ++z) {

                float x0y0z0 = NoiseSamples[x][z];
                float x0y0z1 = NoiseSamples[x][z + 1];
                float x1y0z0 = NoiseSamples[x + 1][z];
                float x1y0z1 = NoiseSamples[x + 1][z + 1];

                // noise values of 4 corners at y=0
                float noiseStartX0 = x0y0z0;
                float noiseStartX1 = x0y0z1;
                float noiseEndX0 = x1y0z0;
                float noiseEndX1 = x1y0z1;

                float noiseStartZ = noiseStartX0;
                float noiseEndZ = noiseStartX1;

                // how much to increment X values, linear interpolation
                float noiseStepX0 = (noiseEndX0 - noiseStartX0) * quarter;
                float noiseStepX1 = (noiseEndX1 - noiseStartX1) * quarter;

                for (int subx = 0; subx < 4; subx++) {
                    int localX = subx+x*4;

                    // how much to increment Z values, linear interpolation
                    float noiseStepZ = (noiseEndZ - noiseStartZ) * quarter;

                    // Y and X already interpolated, just need to interpolate final 4 Z block to get final noise value
                    float noiseValue = noiseStartZ;

                    for (int subz = 0; subz < 4; subz++)
                    {
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

    public float[][][] sampleNoise(int chunkX, int chunkZ, int offX,int offY,int offZ,float freq,float yCrunch, World world, FastNoiseLite tNoise,FastNoiseLite.NoiseType nType)
    {
        float[][][] noiseSamples = new float[5][130][5];
        float noise;

        tNoise.SetSeed((int) world.getRandomSeed());
        tNoise.SetNoiseType(nType);
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

                    noise = tNoise.GetNoise(realX+offX,(realY+offY)*yCrunch,realZ+offZ);
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

    public float[][] sampleNoise2D(int chunkX, int chunkZ, float freq, World world, FastNoiseLite tNoise,FastNoiseLite.NoiseType nType)
    {
        float[][] noiseSamples = new float[5][5];
        float noise;

        tNoise.SetSeed((int) world.getRandomSeed());
        tNoise.SetNoiseType(nType);
        tNoise.SetFrequency(freq);
        //tNoise.SetFrequency(freq);

        for (int x = 0; x < 5; x++)
        {
            int realX = x * 4 + chunkX * 16;
            for (int z = 0; z < 5; z++)
            {
                int realZ = z * 4 + chunkZ * 16;

                // loop from top down for y values so we can adjust noise above current y later on
                noise = tNoise.GetNoise(realX,realZ);
                noiseSamples[x][z] = noise;
            }
        }
        return noiseSamples;
    }

    // Recursive binary search, this search always converges on the surface in 8 in cycles for the range 255 >= y >= 0
    public int recursiveBinarySurfaceSearchUp(int localX, int localZ, int searchTop, int searchBottom, short[] data)
    {
        int top = searchTop;
        if (searchTop > searchBottom)
        {
            int searchMid = (searchBottom + searchTop) / 2;
            if (isRockBlock(Block.getBlock(data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | searchMid])))
            {
                top = recursiveBinarySurfaceSearchUp(localX, localZ, searchTop, searchMid + 1,data);
            } else
            {
                top = recursiveBinarySurfaceSearchUp(localX, localZ, searchMid, searchBottom,data);
            }
        }
        return top;
    }

    public boolean isRockBlock(Block block)
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

    public boolean solidBlockExists(int x,int y,int z, World world)
    {
        return !world.isAirBlock(x,y,z) && !(Block.getBlock(world.getBlockId(x,y,z)) instanceof BlockFluid);
    }
    public boolean solidBlockExistsNoBedrock(int x,int y,int z, World world)
    {
        return solidBlockExists(x,y,z,world) && Block.getBlock(world.getBlockId(x,y,z)).blockMaterial == Material.rock;
    }
    public float lerp(float a, float b, float t)
    {
        return a+(b-a)*t;
    }
    public float lerp(float a, float b, double t)
    {
        return (float) (a+(b-a)*t);
    }
    public float lerp(int a, int b, float t)
    {
        return (a+(b-a)*t);
    }
    public float lerp(int a, int b, double t)
    {
        return (float)(a+(b-a)*t);
    }
    public int clampedLerp(int a, int b, int t,int min, int max)
    {
        return clamp((a+(b-a)*t),min,max);
    }
    public float clampedLerp(float a, float b, double t,float min, float max)
    {
        return clamp(lerp(a,b,t),min,max);
    }
    public float clampedLerp(int a, int b, double t,int min, int max)
    {
        return clamp(lerp(a,b,t),min,max);
    }
    public float clamp(float val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }
    public double clamp(double val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }

    public int clamp(int val, int min, int max) {
        return val < min ? min : val > max ? max : val;
    }

    public double distanceAB(int x1,int y1,int z1,int x2, int y2, int z2)
    {
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2)+Math.pow(z1-z2,2));
    }

    public boolean isSurroundedAndFreeAbove(int x, int y, int z, World world)
    {
        return (isNeitherAirNorWater(x-1,y,z,world) && isNeitherAirNorWater(x+1,y,z,world)  && isNeitherAirNorWater(x,y,z-1,world)  && isNeitherAirNorWater(x,y,z+1,world)  && world.isAirBlock(x,y+1,z));
    }

    public boolean isSurroundedFreeAboveNoLava(int x, int y, int z, World world)
    {
        return (isNeitherAirNorLava(x-1,y,z,world) && isNeitherAirNorLava(x+1,y,z,world)  && isNeitherAirNorLava(x,y,z-1,world)  && isNeitherAirNorLava(x,y,z+1,world)  && world.isAirBlock(x,y+1,z));
    }

    public boolean isNeitherAirNorWater(int x,int y,int z, World world)
    {
        return !(world.isAirBlock(x,y,z) || Block.getBlock(world.getBlockId(x,y,z)).blockMaterial == Material.water);
    }

    public boolean isNeitherAirNorLava(int x, int y, int z, World world)
    {
        return !(world.isAirBlock(x,y,z) || Block.getBlock(world.getBlockId(x,y,z)).blockMaterial == Material.lava);
    }

    public int GetFloor(int x,int y,int z,int limit, World world)
    {
        if(world.isAirBlock(x,y,z))
        {
            for (int height = y; height >= y - limit; height--) {
                if (height>9&&world.isAirBlock(x,height,z) && solidBlockExists(x,height-1,z,world)) {
                    return height;
                }
            }
        }
        return 0;
    }


    public int GetCeiling(int x,int y,int z,int limit,World world)
    {
        if(world.isAirBlock(x,y,z))
        {
            for (int height = y; height <= y + limit; height++) {
                if (height>9&&height<Minecraft.WORLD_HEIGHT_BLOCKS&&world.isAirBlock(x,height,z) && solidBlockExists(x,height+1,z,world)) {
                    return height;
                }
            }
        }
        return 0;
    }

    public int GetPillarBlock(int x,int y,int z,World world)
    {
        int blockId = world.getBlockId(x,y,z);
        Block block = Block.getBlock(blockId);
        if(block == Block.basalt||block == Block.stone||block == Block.granite)
        {
            return blockId;
        }
        else if(block == CaveUberhaul.flowstone)
        {
            return CaveUberhaul.flowstonePillar.blockID;
        }
        else
        {
            return 1;
        }
    }

}
