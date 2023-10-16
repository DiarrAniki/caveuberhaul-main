package diarr.caveuberhaul.gen;

import diarr.caveuberhaul.Profiler;
import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.world.World;

public class CaveBiomeProvider
{
    private static int offsetXZ=256;
    private static int offsetY = 128;
    private static float yCrunch = 0.75f;
    //private UberUtil uberUtil = new UberUtil();
    //private static FastNoiseLite depthNoise = new FastNoiseLite();
    private static FastNoiseLite wetNoise = new FastNoiseLite();
    private static FastNoiseLite weirdNoise = new FastNoiseLite();

    public static int getCaveBiomeAt(int x,int y,int z)
    {
        double wet = wetNoise.GetNoise(x,y*yCrunch,z);
        double weird = weirdNoise.GetNoise(x+offsetXZ,(y+offsetY)*yCrunch,z+offsetXZ);
        return caveBiomeNoiseValuesToInt(wet,weird);
    }

    public int[] provideCaveBiomeValueChunk(int chunkX, int chunkZ, World world)
    {
        Profiler.methodStart("provideCaveBiomeValueChunk");
        //TODO Rework: Generiert nur 18 Chunks in postive und 26 Chunks in negative Richtung
        //Loop through chunk, interpret 2 noise values into CaveBiome Value. Save Cavebiome Value as int into array for usage.
        int[] BiomeInts = new int[256*world.getHeightBlocks()];

        float[][][] wet = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(chunkX,chunkZ,0,0,0,0.003f,yCrunch,world, wetNoise, FastNoiseLite.NoiseType.OpenSimplex2S),world);
        float[][][] weird = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(chunkX,chunkZ,offsetXZ,offsetY,offsetXZ,0.006f,yCrunch,world, weirdNoise, FastNoiseLite.NoiseType.OpenSimplex2S),world);
        //float[][][] depth = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(chunkX,chunkZ,0,0,0,0.012f,0.4f,world, depthNoise, FastNoiseLite.NoiseType.OpenSimplex2S));

        for(int x = 0; x<16;x++)
        {
            for(int z = 0; z<16;z++)
            {
                for(int y = 0; y<world.getHeightBlocks()-1;y++)
                {
                    //BiomeInts[x][y][z] = CaveBiomeNoiseValuesToInt(wetness[x][y][z],weirdness[x][y][z]);
                    BiomeInts[x << world.getHeightBits() + 4 | z << world.getHeightBits()| y] = caveBiomeNoiseValuesToInt(wet[x][y][z],weird[x][y][z]);
                }
            }
        }
        Profiler.methodEnd("provideCaveBiomeValueChunk");
        return BiomeInts;
    }



    private static int caveBiomeNoiseValuesToInt(double wet, double weird) {
        //figure out why noise behaves weird
            if (wet > 0.5F && weird < 0)
            {
                    return 1;
            }
            return 0;
    }
}
