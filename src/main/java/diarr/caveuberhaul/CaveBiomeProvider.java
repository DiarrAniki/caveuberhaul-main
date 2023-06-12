package diarr.caveuberhaul;

import net.minecraft.shared.Minecraft;
import net.minecraft.src.World;

public class CaveBiomeProvider
{
    private UberUtil uberUtil = new UberUtil();
    private static FastNoiseLite depthNoise = new FastNoiseLite();

    public int[][][] provideCaveBiomeValueChunk(int chunkX, int chunkZ, World world)
    {

        //Loop through chunk, interpret 2 noise values into CaveBiome Value. Save Cavebiome Value as int into array for usage.
        int[][][] BiomeInts = new int[16][Minecraft.WORLD_HEIGHT_BLOCKS][16];

        float[][][] depth = uberUtil.getInterpolatedNoiseValue(uberUtil.sampleNoise(chunkX,chunkZ,0,0,0,0.012f,0.4f,world, depthNoise, FastNoiseLite.NoiseType.Perlin));

        for(int x = 0; x<16;x++)
        {
            for(int z = 0; z<16;z++)
            {
                double temp = world.getBlockTemperature(x,z);
                double humid =world.getBlockHumidity(x,z);
                for(int y = 0; y<Minecraft.WORLD_HEIGHT_BLOCKS-1;y++)
                {
                    //BiomeInts[x][y][z] = CaveBiomeNoiseValuesToInt(wetness[x][y][z],weirdness[x][y][z]);
                    BiomeInts[x][y][z] = caveBiomeNoiseValuesToInt(temp,humid,depth[x][y][z]);
                }
            }
        }
        return BiomeInts;
    }



    private int caveBiomeNoiseValuesToInt(double temp, double humid, float depthMap) {
        //figure out why noise behaves weird
            if (humid > 0 && temp > 0 && depthMap > -1)
            {
                    return 1;
            }
            return 0;
    }
}
