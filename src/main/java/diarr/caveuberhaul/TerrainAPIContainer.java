package diarr.caveuberhaul;

import diarr.caveuberhaul.features.*;
import diarr.caveuberhaul.gen.CaveBiomeProvider;
import diarr.caveuberhaul.gen.FastNoiseLite;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockStone;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.animal.EntityWolf;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.feature.WorldFeatureOre;
import net.minecraft.core.world.generate.feature.WorldFeatureRichScorchedDirt;
import useless.terrainapi.generation.overworld.ChunkDecoratorOverworldAPI;

import java.util.Random;

public class TerrainAPIContainer {
    private static final FastNoiseLite caveBiomeDecoratorNoiseMap = new FastNoiseLite();

    public static void initialize(){
        //TODO expand api to allow exact port of original system
        ChunkDecoratorOverworldAPI.BiomeFeatures.addFeatureSurface(new WorldFeatureRichScorchedDirt(10), 1, new Biome[]{Biomes.OVERWORLD_OUTBACK_GRASSY});

        ChunkDecoratorOverworldAPI.OreFeatures.setOreValues(CaveUberhaul.MOD_ID, Block.blockClay.id, 32, 20, 1);
        ChunkDecoratorOverworldAPI.OreFeatures.setOreValues(CaveUberhaul.MOD_ID, Block.dirt.id, 32, 20, 1);
        ChunkDecoratorOverworldAPI.OreFeatures.setOreValues(CaveUberhaul.MOD_ID, Block.gravel.id, 32, 10, 1);
        ChunkDecoratorOverworldAPI.OreFeatures.setOreValues(CaveUberhaul.MOD_ID, Block.oreCoalStone.id, 16, 20, (256-40)/256f);
        ChunkDecoratorOverworldAPI.OreFeatures.setOreValues(CaveUberhaul.MOD_ID, Block.oreIronStone.id, 8, 20, (128-30)/256f);
        ChunkDecoratorOverworldAPI.OreFeatures.setOreValues(CaveUberhaul.MOD_ID,Block.oreGoldStone.id, 8, 2, 1/4f);
        ChunkDecoratorOverworldAPI.OreFeatures.setOreValues(CaveUberhaul.MOD_ID, Block.oreRedstoneStone.id, 7, 8, 1/8f);
        ChunkDecoratorOverworldAPI.OreFeatures.setOreValues(CaveUberhaul.MOD_ID, Block.oreDiamondStone.id, 7, 1, 1/8f);
        ChunkDecoratorOverworldAPI.OreFeatures.setOreValues(CaveUberhaul.MOD_ID,  Block.mossStone.id, 32, 1, (128-32)/256f);
        ChunkDecoratorOverworldAPI.OreFeatures.setOreValues(CaveUberhaul.MOD_ID, Block.oreLapisStone.id, 6, 1, 1/4f);

        ChunkDecoratorOverworldAPI.OreFeatures.addFeature(new WorldFeatureOre(Block.oreIronStone.id, 8, true), 5, 30/256f);

        ChunkDecoratorOverworldAPI.StructureFeature.addStructure(TerrainAPIContainer::func_01, null);
        ChunkDecoratorOverworldAPI.StructureFeature.addStructure(TerrainAPIContainer::func_02, null);
        ChunkDecoratorOverworldAPI.StructureFeature.addStructure(TerrainAPIContainer::func_03, null);
        ChunkDecoratorOverworldAPI.StructureFeature.addStructure(TerrainAPIContainer::func_04, null);
        ChunkDecoratorOverworldAPI.StructureFeature.addStructure(TerrainAPIContainer::generateLaveSwamp, null);
        ChunkDecoratorOverworldAPI.StructureFeature.addStructure(TerrainAPIContainer::generateCaveBiomes, null);
    }
    public static Boolean generateCaveBiomes(Object[] parameters){
        Random rand = (Random) parameters[1];
        Chunk chunk = (Chunk) parameters[2];
        ChunkDecoratorOverworldAPI decorator = (ChunkDecoratorOverworldAPI) parameters[3];

        CaveBiomeProvider caveBiomeProvider = new CaveBiomeProvider();
        int[] caveBiomeValues = caveBiomeProvider.provideCaveBiomeValueChunk(chunk.xPosition,chunk.zPosition, decorator.world);

        int x = chunk.xPosition * 16;
        int z = chunk.zPosition * 16;
        short[] blocks = chunk.blocks;
        float[][] caveBiomeDecoratorNoise = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(chunk.xPosition,chunk.zPosition,0.08f, decorator.world, caveBiomeDecoratorNoiseMap, FastNoiseLite.NoiseType.OpenSimplex2S));
        replaceBlocksForCaveBiome(chunk,blocks,x,z,caveBiomeDecoratorNoise, decorator.world, rand, caveBiomeValues);

