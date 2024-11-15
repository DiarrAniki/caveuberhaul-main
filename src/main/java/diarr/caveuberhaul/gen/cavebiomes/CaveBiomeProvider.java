package diarr.caveuberhaul.gen.cavebiomes;

import diarr.caveuberhaul.UberUtil;
import diarr.caveuberhaul.gen.FastNoiseLite;
import net.minecraft.client.Minecraft;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.ChunkPosition;
import net.minecraft.core.world.chunk.ChunkSection;

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
    public static final List<Biome> tropicBiomes = new ArrayList<>();
    static {
        tropicBiomes.add(Biomes.OVERWORLD_RAINFOREST);
        tropicBiomes.add(Biomes.OVERWORLD_CAATINGA);
        tropicBiomes.add(Biomes.OVERWORLD_CAATINGA_PLAINS);
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

        float[][] tempVals = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(chunkX,chunkZ,0.002f,world, temperatureNoise ,FastNoiseLite.NoiseType.Value));
        float[][] weirdVals = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(chunkX,chunkZ,0.004f,world, weirdNoise ,FastNoiseLite.NoiseType.Value));
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
                    if(frostyBiomes.contains(biome) && y>((world.getHeightBlocks()*CaveBiomes.CAVE_FROST.minHeight)+world.rand.nextInt(4)))
                    {
                        caveBiomesInChunk[ChunkSection.makeBlockIndex(x, y, z)] = CaveBiomes.CAVE_FROST;
                    }
                    if(tropicBiomes.contains(biome) && y>((world.getHeightBlocks()*CaveBiomes.CAVE_JUNGLE.minHeight)+world.rand.nextInt(4))&&y<((world.getHeightBlocks()*CaveBiomes.CAVE_JUNGLE.maxHeight)-world.rand.nextInt(4)))
                    {
                        caveBiomesInChunk[ChunkSection.makeBlockIndex(x, y, z)] = CaveBiomes.CAVE_JUNGLE;
                    }
                    for (int b = 0; b< CaveBiomes.caveBiomeList.size();b++)
                    {
                        CaveBiome cb = CaveBiomes.caveBiomeList.get(b);
                        if(checkIfIsBiome(cb,temperature,weird,y,world))
                        {
                            caveBiomesInChunk[ChunkSection.makeBlockIndex(x, y, z)] = cb;
                        }
                    }
                }
            }
        }
    }

    public CaveBiome getCaveBiomeAt(int x,int y,int z)
    {
        return caveBiomesInChunk[ChunkSection.makeBlockIndex(x, y, z)];
    }

    public CaveBiome[] getCaveBiomesInChunk()
    {
        return caveBiomesInChunk;
    }

    private boolean checkIfIsBiome(CaveBiome b,double t, double w,int y,World world)
    {
        return UberUtil.inRange(t,b.minTemp,b.maxTemp)&&UberUtil.inRange(w,b.minWeird,b.maxWeird)&&UberUtil.inRange(y,b.minHeight*(world.getHeightBlocks()*0.5f) ,b.maxHeight*(world.getHeightBlocks()*0.5f));
    }


}
