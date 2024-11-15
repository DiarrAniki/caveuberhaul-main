package diarr.caveuberhaul.gen;

import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkSection;
import net.minecraft.core.world.generate.MapGenBase;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;

public class MapGenNoiseCaves extends MapGenBase {
//TODO: Höhlen fluten wenn auf Wasser quelle getroffen wird.
    private static final int lavaDepth = 10;

    protected World worldObj;

    private static final float coreThresCheese = 0.5f;
    private static final float caveThresWorm = 0.053f;
    private static final float caveThresNoodle = 0.085f;

    private static final FastNoiseLite cavernNoise = new FastNoiseLite();
    private static final FastNoiseLite wormCaveNoise = new FastNoiseLite();
    private static final FastNoiseLite caveModifierNoise = new FastNoiseLite();
    private static final FastNoiseLite pillarNoise = new FastNoiseLite();


    public MapGenNoiseCaves(boolean isAlpha) {

    }

    public void generate(World world, int baseChunkX, int baseChunkZ, ChunkGeneratorResult result)
    {
        this.worldObj = world;
        generateNoiseCaves(worldObj,baseChunkX, baseChunkZ, result);
    }

    private void generateNoiseCaves(World world,int baseChunkX,int baseChunkZ, ChunkGeneratorResult data)
    {
        float[][][] CheeseCave = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.021f,1.8f,world, cavernNoise, FastNoiseLite.NoiseType.Perlin),world);
        float[][][] WormCave = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoiseDomainWarp(baseChunkX,baseChunkZ,0,0,0,0.01f,1.3f,.4f,world, wormCaveNoise, FastNoiseLite.NoiseType.Value),world);
        float[][][] WormCaveOffset = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoiseDomainWarp(baseChunkX,baseChunkZ,128,32,128,0.01f,1.3f,.4f,world, wormCaveNoise, FastNoiseLite.NoiseType.Value),world);
        float[][][] NoodleCave = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,0,0,0,0.03f,1.5f,world, cavernNoise, FastNoiseLite.NoiseType.Value),world);
        float[][][] NoodleCaveOffset = UberUtil.getInterpolatedNoiseValue(UberUtil.sampleNoise(baseChunkX,baseChunkZ,64,32,64,0.03f,1.5f,world, cavernNoise, FastNoiseLite.NoiseType.Value),world);
        float[][] ModifierNoise = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(baseChunkX,baseChunkZ,0.005f,world, caveModifierNoise, FastNoiseLite.NoiseType.Perlin));
        float[][] PillarNoise = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(baseChunkX,baseChunkZ,0.07f,world, pillarNoise, FastNoiseLite.NoiseType.Value));

        for (int s = 0; s < Chunk.CHUNK_SECTIONS; s++) {
            for (int x = 0; x < Chunk.CHUNK_SIZE_X; ++x) {
                for (int z = 0; z < Chunk.CHUNK_SIZE_Z; ++z) {
                    double cheeseModif = UberUtil.clamp(ModifierNoise[x][z],-0.1f,0.14f);
                    double noodleModif = UberUtil.clamp(ModifierNoise[x][z],-0.01f,0.1f);
                    double coreHeight = UberUtil.clamp(ModifierNoise[x][z],-0.5f,0.5f);
                    double pillarModif = WormCave[x][0][z];
                    int coreCavernBlockHeight = (int) (32+8*(1+coreHeight));
                    for (int _y = ChunkSection.SECTION_SIZE_Y - 1; _y >= 0; _y--) {
                        int y = s * ChunkSection.SECTION_SIZE_Y + _y;
                        float noiseValCheese = CheeseCave[x][y][z];

                        float noiseValWormCave = Math.abs(WormCave[x][y][z]);
                        float noiseValWormCaveOffset = Math.abs(WormCaveOffset[x][y][z]);

                        float noiseValNoodleCave = Math.abs(NoodleCave[x][y][z]);
                        float noiseValNoodleCaveOffset = Math.abs(NoodleCaveOffset[x][y][z]);

                        float pillarNoiseValue = PillarNoise[x][z];
                        float pillarStrengthValue = (float)(Math.pow(pillarNoiseValue,2)*(7+4*pillarModif));



                        if(y < coreCavernBlockHeight)
                        {
                            noiseValCheese = UberUtil.lerp(noiseValCheese,1,(coreCavernBlockHeight-y)/coreCavernBlockHeight);
                        }

                        noiseValNoodleCave += noodleModif;
                        noiseValNoodleCaveOffset += noodleModif;
                        noiseValCheese += cheeseModif;

                        if(y>world.getHeightBlocks()/2-16)
                        {
                            noiseValCheese *= 0.75f;
                            noiseValNoodleCave *= 1.2f;
                            noiseValNoodleCaveOffset *= 1.2f;
                        }

                        float pillarVal;
                        float pillarNormalized = 0;
                        if(pillarNoiseValue>0.68f+(0.125*pillarModif))
                        {
                            pillarVal = pillarStrengthValue-(float)Math.pow((noiseValCheese+(.82f+(0.1*pillarModif))),2);
                            pillarNormalized = (float)(pillarVal/(7+4*pillarModif));
                        }



                        boolean caveFlagWorm =noiseValWormCave < caveThresWorm && noiseValWormCaveOffset < caveThresWorm;
                        boolean caveFlagNoodle = noiseValNoodleCave < caveThresNoodle && noiseValNoodleCaveOffset < caveThresNoodle;
                        boolean caveFlagCoreCavern = noiseValCheese > coreThresCheese;
                        boolean pillar = pillarNormalized > 0.4f;

                        int block = data.getBlock(x, y, z);

                        boolean bedrockFlag = block == (short) Block.bedrock.id;
                        boolean waterFlag = Block.getBlock(block) instanceof BlockFluid || block == (short)Block.ice.id;

                        if ((caveFlagCoreCavern||caveFlagNoodle||caveFlagWorm)&&!pillar&&!bedrockFlag&&!waterFlag)
                        {
                            digBlock(data, x,y,z);
                        }
                    }
                }
            }
        }

    }

    private void digBlock(ChunkGeneratorResult result, int localX,int localY,int localZ)
    {
        if(localY<= lavaDepth)
        {
            result.setBlock(localX, localY, localZ, Block.fluidLavaStill.id);
        } else
        {
            result.setBlock(localX, localY, localZ, 0);
            if (result.getBlock(localX, localY - 1, localZ) == Block.dirt.id)
            {
                result.setBlock(localX, localY - 1, localZ, Block.grass.id);
            }
        }
        if(result.getBlock(localX, localY + 1, localZ)  == Block.fluidWaterStill.id)
        {
            result.setBlock(localX, localY, localZ, Block.fluidWaterFlowing.id);
        }
    }
}