        // Generate Flowstone Pillar
        for(int lx = 0;lx<16;lx++)
        {
            for(int lz = 0;lz<16;lz++)
            {
                for(int ly = decorator.world.getHeightBlocks();ly>0;ly--)
                {
                    if((decorator.world.getBlockId(lx+x,ly,lz+z) == CaveUberhaul.flowstone.id|| decorator.world.getBlockId(lx+x,ly,lz+z) == CaveUberhaul.flowstonePillar.id) && decorator.world.isAirBlock(lx+x,ly-1,lz+z) && caveBiomeDecoratorNoise[lx][lz]>0f && caveBiomeDecoratorNoise[lx][lz]<0.5f)
                    {
                        int length =Math.round(Math.abs(8*caveBiomeDecoratorNoise[lx][lz]))+ rand.nextInt(3);
                        int plength = rand.nextInt(length/2+1);
                        if(rand.nextInt(3)==1&&UberUtil.isSurrounded(lx+x,ly+1,lz+z,decorator.world))
                        {
                            decorator.world.setBlock(lx+x,ly+1,lz+z,Block.fluidWaterStill.id);
                        }
                        new WorldFeatureFlowstonePillar(length,plength).generate(decorator.world,rand,lx+x,ly-1,lz+z);
                    }
                }
            }
        }
        return true;
    }
    public static Boolean generateLaveSwamp(Object[] parameters){
        Random rand = (Random) parameters[1];
        if (rand.nextFloat() < 0.92f) {return true;}
        Chunk chunk = (Chunk) parameters[2];
        ChunkDecoratorOverworldAPI decorator = (ChunkDecoratorOverworldAPI) parameters[3];
        int x = chunk.xPosition * 16;
        int z = chunk.zPosition * 16;
        // Generate Lava Swamp
        int lsx = x + rand.nextInt(16);
        int lsz = z + rand.nextInt(16);
        new WorldFeatureLavaSwamp().generate(decorator.world, rand, lsx, 10, lsz);
        return true;
    }
    public static Boolean func_01(Object[] parameters){
        Random rand = (Random) parameters[1];
        if(rand.nextFloat()>0.0001f) {return true;}
        Chunk chunk = (Chunk) parameters[2];
        ChunkDecoratorOverworldAPI decorator = (ChunkDecoratorOverworldAPI) parameters[3];
        int x = chunk.xPosition * 16;
        int z = chunk.zPosition * 16;
        int px = x + rand.nextInt(16);
        int pz = z + rand.nextInt(16);
        int py = rand.nextInt(decorator.rangeY/2);
        if(decorator.world.isAirBlock(px,py,pz) && decorator.world.isBlockNormalCube(px, py, pz + 1)) {
            decorator.world.setBlock(px, py, pz, Block.torchCoal.id);
            decorator.world.setBlockMetadataWithNotify(px, py, pz, 4);
        }
        return true;
    }
    public static Boolean func_02(Object[] parameters){
        Random rand = (Random) parameters[1];
        if(rand.nextFloat()>0.01f) {return true;}
        Chunk chunk = (Chunk) parameters[2];
        ChunkDecoratorOverworldAPI decorator = (ChunkDecoratorOverworldAPI) parameters[3];
        int x = chunk.xPosition * 16;
        int z = chunk.zPosition * 16;
        int rx = x + rand.nextInt(16);
        int ry = rand.nextInt(decorator.rangeY);
        int rz = z + rand.nextInt(16);
        if(decorator.world.isAirBlock(rx,ry,rz)&& !decorator.world.isAirBlock(rx,ry-1,rz)&&Block.getBlock(decorator.world.getBlockId(rx,ry-1,rz)).blockMaterial.isSolid()) {
            EntityWolf entityWolf = new EntityWolf(decorator.world);
            decorator.world.entityJoinedWorld(entityWolf);
            entityWolf.setPos(rx,ry,rz);
            entityWolf.setRot(decorator.world.rand.nextFloat() * 360.0F, 0.0F);
            entityWolf.setWolfTamed(true);
            entityWolf.setWolfOwner("");
            entityWolf.setWolfAngry(true);
        }
        return true;
    }
    public static Boolean func_03(Object[] parameters){
        Random rand = (Random) parameters[1];
        if(rand.nextFloat()>0.00001f){return true;}
        Chunk chunk = (Chunk) parameters[2];
        ChunkDecoratorOverworldAPI decorator = (ChunkDecoratorOverworldAPI) parameters[3];
        int x = chunk.xPosition * 16;
        int z = chunk.zPosition * 16;
        int px = x + rand.nextInt(16);
        int pz = z + rand.nextInt(16);
        int py = rand.nextInt(decorator.rangeY/3);
        for(int i=0;i<=rand.nextInt(39)+11;i++) {
            decorator.world.setBlock(px+i, py, pz, 0);
            decorator.world.setBlock(px+i, py+1, pz, 0);
            if(i%6==0&& Block.torchRedstoneActive.canPlaceBlockAt(decorator.world,px+i,py+1,pz))
            {
                decorator.world.setBlock(px+i, py+1, pz, Block.torchRedstoneActive.id);
                decorator.world.setBlockMetadataWithNotify(px+i,py+1,pz,3);
            }
        }
        return true;
    }
    public static Boolean func_04(Object[] parameters){
        Random rand = (Random) parameters[1];
        Chunk chunk = (Chunk) parameters[2];
        ChunkDecoratorOverworldAPI decorator = (ChunkDecoratorOverworldAPI) parameters[3];
        int x = chunk.xPosition * 16;
        int z = chunk.zPosition * 16;
        for (int k1 = 0; k1 < (8 * decorator.oreHeightModifier); k1++) {
            if(rand.nextInt(100000)!=0) {continue;}
            int j5 = x + rand.nextInt(16) + 8;
            int k8 = decorator.minY + rand.nextInt(decorator.rangeY);
            int j11 = z + rand.nextInt(16) + 8;
            new WorldFeatureDungeoni(Block.cobbleStone.id, Block.cobbleStoneMossy.id, null).generate(decorator.world, rand, j5, k8, j11);
        }
        return true;
    }

    private static void placePillars(int x, int y, int z, int xChunk, int zChunk, World worldObj, Random rand, int[] caveBiomeValues)
    {
        int gx = x+xChunk;
        int gz = z+zChunk;
        if(worldObj.isBlockNormalCube(gx,y,gz)&&worldObj.isAirBlock(gx,y+1,gz)) {
            float pillarChance;
            int bigPillarChance;
            if (caveBiomeValues[x << worldObj.getHeightBits() + 4 | z << worldObj.getHeightBits() | y] == 1) {

                pillarChance = 0.01F;
                bigPillarChance = 2;
                if(rand.nextFloat()< pillarChance) {
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
                if(rand.nextFloat()< pillarChance) {
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
    private static void replaceBlocksForCaveBiome(Chunk chunk, short[] data, int x, int z, float[][] biomeDecNoise, World worldObj, Random rand, int[] caveBiomeValues)
    {
        boolean placeFlowstone;
        for(int lx = 0; lx<16; lx++)
        {
            int gx = lx+x;
            for(int lz = 0; lz<16; lz++)
            {
                int gz = lz+z;
                placeFlowstone = biomeDecNoise[lx][lz]>-0.2f;
                for(int ly = worldObj.getHeightBlocks()-1; ly > 0; ly--)
                {
                    if (caveBiomeValues[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] == 1) {
                        if (data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] != 0 && data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] != Block.bedrock.id && Block.getBlock(data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly]) instanceof BlockStone && placeFlowstone) {
                            data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] = (short) CaveUberhaul.flowstone.id;
                        }

                        if (data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] != 0 && Block.getBlock(data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly]).blockMaterial == Material.stone && rand.nextFloat() >= 0.4f && UberUtil.isSurroundedFreeAboveNoLava(x + lx, ly, z + lz, worldObj)) {
                            //worldObj.setBlock(x + lx, ly, z + lz, Block.fluidWaterStill.blockID);
                            if (worldObj.isAirBlock(gx, ly - 1, gz)) {
                                data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] = (short) Block.fluidWaterFlowing.id;
                            } else {
                                data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] = (short) Block.fluidWaterStill.id;
                            }
                        }
                    }
                    placePillars(lx,ly,lz,x,z, worldObj, rand, caveBiomeValues);
                }
            }
        }
        chunk.blocks = data;
    }
}
