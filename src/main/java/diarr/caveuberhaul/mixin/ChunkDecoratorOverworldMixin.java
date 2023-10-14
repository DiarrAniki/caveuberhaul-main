package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.CaveUberhaul;
import diarr.caveuberhaul.UberUtil;
import diarr.caveuberhaul.features.*;
import diarr.caveuberhaul.gen.CaveBiomeProvider;
import diarr.caveuberhaul.gen.FastNoiseLite;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockSand;
import net.minecraft.core.block.BlockStone;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.perlin.overworld.ChunkDecoratorOverworld;
import net.minecraft.core.world.generate.feature.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value= ChunkDecoratorOverworld.class,remap = false)
public class ChunkDecoratorOverworldMixin {
    @Unique
    private final CaveBiomeProvider caveBiomeProvider = new CaveBiomeProvider();
    @Unique
    private static final FastNoiseLite caveBiomeDecoratorNoiseMap = new FastNoiseLite();
    @Unique
    private int[] caveBiomeValues;
    @Unique
    float pillarChance = 0.003F;
    @Unique
    int bigPillarChance = 6;
    @Shadow
    private World world;

    @Inject(method = "decorate", at = @At("HEAD"))
    public void decorate(Chunk chunk, CallbackInfo ci)
    {
        int chunkX = chunk.xPosition;
        int chunkZ = chunk.zPosition;

        int minY = world.getWorldType().getMinY();
        int maxY = world.getWorldType().getMaxY();
        int rangeY = (maxY + 1) - minY;

        float oreHeightModifier = rangeY / 128f;

        BlockSand.fallInstantly = true;
        int x = chunkX * 16;
        int z = chunkZ * 16;
        int y = world.getHeightValue(x + 16, z + 16);
        Biome biome = world.getBlockBiome(x + 16, y, z + 16);
        Random rand = new Random(world.getRandomSeed());
        long l1 = (rand.nextLong() / 2L) * 2L + 1L;
        long l2 = (rand.nextLong() / 2L) * 2L + 1L;
        rand.setSeed((long) chunkX * l1 + (long) chunkZ * l2 ^ world.getRandomSeed());
        Random swampRand = new Random((long) chunkX * l1 + (long) chunkZ * l2 ^ world.getRandomSeed());

        caveBiomeValues = caveBiomeProvider.provideCaveBiomeValueChunk(chunk.xPosition,chunk.zPosition,world);
        short[] blocks = chunk.blocks;
        float[][] caveBiomeDecoratorNoise = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(chunk.xPosition,chunk.zPosition,0.08f,world, caveBiomeDecoratorNoiseMap, FastNoiseLite.NoiseType.OpenSimplex2S));
        replaceBlocksForCaveBiome(chunk,blocks,x,z,caveBiomeDecoratorNoise,world,rand);

        for(int lx = 0;lx<16;lx++)
        {
            for(int lz = 0;lz<16;lz++)
            {
                for(int ly = world.getHeightBlocks();ly>0;ly--)
                {
                    if((world.getBlockId(lx+x,ly,lz+z) == CaveUberhaul.flowstone.id||world.getBlockId(lx+x,ly,lz+z) == CaveUberhaul.flowstonePillar.id)&&world.isAirBlock(lx+x,ly-1,lz+z) && caveBiomeDecoratorNoise[lx][lz]>0f && caveBiomeDecoratorNoise[lx][lz]<0.5f)
                    {
                        int length =Math.round(Math.abs(8*caveBiomeDecoratorNoise[lx][lz]))+ rand.nextInt(3);
                        int plength = rand.nextInt(length/2+1);
                        if(rand.nextInt(3)==1&&UberUtil.isSurrounded(lx+x,ly+1,lz+z,world))
                        {
                            world.setBlock(lx+x,ly+1,lz+z,Block.fluidWaterStill.id);
                        }
                        new WorldFeatureFlowstonePillar(length,plength).generate(world,rand,lx+x,ly-1,lz+z);
                    }
                }
            }
        }


        if(rand.nextFloat()>0.92f) {
            int lsx = x + rand.nextInt(16);
            int lsz = z + rand.nextInt(16);
            new WorldFeatureLavaSwamp().generate(world, rand, lsx, 10, lsz);
        }

