package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.CaveUberhaul;
import diarr.caveuberhaul.UberUtil;
import diarr.caveuberhaul.blocks.EntityFallingStalagtite;
import diarr.caveuberhaul.features.*;
import diarr.caveuberhaul.gen.CaveBiomeProvider;
import diarr.caveuberhaul.gen.FastNoiseLite;
import diarr.caveuberhaul.gen.MapGenNoiseCaves;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockSand;
import net.minecraft.core.block.BlockStone;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.animal.EntityWolf;
import net.minecraft.core.net.command.CommandError;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.BiomeOutback;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.MapGenBase;
import net.minecraft.core.world.generate.chunk.perlin.overworld.ChunkDecoratorOverworld;
import net.minecraft.core.world.generate.feature.*;
import net.minecraft.core.world.noise.PerlinNoise;
import net.minecraft.core.world.type.WorldTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value= ChunkDecoratorOverworld.class,remap = false)
public class ChunkDecoratorOverworldMixin {
    private CaveBiomeProvider caveBiomeProvider = new CaveBiomeProvider();
    private static FastNoiseLite caveBiomeDecoratorNoiseMap = new FastNoiseLite();
    private int[] caveBiomeValues;
    float pillarChance = 0.003F;
    int bigPillarChance = 6;
    @Shadow
    private World world;
    @Shadow
    private PerlinNoise treeDensityNoise;
    @Shadow
    private int treeDensityOverride;


    @Inject(method = "decorate", at = @At("HEAD"),cancellable = true)
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
        double d = 0.25D;

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

