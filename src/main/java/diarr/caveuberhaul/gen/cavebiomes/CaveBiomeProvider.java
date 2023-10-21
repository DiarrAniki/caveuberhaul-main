package diarr.caveuberhaul.gen.cavebiomes;

import diarr.caveuberhaul.UberUtil;
import diarr.caveuberhaul.gen.FastNoiseLite;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkPosition;

public class CaveBiomeProvider
{
    private final CaveBiome[] caveBiomesInChunk;
    private static final FastNoiseLite temperatureNoise = new FastNoiseLite();
    private static final FastNoiseLite weirdNoise = new FastNoiseLite();

    public CaveBiomeProvider(World world,int chunkX, int chunkZ)
    {
        caveBiomesInChunk = new CaveBiome[256*world.getHeightBlocks()];
        generateBiomesThisChunk(world,chunkX,chunkZ);
        CaveBiomeChunkMap.map.put(new ChunkPosition(chunkX,0,chunkZ),this);
        temperatureNoise.SetSeed((int)world.getRandomSeed());
        weirdNoise.SetSeed((int) world.getRandomSeed());
    }
    private void generateBiomesThisChunk(World world, int chunkX, int chunkZ)
    {
        double weird;
        double temperature;

        float[][][] tempVals = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(chunkX,chunkZ,0,0,0,0.04f,1.5f,world, temperatureNoise ,FastNoiseLite.NoiseType.Perlin),world);
        float[][][] weirdVals = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(chunkX,chunkZ,0,0,0,0.06f,1.5f,world, weirdNoise ,FastNoiseLite.NoiseType.Perlin),world);
        for(int x = 0; x<16;x++)
        {
            for(int z = 0; z<16;z++)
            {
                for(int y = world.getHeightBlocks()-1; y>0;y--)
                {
                    weird = weirdVals[x][y][z];
                    temperature = tempVals[x][y][z];
                    for (int b = 0; b< CaveBiomes.caveBiomeList.size();b++)
                    {
                        CaveBiome cb = CaveBiomes.caveBiomeList.get(b);
                        if(checkIfIsBiome(cb,temperature,weird))
                        {
                            caveBiomesInChunk[x << world.getHeightBits() + 4 | z << world.getHeightBits() | y] = cb;
                        }
                    }
                }
            }
        }
    }

    public CaveBiome getCaveBiomeAt(int x,int y,int z,World world)
    {
        return caveBiomesInChunk[x << world.getHeightBits() + 4 | z << world.getHeightBits() | y];
    }

    private boolean checkIfIsBiome(CaveBiome b,double t, double w)
    {
        return inRange(t,b.minTemp,b.maxTemp)&&inRange(w,b.minWeird,b.maxWeird);
    }

    private boolean inRange(double val,double min, double max)
    {
        return val>=min&&val<=max;
    }
}