        // Water pass for swamps
        if (biome == Biomes.OVERWORLD_SWAMPLAND)
        {
            for (int dx = 0; dx < 16; dx++)
            {
                for (int dz = 0; dz < 16; dz++)
                {
                    int topBlock = world.getHeightValue(x + dx, z + dz);
                    int id = world.getBlockId(x + dx, topBlock - 1, z + dz);
                    if (id == Block.grass.id)
                    {
                        boolean shouldPlaceWater = swampRand.nextFloat() < 0.5f;
                        if (shouldPlaceWater)
                        {
                            int posXId = world.getBlockId(x + dx + 1, topBlock - 1, z + dz);
                            int negXId = world.getBlockId(x + dx - 1, topBlock - 1, z + dz);
                            int posZId = world.getBlockId(x + dx, topBlock - 1, z + dz + 1);
                            int negZId = world.getBlockId(x + dx, topBlock - 1, z + dz - 1);
                            int negYId = world.getBlockId(x + dx, topBlock - 2, z + dz);
                            if (
                                    posXId != 0 && (Block.blocksList[posXId].blockMaterial.isSolid() || Block.blocksList[posXId].blockMaterial == Material.water) &&
                                            negXId != 0 && (Block.blocksList[negXId].blockMaterial.isSolid() || Block.blocksList[negXId].blockMaterial == Material.water) &&
                                            posZId != 0 && (Block.blocksList[posZId].blockMaterial.isSolid() || Block.blocksList[posZId].blockMaterial == Material.water) &&
                                            negZId != 0 && (Block.blocksList[negZId].blockMaterial.isSolid() || Block.blocksList[negZId].blockMaterial == Material.water) &&
                                            negYId != 0 && Block.blocksList[negYId].blockMaterial.isSolid()
                            )
                            {
                                world.setBlock(x + dx, topBlock - 1, z + dz, Block.fluidWaterStill.id);
                                world.setBlock(x + dx, topBlock, z + dz, 0);
                            }
                        }
                    }
                }
            }
        }

        int lakeChance = 4;
        if (biome == Biomes.OVERWORLD_SWAMPLAND)
            lakeChance = 2;
        if (biome == Biomes.OVERWORLD_DESERT)
            lakeChance = 0;
        if (lakeChance != 0 && rand.nextInt(lakeChance) == 0) {
            int fluid = Block.fluidWaterStill.id;
            if (biome.hasSurfaceSnow())
            {
                fluid = Block.ice.id;
            }
            int i1 = x + rand.nextInt(16) + 8;
            int l4 = minY + rand.nextInt(rangeY);
            int i8 = z + rand.nextInt(16) + 8;
            (new WorldFeatureLake(fluid)).generate(world, rand, i1, l4, i8);
        }
        if (rand.nextInt(8) == 0) {
            int xf = x + rand.nextInt(16) + 8;
            int yf = minY + rand.nextInt(rand.nextInt(rangeY - (rangeY / 16)) + (rangeY / 16));
            int zf = z + rand.nextInt(16) + 8;
            if (yf < minY + rangeY / 2 || rand.nextInt(10) == 0) {
                (new WorldFeatureLake(Block.fluidLavaStill.id)).generate(world, rand, xf, yf, zf);
            }
        }
        for (int k1 = 0; k1 < (8 * oreHeightModifier); k1++) {
            int j5 = x + rand.nextInt(16) + 8;
            int k8 = minY + rand.nextInt(rangeY);
            int j11 = z + rand.nextInt(16) + 8;
            if(rand.nextInt(100000)==0) {
                (new WorldFeatureDungeoni(Block.cobbleStone.id, Block.cobbleStoneMossy.id, null)).generate(world, rand, j5, k8, j11);
            }
            else
            {
                if (rand.nextInt(2) == 0)
                {
                    (new WorldFeatureDungeon(Block.brickClay.id, Block.brickClay.id, null)).generate(world, rand, j5, k8, j11);
                }
                else
                {
                    (new WorldFeatureDungeon(Block.cobbleStone.id, Block.cobbleStoneMossy.id, null)).generate(world, rand, j5, k8, j11);
                }
            }
        }