        for (int i2 = 0; i2 < (20 * oreHeightModifier); i2++) {
            int k5 = x + rand.nextInt(16);
            int l8 = minY + rand.nextInt(rangeY);
            int k11 = z + rand.nextInt(16);
            (new WorldFeatureClay(32)).generate(world, rand, k5, l8, k11);
        }
        if (biome instanceof BiomeOutback) {
            int l5 = x + rand.nextInt(16);
            int l11 = z + rand.nextInt(16);
            int i9 = world.getHeightValue(l5, l11);
            (new WorldFeatureRichScorchedDirt(20)).generate(world, rand, l5, i9, l11);
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

        if(rand.nextFloat()<0.0001f)
        {
            int px = x + rand.nextInt(16);
            int pz = z + rand.nextInt(16);
            int py = rand.nextInt(rangeY/2);
            if(world.isAirBlock(px,py,pz)&&world.isBlockNormalCube(px, py, pz + 1)) {
               world.setBlock(px, py, pz, Block.torchCoal.id);
               world.setBlockMetadataWithNotify(px, py, pz, 4);
            }
        }

        d = 0.5D;
        int k4 = (int) ((treeDensityNoise.get((double) x * d, (double) z * d) / 8D + rand.nextDouble() * 4D + 4D) / 3D);
        int treeDensity = 0;
        if (rand.nextInt(10) == 0) {
            treeDensity++;
        }
        if (biome == Biomes.OVERWORLD_FOREST) {
            treeDensity += k4 + 5;
        }
        if (biome == Biomes.OVERWORLD_BIRCH_FOREST) {
            treeDensity += k4 + 4;
        }
        if (biome == Biomes.OVERWORLD_RAINFOREST) {
            treeDensity += k4 + 10;
        }
        if (biome == Biomes.OVERWORLD_SEASONAL_FOREST) {
            treeDensity += k4 + 2;
        }
        if (biome == Biomes.OVERWORLD_TAIGA) {
            treeDensity += k4 + 5;
        }
        if (biome == Biomes.OVERWORLD_BOREAL_FOREST) {
            treeDensity += k4 + 3;
        }
        if (biome == Biomes.OVERWORLD_DESERT) {
            treeDensity = 0;
        }
        if (biome == Biomes.OVERWORLD_TUNDRA) {
            treeDensity -= 20;
        }
        if (biome == Biomes.OVERWORLD_PLAINS) {
            treeDensity -= 20;
        }
        if (biome == Biomes.OVERWORLD_SWAMPLAND) {
            treeDensity += k4 + 4;
        }
        if (biome == Biomes.OVERWORLD_OUTBACK_GRASSY) {
            treeDensity += k4;
        }
        if (treeDensityOverride != -1) treeDensity = treeDensityOverride;
        for (int i11 = 0; i11 < treeDensity; i11++) {
            int l13 = x + rand.nextInt(16) + 8;
            int j14 = z + rand.nextInt(16) + 8;
            WorldFeature feature = biome.getRandomWorldGenForTrees(rand);
            feature.func_517_a(1.0D, 1.0D, 1.0D);
            feature.generate(world, rand, l13, world.getHeightValue(l13, j14), j14);
        }
        byte byteReeds = 0;
        if (biome == Biomes.OVERWORLD_RAINFOREST) {
            byteReeds = 1;
        }
        for (int i11 = 0; i11 < byteReeds; i11++) {
            int i18 = x + rand.nextInt(16) + 8;
            int i23 = z + rand.nextInt(16) + 8;
            int i21 = world.getHeightValue(i18, i23);
            (new WorldFeatureSugarCaneTall()).generate(world, rand, i18, i21, i23);
        }

        if(rand.nextFloat()<0.01f) {
            int rx = x + rand.nextInt(16);
            int ry = rand.nextInt(rangeY );
            int rz = z + rand.nextInt(16);
            if(world.isAirBlock(rx,ry,rz)&&!world.isAirBlock(rx,ry-1,rz)&&Block.getBlock(world.getBlockId(rx,ry-1,rz)).blockMaterial.isSolid()) {
                EntityWolf entityWolf = new EntityWolf(world);
                world.entityJoinedWorld(entityWolf);
                entityWolf.setPos(rx,ry,rz);
                entityWolf.setRot(this.world.rand.nextFloat() * 360.0F, 0.0F);
                entityWolf.setWolfTamed(true);
                entityWolf.setWolfOwner("");
                entityWolf.setWolfAngry(true);
            }
        }

        byte byteMeadow = 0;
        if (biome == Biomes.OVERWORLD_SEASONAL_FOREST) {
            byteMeadow = 1;
        }
        if (biome == Biomes.OVERWORLD_MEADOW) {
            byteMeadow = 2;
        }
        if (biome == Biomes.OVERWORLD_BOREAL_FOREST) {
            byteMeadow = 2;
        }
        if (biome == Biomes.OVERWORLD_SHRUBLAND) {
            byteMeadow = 1;
        }
        for (int l14 = 0; l14 < byteMeadow; l14++) {
            int blockId = Block.flowerYellow.id;
            if (rand.nextInt(3) != 0) {
                blockId = Block.flowerRed.id;
            }
            int l19 = x + rand.nextInt(16) + 8;
            int k22 = rand.nextInt(world.getHeightBlocks());
            int j24 = z + rand.nextInt(16) + 8;
            (new WorldFeatureTallGrass(blockId)).generate(world, rand, l19, k22, j24);
        }

        byte byte0 = 0;
        if (biome == Biomes.OVERWORLD_FOREST) {
            byte0 = 2;
        }
        if (biome == Biomes.OVERWORLD_SWAMPLAND) {
            byte0 = 2;
        }
        if (biome == Biomes.OVERWORLD_TAIGA) {
            byte0 = 2;
        }
        if (biome == Biomes.OVERWORLD_PLAINS) {
            byte0 = 3;
        }
        if (biome == Biomes.OVERWORLD_OUTBACK_GRASSY || biome == Biomes.OVERWORLD_OUTBACK) {
            byte0 = 2;
        }
        for (int i14 = 0; i14 < byte0; i14++) {
            int k14 = x + rand.nextInt(16) + 8;
            int l16 = minY + rand.nextInt(rangeY);
            int k19 = z + rand.nextInt(16) + 8;
            (new WorldFeatureFlowers(Block.flowerYellow.id)).generate(world, rand, k14, l16, k19);
        }

        if(rand.nextFloat()<0.00001f)
        {
            int px = x + rand.nextInt(16);
            int pz = z + rand.nextInt(16);
            int py = rand.nextInt(rangeY/3);
            for(int i=0;i<=rand.nextInt(39)+11;i++) {
                world.setBlock(px+i, py, pz, 0);
                world.setBlock(px+i, py+1, pz, 0);
                if(i%6==0&& Block.torchRedstoneActive.canPlaceBlockAt(world,px+i,py+1,pz))
                {
                    world.setBlock(px+i, py+1, pz, Block.torchRedstoneActive.id);
                    world.setBlockMetadataWithNotify(px+i,py+1,pz,3);
                }
            }
        }

        byte byte1 = 0;
        if (biome == Biomes.OVERWORLD_FOREST) {
            byte1 = 2;
        }
        if (biome == Biomes.OVERWORLD_MEADOW) {
            byte1 = 2;
        }
        if (biome == Biomes.OVERWORLD_RAINFOREST) {
            byte1 = 10;
        }
        if (biome == Biomes.OVERWORLD_DESERT) {
            byte1 = 5;
        }
        if (biome == Biomes.OVERWORLD_SEASONAL_FOREST) {
            byte1 = 2;
        }
        if (biome == Biomes.OVERWORLD_TAIGA) {
            byte1 = 1;
        }
        if (biome == Biomes.OVERWORLD_BOREAL_FOREST) {
            byte1 = 5;
        }
        if (biome == Biomes.OVERWORLD_PLAINS) {
            byte1 = 10;
        }
        if (biome == Biomes.OVERWORLD_SWAMPLAND) {
            byte1 = 4;
        }
        if (biome == Biomes.OVERWORLD_SHRUBLAND) {
            byte1 = 2;
        }
        if (biome == Biomes.OVERWORLD_OUTBACK_GRASSY) {
            byte1 = 25;
        }
        if (biome == Biomes.OVERWORLD_BIRCH_FOREST) {
            byte1 = 10;
        }
        for (int l14 = 0; l14 < byte1; l14++) {
            int type = Block.tallgrass.id;
            if ((biome == Biomes.OVERWORLD_RAINFOREST || biome == Biomes.OVERWORLD_SWAMPLAND || biome == Biomes.OVERWORLD_BOREAL_FOREST || biome == Biomes.OVERWORLD_TAIGA) && rand.nextInt(3) != 0) {
                type = Block.tallgrassFern.id;
            }
            int l19 = x + rand.nextInt(16) + 8;
            int k22 = minY + rand.nextInt(rangeY);
            int j24 = z + rand.nextInt(16) + 8;
            (new WorldFeatureTallGrass(type)).generate(world, rand, l19, k22, j24);
        }


        byte1 = 0;
        if (biome == Biomes.OVERWORLD_OUTBACK) {
            byte1 = 4;
        }
        for (int i15 = 0; i15 < byte1; i15++) {
            int i17 = x + rand.nextInt(16) + 8;
            int i20 = minY + rand.nextInt(rangeY);
            int l22 = z + rand.nextInt(16) + 8;
            (new WorldFeatureSpinifexPatch()).generate(world, rand, i17, i20, l22);
        }

        byte1 = 0;
        if (biome == Biomes.OVERWORLD_DESERT) {
            byte1 = 2;
        }
        for (int i15 = 0; i15 < byte1; i15++) {
            int i17 = x + rand.nextInt(16) + 8;
            int i20 = minY + rand.nextInt(rangeY);
            int l22 = z + rand.nextInt(16) + 8;
            (new WorldFeatureDeadBush(Block.deadbush.id)).generate(world, rand, i17, i20, l22);
        }

        if (rand.nextInt(2) == 0) {
            int j15 = x + rand.nextInt(16) + 8;
            int j17 = minY + rand.nextInt(rangeY);
            int j20 = z + rand.nextInt(16) + 8;
            (new WorldFeatureFlowers(Block.flowerRed.id)).generate(world, rand, j15, j17, j20);
        }
        if (rand.nextInt(4) == 0) {
            int k15 = x + rand.nextInt(16) + 8;
            int k17 = minY + rand.nextInt(rangeY);
            int k20 = z + rand.nextInt(16) + 8;
            (new WorldFeatureFlowers(Block.mushroomBrown.id)).generate(world, rand, k15, k17, k20);
        }
        if (rand.nextInt(8) == 0) {
            int l15 = x + rand.nextInt(16) + 8;
            int l17 = minY + rand.nextInt(rangeY);
            int l20 = z + rand.nextInt(16) + 8;
            (new WorldFeatureFlowers(Block.mushroomRed.id)).generate(world, rand, l15, l17, l20);
        }
        if(rand.nextInt(5) == 0) {
            int i18 = x + rand.nextInt(16) + 8;
            int i23 = z + rand.nextInt(16) + 8;
            int i21 = world.getHeightValue(i18, i23);
            (new WorldFeatureSugarCane()).generate(world, rand, i18, i21, i23);
        }

        if(rand.nextInt(128) == 0)
        {
            int j16 = x + rand.nextInt(16) + 8;
            int j21 = z + rand.nextInt(16) + 8;
            int i22 = world.getHeightValue(j16, j21);
            (new WorldFeaturePumpkin()).generate(world, rand, j16, i22, j21);
        }
        if(rand.nextInt(64) == 0)
        {
            int j16 = x + rand.nextInt(16) + 8;
            int j21 = z + rand.nextInt(16) + 8;
            int i22 = world.getHeightValue(j16, j21);
            (new WorldFeatureSponge()).generate(world, rand, j16, i22, j21);
        }

        int k16 = 0;
        if(biome == Biomes.OVERWORLD_DESERT)
        {
            k16 += 10;
        }
        for(int k18 = 0; k18 < k16; k18++)
        {
            int k21 = x + rand.nextInt(16) + 8;
            int j23 = minY + rand.nextInt(rangeY);
            int k24 = z + rand.nextInt(16) + 8;
            (new WorldFeatureCactus()).generate(world, rand, k21, j23, k24);
        }

        for(int l18 = 0; l18 < 50; l18++)
        {
            int l21 = x + rand.nextInt(16) + 8;
            int k23 = minY + rand.nextInt(rand.nextInt(rangeY - (rangeY / 16)) + (rangeY / 16));
            int l24 = z + rand.nextInt(16) + 8;
            (new WorldFeatureLiquid(Block.fluidWaterFlowing.id)).generate(world, rand, l21, k23, l24);
        }

        for(int i19 = 0; i19 < 20; i19++)
        {
            int i22 = x + rand.nextInt(16) + 8;
            int l23 = minY + rand.nextInt(rand.nextInt(rand.nextInt(rangeY - (rangeY / 8)) + (rangeY / 16)) + (rangeY / 16));
            int i25 = z + rand.nextInt(16) + 8;
            (new WorldFeatureLiquid(Block.fluidLavaFlowing.id)).generate(world, rand, i22, l23, i25);
        }

        int oceanY = world.getWorldType().getOceanY();
        for(int dx = x + 8; dx < x + 8 + 16; dx++)
        {
            for(int dz = z + 8; dz < z + 8 + 16; dz++)
            {
                int dy = world.getHeightValue(dx, dz);
                Biome localBiome = world.getBlockBiome(dx, dy, dz);

                if ((localBiome.hasSurfaceSnow() || world.worldType == WorldTypes.OVERWORLD_WINTER) && dy > 0 && dy < world.getHeightBlocks())
                {
                    if (world.isAirBlock(dx, dy, dz) && world.getBlockMaterial(dx, dy - 1, dz).blocksMotion())
                    {
                        world.setBlockWithNotify(dx, dy, dz, Block.layerSnow.id);
                    }
                }
                if ((localBiome.hasSurfaceSnow() || world.worldType == WorldTypes.OVERWORLD_WINTER) && (world.getBlockId(dx, oceanY - 1, dz) == Block.fluidWaterStill.id || world.getBlockId(dx, oceanY - 1, dz) == Block.fluidWaterFlowing.id))
                {
                    world.setBlockWithNotify(dx, oceanY - 1, dz, Block.ice.id);
                }

            }

        }

        BlockSand.fallInstantly = false;
        ci.cancel();
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
