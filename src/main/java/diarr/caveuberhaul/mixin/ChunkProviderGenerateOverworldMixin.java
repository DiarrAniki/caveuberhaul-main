package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.UberUtil;
import diarr.caveuberhaul.WorldFeatureLavaSwamp;
import diarr.caveuberhaul.WorldFeatureThermalVent;
import net.minecraft.core.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.block.BlockSand;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldType;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.perlin.overworld.ChunkDecoratorOverworld;
import net.minecraft.core.world.generate.feature.*;
import net.minecraft.core.world.noise.NoiseGeneratorOctaves;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

//Huge thanks to Worley and the Worley Caves mod https://www.curseforge.com/minecraft/mc-mods/worleys-caves for explaining how alot of this works.
@Mixin(value= ChunkDecoratorOverworld.class,remap = false)
public class ChunkProviderGenerateOverworldMixin {

    private UberUtil uberUtil = new UberUtil();

    @Shadow
    private World world;
    @Shadow
    private NoiseGeneratorOctaves treeDensityNoise;
    @Shadow
    private int treeDensityOverride;

    @Inject(method = "decorate", at = @At("HEAD"),cancellable = true)
    public void populate(Chunk chunk, CallbackInfo ci)
    {
        int chunkX = chunk.xPosition;
        int chunkZ = chunk.zPosition;

        int minY = world.getWorldType().minY;
        int maxY = world.getWorldType().maxY;
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

        if(rand.nextFloat()>0.92f) {
            int lsx = x + rand.nextInt(16);
            int lsz = z + rand.nextInt(16);
            new WorldFeatureLavaSwamp().generate(world, rand, lsx, 10, lsz);
        }

        /*if(rand.nextFloat()>0.6f)
        {
            int tvcx = x + rand.nextInt(16);
            int tvcz = z + rand.nextInt(16);
            int tvcy = 10;
            for(int k1 = 0;k1<=3+rand.nextInt(3);k1++)
            {
                double r = rand.nextInt(12)+6 * Math.sqrt(rand.nextFloat());
                double theta = rand.nextFloat() * 2 * Math.PI;
                int tvx = (int) (tvcx + r * Math.cos((theta)));
                int tvy = uberUtil.GetFirstAirBlock(tvcx,10,tvcz,world);
                int tvz = (int) (tvcz + r * Math.sin(theta));
                new WorldFeatureThermalVent().generate(world, rand, tvx, tvy, tvz);
            }
        }*/

        // Water pass for swamps
        if (biome == Biome.SWAMPLAND)
        {
            (new WorldFeatureMudPatch()).generate(world, rand, x, 0, z);
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

        if (biome == Biome.OUTBACK) {

            (new WorldFeatureScorchedGrass()).generate(world, rand, x, 0, z);
        }

        int lakeChance = 4;
        if (biome == Biome.SWAMPLAND)
            lakeChance = 2;
        if (biome == Biome.DESERT)
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
            if (rand.nextInt(2) == 0) {
                (new WorldFeatureDungeon(Block.brickClay.id, Block.brickClay.id, null)).generate(world, rand, j5, k8, j11);
            } else {
                (new WorldFeatureDungeon(Block.cobbleStone.id, Block.cobbleStoneMossy.id, null)).generate(world, rand, j5, k8, j11);
            }
        }

        for (int k1 = 0; k1 < 1; k1++) {
            int j5 = x + rand.nextInt(16) + 8;
            int j11 = z + rand.nextInt(16) + 8;
            int k8 = world.getHeightValue(j5, j11) - (rand.nextInt(2) + 2);

            //chance to sink labyrinth
            if (rand.nextInt(5) == 0)
                k8 -= rand.nextInt(10) + 30;

            if (rand.nextInt(700) == 0) {
                (new WorldFeatureLabyrinth()).generate(world, rand, j5, k8, j11);
            }
        }

        for (int i2 = 0; i2 < (20 * oreHeightModifier); i2++) {
            int k5 = x + rand.nextInt(16);
            int l8 = minY + rand.nextInt(rangeY);
            int k11 = z + rand.nextInt(16);
            (new WorldFeatureClay(32)).generate(world, rand, k5, l8, k11);
        }
        if (biome == Biome.OUTBACK) {
            int l5 = x + rand.nextInt(16);
            int l11 = z + rand.nextInt(16);
            int i9 = world.getHeightValue(l5, l11);
            (new WorldFeatureRichScorchedDirt(20)).generate(world, rand, l5, i9, l11);
        }


        for (int j2 = 0; j2 < (20 * oreHeightModifier); j2++) {
            int l5 = x + rand.nextInt(16);
            int i9 = minY + rand.nextInt(rangeY);
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
            int k9 = minY + 40+rand.nextInt(rangeY-40);
            int j12 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.oreCoalStone.id, 16, true)).generate(world, rand, j6, k9, j12);
        }

        for (int j3 = 0; j3 < (20 * oreHeightModifier); j3++) {
            int k6 = x + rand.nextInt(16);
            int l9 = minY + 30 + rand.nextInt(rangeY / 2-30);
            int k12 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.oreIronStone.id, 8, true)).generate(world, rand, k6, l9, k12);
        }
        for (int j3 = 0; j3 < (5 * oreHeightModifier); j3++) {
            int k6 = x + rand.nextInt(16);
            int l9 = minY + (rand.nextInt(30));
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
            int k10 = minY + 40 + rand.nextInt(rangeY / 2-40);
            int j13 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.mossStone.id, 32, true)).generate(world, rand, j7, k10, j13);
        }

        for (int j4 = 0; j4 < oreHeightModifier; j4++) {
            int k7 = x + rand.nextInt(16);
            int l10 = minY + rand.nextInt(rangeY / 8) + rand.nextInt(rangeY / 8);
            int k13 = z + rand.nextInt(16);
            (new WorldFeatureOre(Block.oreLapisStone.id, 6, true)).generate(world, rand, k7, l10, k13);
        }


        d = 0.5D;
        int k4 = (int) ((treeDensityNoise.generateNoise((double) x * d, (double) z * d) / 8D + rand.nextDouble() * 4D + 4D) / 3D);
        int treeDensity = 0;
        if (rand.nextInt(10) == 0) {
            treeDensity++;
        }
        if (biome == Biome.FOREST) {
            treeDensity += k4 + 5;
        }
        if (biome == Biome.BIRCH_FOREST) {
            treeDensity += k4 + 4;
        }
        if (biome == Biome.RAINFOREST) {
            treeDensity += k4 + 10;
        }
        if (biome == Biome.SEASONAL_FOREST) {
            treeDensity += k4 + 2;
        }
        if (biome == Biome.TAIGA) {
            treeDensity += k4 + 5;
        }
        if (biome == Biome.BOREAL_FOREST) {
            treeDensity += k4 + 3;
        }
        if (biome == Biome.DESERT) {
            treeDensity += k4 + 1;
        }
        if (biome == Biome.TUNDRA) {
            treeDensity -= 20;
        }
        if (biome == Biome.PLAINS) {
            treeDensity -= 20;
        }
        if (biome == Biome.SWAMPLAND) {
            treeDensity += k4 + 4;
        }
        if (treeDensityOverride != -1) treeDensity = treeDensityOverride;
        for (int i11 = 0; i11 < treeDensity; i11++) {
            int l13 = x + rand.nextInt(16) + 8;
            int j14 = z + rand.nextInt(16) + 8;
            WorldFeature worldgenerator = biome.getRandomWorldGenForTrees(rand);
            worldgenerator.func_517_a(1.0D, 1.0D, 1.0D);
            worldgenerator.generate(world, rand, l13, world.getHeightValue(l13, j14), j14);
        }
        byte byteReeds = 0;
        if (biome == Biome.RAINFOREST) {
            byteReeds = 1;
        }
        for (int i11 = 0; i11 < byteReeds; i11++) {
            int i18 = x + rand.nextInt(16) + 8;
            int i23 = z + rand.nextInt(16) + 8;
            int i21 = world.getHeightValue(i18, i23);
            (new WorldFeatureSugarCaneTall()).generate(world, rand, i18, i21, i23);
        }

        byte byteMeadow = 0;
        if (biome == Biome.SEASONAL_FOREST) {
            byteMeadow = 1;
        }
        if (biome == Biome.MEADOW) {
            byteMeadow = 2;
        }
        if (biome == Biome.BOREAL_FOREST) {
            byteMeadow = 2;
        }
        if (biome == Biome.SHRUBLAND) {
            byteMeadow = 1;
        }
        for (int l14 = 0; l14 < byteMeadow; l14++) {
            int blockId = Block.flowerYellow.id;
            if (rand.nextInt(3) != 0) {
                blockId = Block.flowerRed.id;
            }
            int l19 = x + rand.nextInt(16) + 8;
            int k22 = rand.nextInt(Minecraft.WORLD_HEIGHT_BLOCKS);
            int j24 = z + rand.nextInt(16) + 8;
            (new WorldFeatureTallGrass(blockId)).generate(world, rand, l19, k22, j24);
        }

        byte byte0 = 0;
        if (biome == Biome.FOREST) {
            byte0 = 2;
        }
        if (biome == Biome.SWAMPLAND) {
            byte0 = 2;
        }
        if (biome == Biome.TAIGA) {
            byte0 = 2;
        }
        if (biome == Biome.PLAINS) {
            byte0 = 3;
        }
        for (int i14 = 0; i14 < byte0; i14++) {
            int k14 = x + rand.nextInt(16) + 8;
            int l16 = minY + rand.nextInt(rangeY);
            int k19 = z + rand.nextInt(16) + 8;
            (new WorldFeatureFlowers(Block.flowerYellow.id)).generate(world, rand, k14, l16, k19);
        }

        byte byte1 = 0;
        if (biome == Biome.FOREST) {
            byte1 = 2;
        }
        if (biome == Biome.MEADOW) {
            byte1 = 2;
        }
        if (biome == Biome.RAINFOREST) {
            byte1 = 10;
        }
        if (biome == Biome.DESERT) {
            byte1 = 5;
        }
        if (biome == Biome.SEASONAL_FOREST) {
            byte1 = 2;
        }
        if (biome == Biome.TAIGA) {
            byte1 = 1;
        }
        if (biome == Biome.BOREAL_FOREST) {
            byte1 = 5;
        }
        if (biome == Biome.PLAINS) {
            byte1 = 10;
        }
        if (biome == Biome.SWAMPLAND) {
            byte1 = 4;
        }
        if (biome == Biome.SHRUBLAND) {
            byte1 = 2;
        }
        if (biome == Biome.BIRCH_FOREST) {
            byte1 = 10;
        }
        for (int l14 = 0; l14 < byte1; l14++) {
            int type = Block.tallgrass.id;
            if ((biome == Biome.RAINFOREST || biome == Biome.SWAMPLAND || biome == Biome.BOREAL_FOREST || biome == Biome.TAIGA) && rand.nextInt(3) != 0) {
                type = Block.tallgrassFern.id;
            }
            int l19 = x + rand.nextInt(16) + 8;
            int k22 = minY + rand.nextInt(rangeY);
            int j24 = z + rand.nextInt(16) + 8;
            (new WorldFeatureTallGrass(type)).generate(world, rand, l19, k22, j24);
        }


        byte1 = 0;
        if (biome == Biome.OUTBACK) {
            byte1 = 2;
        }
        for (int i15 = 0; i15 < byte1; i15++) {
            int i17 = x + rand.nextInt(16) + 8;
            int i20 = minY + rand.nextInt(rangeY);
            int l22 = z + rand.nextInt(16) + 8;
            (new WorldFeatureTallGrass(Block.spinifex.id)).generate(world, rand, i17, i20, l22);
        }

        byte1 = 0;
        if (biome == Biome.DESERT) {
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
        if(biome == Biome.DESERT)
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

        int oceanY = world.getWorldType().oceanY;
        for(int dx = x + 8; dx < x + 8 + 16; dx++)
        {
            for(int dz = z + 8; dz < z + 8 + 16; dz++)
            {
                int dy = world.getHeightValue(dx, dz);
                Biome localBiome = world.getBlockBiome(dx, dy, dz);

                if ((localBiome.hasSurfaceSnow() || world.worldType == WorldType.overworldWinter) && dy > 0 && dy < Minecraft.WORLD_HEIGHT_BLOCKS)
                {
                    if (world.isAirBlock(dx, dy, dz) && world.getBlockMaterial(dx, dy - 1, dz).getIsSolid())
                    {
                        world.setBlockWithNotify(dx, dy, dz, Block.layerSnow.id);
                    }
                }
                if ((localBiome.hasSurfaceSnow() || world.worldType == WorldType.overworldWinter) && (world.getBlockId(dx, oceanY - 1, dz) == Block.fluidWaterStill.id || world.getBlockId(dx, oceanY - 1, dz) == Block.fluidWaterFlowing.id))
                {
                    world.setBlockWithNotify(dx, oceanY - 1, dz, Block.ice.id);
                }

            }

        }

        BlockSand.fallInstantly = false;
        ci.cancel();
    }

    /*private void GeneratePillar(int gx, int gz,Random rand,int heightModifier)
    {
        int upperY;
        int lowerY;
        for(int chance = 0; chance <  2*heightModifier; ++chance) {
            int lx = gx+rand.nextInt(16);
            int ly = rand.nextInt(Minecraft.WORLD_HEIGHT_BLOCKS/2)+10;
            int lz = gz+rand.nextInt(16) ;
            if(!uberUtil.isRockBlockAndNotLava(Block.getBlock(world.getBlockId(lx,ly,lz))))
            {
                lowerY = GetBlocksDown(lx, ly, lz,0,15);
                upperY = GetBlocksUp(lx, lowerY, lz,0,30);
            }
            else
            {
                lowerY = GetFirstNonSolidUp(lx,ly,lz);
                upperY = GetBlocksUp(lx, lowerY, lz,0,15);
            }
            int heightdiff = (upperY+3)-(lowerY-3);
            double radius = uberUtil.clamp(heightdiff*0.3f,0,6);
            if(ly > 14)
            {
                if(canPillarBePlaced(lx,upperY,lowerY,lz,radius,2,0)) {
                    //BuildPillar(lx, lz, lowerY-3, upperY+3,heightdiff,radius);
                    BuildPillar2(lx, lz, lowerY-3, upperY+3,heightdiff,radius);
                }
            }
            else
            {
                if(canPillarBePlaced(lx,upperY,lowerY,lz,radius,2,2)) {
                    //BuildPillar(lx, lz, lowerY-3, upperY+3,heightdiff,radius);
                    BuildPillar2(lx, lz, lowerY-3, upperY+3,heightdiff,radius);
                }
            }

        }
    }

    private void BuildPillar(int x,int z, int lower ,int upper, int height, double radius,Random rand)
    {
        //worldObj.setBlock(x, upper, z, Block.blockDiamond.blockID);
        //worldObj.setBlock(x, lower, z, Block.blockGold.blockID);

       // int pillarBlockId = GetPillarBlock(x,lower-1,z);

        //int heightdif = upper-lower;
        int half = height/2+rand.nextInt(2)-1;

        //float radius = uberUtil.clamp(heightdif*0.3f,0,6);
        int radiusInt = (int) Math.round(Math.floor(radius));

            for (int xPos = x - radiusInt; xPos <= x + radiusInt; xPos++) {
                for (int zPos = z - radiusInt; zPos <= z + radiusInt; zPos++) {

                    float dist = Math.round(uberUtil.distanceAB(xPos, lower, zPos, x, lower, z));

                    //TODO random block addition depending on distance from center. If xPos OR zPos on max/min, heighest random addition, if xPos AND zPos on max/min, either 0 or random reduction. (Hopefully round shape?)
                    int inWorldHeight = uberUtil.clamp(lower+(half-(int)Math.rint(Math.pow(2,dist))),lower-3,lower+half)+rand.nextInt(3);
                    int heightDifFromMid = (lower+half)-inWorldHeight;//+ rand.nextInt(3)-1;

                    if(dist ==0)
                    {
                        inWorldHeight += 2+rand.nextInt(3);
                    }
                    for (int yPos = lower; yPos <= upper; yPos++) {

                        //boolean needTrim = !uberUtil.solidBlockExists(xPos,yPos,zPos,worldObj)&&((yPos<lower+3 && uberUtil.solidBlockExists(xPos,yPos+1,zPos,worldObj)) || (yPos>upper-3 && uberUtil.solidBlockExists(xPos,yPos-1,zPos,worldObj)));
                        boolean blockOmit = dist < radius && (yPos<inWorldHeight||yPos>=lower+half+heightDifFromMid);
                        boolean blockRules = world.getBlockId(xPos, yPos, zPos) != Block.bedrock.id&& !uberUtil.solidBlockExists(xPos,yPos,zPos,world);// && worldObj.getBlockId(xPos, yPos, zPos) != Block.blockDiamond.blockID && worldObj.getBlockId(xPos, yPos, zPos) != Block.blockGold.blockID );
                        if (blockOmit && blockRules) {
                           // world.setBlock(xPos, yPos, zPos, pillarBlockId);
                        }
                    }
                }
        }
    }

    private void BuildPillar2(int x,int z, int lower ,int upper, int height, double radius)
    {
        //worldObj.setBlock(x, upper, z, Block.blockDiamond.blockID);
        //worldObj.setBlock(x, lower, z, Block.blockGold.blockID);

        //int pillarBlockId = GetPillarBlock(x,lower-1,z);

        //int heightdif = upper-lower;
        int maxHeight = 2*height /5;

        //float radius = uberUtil.clamp(heightdif*0.3f,0,6);
        int radiusInt = (int) radius;

        for (int xPos = x - radiusInt; xPos <= x + radiusInt; xPos++) {
            for (int zPos = z - radiusInt; zPos <= z + radiusInt; zPos++) {

                float dist = Math.round(uberUtil.distanceAB(xPos, lower, zPos, x, lower, z));

                int actualHeight = (int) uberUtil.clampedLerp(lower,lower+maxHeight,1/(dist/radius)/3-0.2f,lower,lower+maxHeight);

                for(int yPos = lower;yPos<=actualHeight;yPos++) {
                    boolean blockRules = world.getBlockId(xPos, yPos, zPos) != Block.bedrock.id && !uberUtil.solidBlockExists(xPos, yPos, zPos, world);// && worldObj.getBlockId(xPos, yPos, zPos) != Block.blockDiamond.blockID && worldObj.getBlockId(xPos, yPos, zPos) != Block.blockGold.blockID );
                    if (blockRules) {
                        //world.setBlock(xPos, yPos, zPos, pillarBlockId);
                    }
                }
            }
        }
    }

    private boolean canPillarBePlaced(int x, int upper,int lower ,int z,double radius,int upperRange,int lowerRange)
    {
        //int halfRad = Math.round(radius/2);
        boolean test = true;
        if(lower == 0 || upper ==0 || upper-lower<=3)
        {
            test = false;
        }
        else {
            for (int xp = x - 1; xp <= x +1; xp++) {
                for (int zp = z - 1; zp <= z + 1; zp++) {
                    for (int yup = upper; yup <= upper + upperRange; yup++) {
                        if (!uberUtil.solidBlockExists(xp, yup + 1, zp, world)) {
                            test = false;
                        }
                    }
                    for (int ydn = lower; ydn >= lower - lowerRange; ydn--) {
                        if (!uberUtil.solidBlockExists(xp, ydn - 1, zp, world)) {
                            test = false;
                        }
                    }
                }
            }
        }
        //System.out.println(test);
        return test;
    }*/

    private int GetFirstNonSolidUp(int x,int y,int z)
    {
        if(y<=Minecraft.WORLD_HEIGHT_BLOCKS/2&&y>8)
        {
            if(!uberUtil.solidBlockExists(x,y+1,z,world))
            {
                return y + 1;
            }
            else
            {
                return GetFirstNonSolidUp(x, y + 1, z);
            }
        }
        else
        {
            return 0;
        }
    }

    private int GetFirstAirUp(int x,int y,int z)
    {
        if(world.isAirBlock(x,y,z))
        {return y;}
        else {
            if (y <= Minecraft.WORLD_HEIGHT_BLOCKS / 2 && y > 8) {
                if (world.isAirBlock(x, y + 1, z)) {
                    return y + 1;
                } else {
                    return GetFirstAirUp(x, y + 1, z);
                }
            } else {
                return 0;
            }
        }
    }
}