        for (int k1 = 0; k1 < 1; k1++) {
            int j5 = x + rand.nextInt(16) + 8;
            int j11 = z + rand.nextInt(16) + 8;
            int k8 = world.getHeightValue(j5, j11) - (rand.nextInt(2) + 2);

            //chance to sink labyrinth
            if (rand.nextInt(5) == 0) {
                k8 -= rand.nextInt(10) + 30;
            }

            if (rand.nextInt(700) == 0) {
                Random lRand = chunk.getChunkRandom(0x4823F58);
                (new WorldFeatureLabyrinth()).generate(world, lRand, j5, k8, j11);
            }
        }
        for (int j2 = 0; j2 < (20 * oreHeightModifier); j2++) {
            int l5 = x + rand.nextInt(16);
            int i9 = 48 + (rand.nextInt(rangeY)-48);
            int l11 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.dirt.id, 32, false)).generate(world, rand, l5, i9, l11);
        }

        for (int k2 = 0; k2 < (10 * oreHeightModifier); k2++) {
            int i6 = x + rand.nextInt(16);
            int j9 = minY + rand.nextInt(rangeY);
            int i12 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.gravel.id, 32, false)).generate(world, rand, i6, j9, i12);
        }

        for (int i3 = 0; i3 < (20 * oreHeightModifier); i3++) {
            int j6 = x + rand.nextInt(16);
            int k9 = minY + rand.nextInt(rangeY-40);
            int j12 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.oreCoalStone.id, 16, true)).generate(world, rand, j6, k9, j12);
        }

        for (int j3 = 0; j3 < (20 * oreHeightModifier); j3++) {
            int k6 = x + rand.nextInt(16);
            int l9 = minY + rand.nextInt(world.getHeightBlocks() / 2-30);
            int k12 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.oreIronStone.id, 8, true)).generate(world, rand, k6, l9, k12);
        }

        for (int j3 = 0; j3 < (5 * oreHeightModifier); j3++) {
            int k6 = x + rand.nextInt(16);
            int l9 = minY + rand.nextInt(30);
            int k12 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.oreIronStone.id, 8, true)).generate(world, rand, k6, l9, k12);
        }

        for (int k3 = 0; k3 < (2 * oreHeightModifier); k3++) {
            int l6 = x + rand.nextInt(16);
            int i10 = minY + rand.nextInt(rangeY / 4);
            int l12 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.oreGoldStone.id, 8, true)).generate(world, rand, l6, i10, l12);
        }

        for (int l3 = 0; l3 < (8 * oreHeightModifier); l3++) {
            int i7 = x + rand.nextInt(16);
            int j10 = minY + rand.nextInt(rangeY / 8);
            int i13 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.oreRedstoneStone.id, 7, true)).generate(world, rand, i7, j10, i13);
        }

        for (int i4 = 0; i4 < oreHeightModifier/2; i4++) {
            int j7 = x + rand.nextInt(16);
            int k10 = minY + rand.nextInt(rangeY / 8);
            int j13 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.oreDiamondStone.id, 7, true)).generate(world, rand, j7, k10, j13);
        }

        for (int i4 = 0; i4 < oreHeightModifier; i4++) {
            int j7 = x + rand.nextInt(16);
            int k10 = 32 + (rand.nextInt(rangeY / 2)-32);
            int j13 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.mossStone.id, 32, true)).generate(world, rand, j7, k10, j13);
        }

        for (int j4 = 0; j4 < oreHeightModifier; j4++) {
            int k7 = x + rand.nextInt(16);
            int l10 = minY + rand.nextInt(rangeY / 8) + rand.nextInt(rangeY / 8);
            int k13 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.oreLapisStone.id, 6, true)).generate(world, rand, k7, l10, k13);
        }
    }

    @Unique
    private void placePillars(int x, int y, int z, int xChunk, int zChunk, World worldObj, Random rand)
    {
        int gx = x+xChunk;
        int gz = z+zChunk;
        if(worldObj.isBlockNormalCube(gx,y,gz)&&worldObj.isAirBlock(gx,y+1,gz)) {
            if (caveBiomeValues[x << worldObj.getHeightBits() + 4 | z << worldObj.getHeightBits() | y] == 1) {

                pillarChance = 0.01F;
                bigPillarChance = 2;
                if(rand.nextFloat()<pillarChance) {
                    if (rand.nextInt(bigPillarChance) == 0) {
                        new WorldFeatureCavePillar(y, UberUtil.getCeiling(gx, y + 1, gz, 40, worldObj), CaveUberhaul.flowstonePillar.id, CaveUberhaul.flowstonePillar.id).generate(worldObj, rand, gx, y + 1, gz);
                        //big pillar
                    } else {
                        new WorldFeatureSmallCavePillar(y, UberUtil.getCeiling(gx, y + 1, gz, 18, worldObj), CaveUberhaul.flowstonePillar.id, CaveUberhaul.flowstonePillar.id).generate(worldObj, rand, gx, y + 1, gz);
                        //small pillar
                    }
                }
            }
            else
            {
                pillarChance = 0.002F;
                bigPillarChance = 4;
                if(rand.nextFloat()<pillarChance) {
                    if (rand.nextInt(bigPillarChance) == 0) {
                        int py = UberUtil.getCeiling(gx, y + 1, gz, 40, worldObj);
                        new WorldFeatureCavePillar(y, py, UberUtil.getPillarBlock(gx, y - 1, gz, worldObj), UberUtil.getPillarBlock(gx, py + 1, gz, worldObj)).generate(worldObj, rand, gx, y + 1, gz);
                        //big pillar
                    } else {
                        int py = UberUtil.getCeiling(gx, y + 1, gz, 18, worldObj);
                        new WorldFeatureSmallCavePillar(y, py, UberUtil.getPillarBlock(gx, y - 1, gz, worldObj), UberUtil.getPillarBlock(gx, py + 1, gz, worldObj)).generate(worldObj, rand, gx, y + 1, gz);
                        //small pillar
                    }
                }
            }
        }
    }

    @Unique
    private void replaceBlocksForCaveBiome(Chunk chunk, short[] data, int x, int z, float[][] biomeDecNoise, World worldObj, Random rand)
    {
        boolean placeFlowstone;
        for(int lx = 0; lx<16; lx++)
        {
            int gx = lx+x;
            for(int lz = 0; lz<16; lz++)
            {
                int gz = lz+z;
                placeFlowstone = biomeDecNoise[lx][lz]>-0.2f;
                for(int ly = world.getHeightBlocks()-1; ly > 0; ly--)
                {

                    switch(caveBiomeValues[lx << world.getHeightBits() + 4 | lz << world.getHeightBits() | ly]) {
                        case 1:
                        {
                            if (data[lx << world.getHeightBits() + 4 | lz << world.getHeightBits() | ly] !=0 && data[lx << world.getHeightBits() + 4 | lz << world.getHeightBits() | ly] != Block.bedrock.id && Block.getBlock(data[lx << world.getHeightBits() + 4 | lz << world.getHeightBits() | ly]) instanceof BlockStone && placeFlowstone)
                            {
                                data[lx << world.getHeightBits() + 4 | lz << world.getHeightBits() | ly] = (short) CaveUberhaul.flowstone.id;
                            }

                            if(data[lx << world.getHeightBits() + 4 | lz << world.getHeightBits() | ly] !=0&&Block.getBlock(data[lx << world.getHeightBits() + 4 | lz << world.getHeightBits() | ly]).blockMaterial==Material.stone&&rand.nextFloat()>=0.4f&&UberUtil.isSurroundedFreeAboveNoLava(x+lx,ly,z+lz,worldObj))
                            {
                                //worldObj.setBlock(x + lx, ly, z + lz, Block.fluidWaterStill.blockID);
                                if(worldObj.isAirBlock(gx,ly-1,gz))
                                {
                                    data[lx << world.getHeightBits() + 4 | lz << world.getHeightBits() | ly]=(short) Block.fluidWaterFlowing.id;
                                }
                                else {
                                    data[lx << world.getHeightBits() + 4 | lz << world.getHeightBits() | ly] = (short) Block.fluidWaterStill.id;
                                }
                            }
                            break;
                        }
                        default:
                        {
                            break;
                        }
                    }
                    placePillars(lx,ly,lz,x,z,worldObj,rand);
                }
            }
        }
        chunk.blocks = data;
    }
}
