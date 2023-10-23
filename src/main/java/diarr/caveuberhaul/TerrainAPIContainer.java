package diarr.caveuberhaul;

import diarr.caveuberhaul.features.*;
import diarr.caveuberhaul.gen.cavebiomes.CaveBiomeProvider;
import diarr.caveuberhaul.gen.FastNoiseLite;
import diarr.caveuberhaul.gen.cavebiomes.CaveBiome;
import diarr.caveuberhaul.gen.cavebiomes.CaveBiomeChunkMap;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockStone;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.animal.EntityWolf;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkPosition;
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


        int x = chunk.xPosition * 16;
        int z = chunk.zPosition * 16;
        short[] blocks = chunk.blocks;
        float[][] caveBiomeDecoratorNoise = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(chunk.xPosition,chunk.zPosition,0.08f, decorator.world, caveBiomeDecoratorNoiseMap, FastNoiseLite.NoiseType.OpenSimplex2S));
        replaceBlocksForCaveBiome(chunk,blocks,x,z,caveBiomeDecoratorNoise, decorator.world, rand);

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
        if (rand.nextFloat() < 0.92f) {return false;}
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

    private static void placePillars(int x, int y, int z, int xChunk, int zChunk, World worldObj, Random rand, CaveBiome cb)
    {
        int gx = x+xChunk;
        int gz = z+zChunk;
        if(worldObj.isBlockNormalCube(gx,y,gz)&&worldObj.isAirBlock(gx,y+1,gz)) {
            if(cb !=null)
            {
                switch(cb.id)
                {
                    case 1:
                    {
                        if(rand.nextInt(cb.smallPillarChance)==0) {
                            if (rand.nextInt(cb.bigPillarChance) == 0) {
                                new WorldFeatureCavePillar(y, UberUtil.getCeiling(gx, y + 1, gz, 40, worldObj), cb.blockList[1].id, cb.blockList[1].id).generate(worldObj, rand, gx, y + 1, gz);
                            } else {
                                new WorldFeatureSmallCavePillar(y, UberUtil.getCeiling(gx, y + 1, gz, 18, worldObj), cb.blockList[1].id, cb.blockList[1].id).generate(worldObj, rand, gx, y + 1, gz);
                            }
                        }
                        break;
                    }
                    case 2:
                    {
                        if(rand.nextInt(cb.smallPillarChance)==0) {
                            if (rand.nextInt(cb.bigPillarChance) == 0) {
                                if(rand.nextInt(2)==0) {
                                    new WorldFeatureCavePillar(y, UberUtil.getCeiling(gx, y + 1, gz, 40, worldObj), cb.blockList[1].id, cb.blockList[1].id).generate(worldObj, rand, gx, y + 1, gz);
                                }
                                else
                                {
                                    new WorldFeatureCavePillar(y, UberUtil.getCeiling(gx, y + 1, gz, 40, worldObj), cb.blockList[2].id, cb.blockList[2].id).generate(worldObj, rand, gx, y + 1, gz);
                                }
                            } else {
                                if(rand.nextInt(2)==0) {
                                    new WorldFeatureSmallCavePillar(y, UberUtil.getCeiling(gx, y + 1, gz, 18, worldObj), cb.blockList[1].id, cb.blockList[1].id).generate(worldObj, rand, gx, y + 1, gz);
                                }
                                else
                                {
                                    new WorldFeatureSmallCavePillar(y, UberUtil.getCeiling(gx, y + 1, gz, 18, worldObj), cb.blockList[2].id, cb.blockList[2].id).generate(worldObj, rand, gx, y + 1, gz);
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    private static void replaceBlocksForCaveBiome(Chunk chunk, short[] data, int x, int z, float[][] biomeDecNoise, World worldObj, Random rand)
    {
        CaveBiomeProvider cbp = CaveBiomeChunkMap.map.get(new ChunkPosition(chunk.xPosition,0,chunk.zPosition));//new CaveBiomeProvider(worldObj,chunk);
        if(cbp == null)
        {
            cbp = new CaveBiomeProvider(worldObj,chunk.xPosition,chunk.zPosition);
        }
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
                    if(cbp.getCaveBiomeAt(lx,ly,lz,worldObj)!=null) {
                        CaveBiome cb = cbp.getCaveBiomeAt(lx, ly, lz, worldObj);
                        if (cb != null) {
                            switch (cb.id) {
                                case 1: {
                                    if (data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] != 0 && data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] != Block.bedrock.id && Block.getBlock(data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly]) instanceof BlockStone && placeFlowstone) {
                                        data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] = (short) cb.blockList[0].id;
                                    }

                                    if (data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] != 0 && Block.getBlock(data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly]).blockMaterial == Material.stone && rand.nextFloat() >= 0.4f && UberUtil.isSurroundedFreeAboveNoLava(x + lx, ly, z + lz, worldObj)) {
                                        if (worldObj.isAirBlock(gx, ly - 1, gz)) {
                                            data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] = (short) Block.fluidWaterFlowing.id;
                                        } else {
                                            data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] = (short) Block.fluidWaterStill.id;
                                        }
                                    }
                                    break;
                                }
                                case 2: {
                                    if (data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] != 0  && Block.getBlock(data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly]) instanceof BlockStone && placeFlowstone) {
                                            data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly] = (short) cb.blockList[0].id;
                                    }
                                    if(biomeDecNoise[lx][lz]>0.4)
                                    {
                                        if(Block.getBlock(data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly]) instanceof BlockStone&&worldObj.isAirBlock(gx,ly-1,gz))
                                        {
                                            if(biomeDecNoise[lx][lz]>0.6) {
                                                data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly+1] =(short) cb.blockList[3].id;
                                                for(int h = 0;h<=3;h++)
                                                {
                                                    data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly-h] = (short) cb.blockList[1].id;
                                                }
                                                data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly - 4] = (short) cb.blockList[2].id;
                                            }
                                            else
                                            {
                                                for(int h = 0;h<=3;h++)
                                                {
                                                    data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly-h] = (short) cb.blockList[2].id;
                                                }
                                            }
                                        }
                                        if(Block.getBlock(data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly]) instanceof BlockStone&&worldObj.isAirBlock(gx,ly+1,gz))
                                        {
                                            data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly-1] =(short) cb.blockList[3].id;
                                            if(biomeDecNoise[lx][lz]>0.6) {
                                                data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly+1] =(short) cb.blockList[3].id;
                                                for(int h = 0;h<=3;h++)
                                                {
                                                    data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly+h] = (short) cb.blockList[1].id;
                                                }
                                                data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly + 4] = (short) cb.blockList[2].id;
                                            }
                                            else
                                            {
                                                for(int h = 0;h<=3;h++)
                                                {
                                                    data[lx << worldObj.getHeightBits() + 4 | lz << worldObj.getHeightBits() | ly+h] = (short) cb.blockList[2].id;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    placePillars(lx,ly,lz,x,z, worldObj, rand, cbp.getCaveBiomeAt(lx,ly,lz,worldObj));
                }
            }
        }
        chunk.blocks = data;
    }
}
