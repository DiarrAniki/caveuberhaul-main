package diarr.caveuberhaul.gen.cavebiomes;

import diarr.caveuberhaul.UberUtil;
import diarr.caveuberhaul.gen.FastNoiseLite;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.ChunkPosition;

import java.util.ArrayList;
import java.util.List;

public class CaveBiomeProvider
{
    private final CaveBiome[] caveBiomesInChunk;
    private static final FastNoiseLite temperatureNoise = new FastNoiseLite();
    private static final FastNoiseLite weirdNoise = new FastNoiseLite();
    public static final List<Biome> frostyBiomes = new ArrayList<>();
    static {
        frostyBiomes.add(Biomes.OVERWORLD_GLACIER);
        frostyBiomes.add(Biomes.OVERWORLD_TAIGA);
        frostyBiomes.add(Biomes.OVERWORLD_TUNDRA);
    }

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

        float[][] tempVals = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(chunkX,chunkZ,0.002f,world, temperatureNoise ,FastNoiseLite.NoiseType.Perlin));
        float[][] weirdVals = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(chunkX,chunkZ,0.004f,world, weirdNoise ,FastNoiseLite.NoiseType.OpenSimplex2S));
        for(int x = 0; x<16;x++)
        {
            int gx = x+chunkX*16;
            for(int z = 0; z<16;z++)
            {
                int gz = z+chunkZ*16;
                Biome biome = world.getBlockBiome(gx,0,gz);
                weird = (1+weirdVals[x][z])/2;
                temperature = (1+tempVals[x][z])/2;
                for(int y = world.getHeightBlocks()-1; y>0;y--)
                {
                    if(frostyBiomes.contains(biome) && y>((world.getHeightBlocks()/4)-world.rand.nextInt(4)))
                    {
                        caveBiomesInChunk[x << world.getHeightBits() + 4 | z << world.getHeightBits() | y] = CaveBiomes.CAVE_FROST;
                    }
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
        return min<val&&max>val;
    }
}
