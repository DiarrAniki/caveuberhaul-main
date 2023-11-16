package diarr.caveuberhaul;

import diarr.caveuberhaul.features.*;
import diarr.caveuberhaul.gen.FastNoiseLite;
import diarr.caveuberhaul.gen.cavebiomes.CaveBiome;
import diarr.caveuberhaul.gen.cavebiomes.CaveBiomeChunkMap;
import diarr.caveuberhaul.gen.cavebiomes.CaveBiomeProvider;
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
import useless.terrainapi.api.TerrainAPI;
import useless.terrainapi.generation.ChunkDecoratorAPI;
import useless.terrainapi.generation.Parameters;
import useless.terrainapi.generation.overworld.OverworldConfig;
import useless.terrainapi.generation.overworld.api.ChunkDecoratorOverworldAPI;

import java.util.Random;

public class TerrainAPIContainer implements TerrainAPI {
    private static final FastNoiseLite caveBiomeDecoratorNoiseMap = new FastNoiseLite();
    public static final OverworldConfig overworldConfig = ChunkDecoratorOverworldAPI.overworldConfig;
    @Override
    public String getModID() {
        return CaveUberhaul.MOD_ID;
    }
    public void onInitialize() {
        ChunkDecoratorOverworldAPI.biomeFeatures.addFeatureSurface(new WorldFeatureRichScorchedDirt(10), 1, new Biome[]{Biomes.OVERWORLD_OUTBACK_GRASSY});

        overworldConfig.setOreValues(CaveUberhaul.MOD_ID, Block.blockClay, 32, 20, 1);
        overworldConfig.setOreValues(CaveUberhaul.MOD_ID, Block.dirt, 32, 20, 1);
        overworldConfig.setOreValues(CaveUberhaul.MOD_ID, Block.gravel, 32, 10, 1);
        overworldConfig.setOreValues(CaveUberhaul.MOD_ID, Block.oreCoalStone, 16, 20, (256-40)/256f);
        overworldConfig.setOreValues(CaveUberhaul.MOD_ID, Block.oreIronStone, 8, 20, (128-30)/256f);
        overworldConfig.setOreValues(CaveUberhaul.MOD_ID,Block.oreGoldStone, 8, 2, 1/4f);
        overworldConfig.setOreValues(CaveUberhaul.MOD_ID, Block.oreRedstoneStone, 7, 8, 1/8f);
        overworldConfig.setOreValues(CaveUberhaul.MOD_ID, Block.oreDiamondStone, 7, 1, 1/8f);
        overworldConfig.setOreValues(CaveUberhaul.MOD_ID,  Block.mossStone, 32, 1, (128-32)/256f);
        overworldConfig.setOreValues(CaveUberhaul.MOD_ID, Block.oreLapisStone, 6, 1, 1/4f);

        ChunkDecoratorOverworldAPI.oreFeatures.addFeature(new WorldFeatureOre(Block.oreIronStone.id, 8, true), 5, 30/256f);

        ChunkDecoratorOverworldAPI.structureFeatures.addFeature(TerrainAPIContainer::func_01, null);
        ChunkDecoratorOverworldAPI.structureFeatures.addFeature(TerrainAPIContainer::func_02, null);
        ChunkDecoratorOverworldAPI.structureFeatures.addFeature(TerrainAPIContainer::func_03, null);
        ChunkDecoratorOverworldAPI.structureFeatures.addFeature(TerrainAPIContainer::func_04, null);
        ChunkDecoratorOverworldAPI.structureFeatures.addFeature(TerrainAPIContainer::generateLaveSwamp, null);
        ChunkDecoratorOverworldAPI.structureFeatures.addFeature(TerrainAPIContainer::generateCaveBiomes, null);
    }
    public static Void generateCaveBiomes(Parameters parameters){
        Random rand = parameters.random;
        Chunk chunk = parameters.chunk;
        ChunkDecoratorAPI decorator = parameters.decorator;

        int x = chunk.xPosition * 16;
        int z = chunk.zPosition * 16;
        short[] blocks = chunk.blocks;
        float[][] caveBiomeDecoratorNoise = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(chunk.xPosition,chunk.zPosition,0.08f, decorator.world, caveBiomeDecoratorNoiseMap, FastNoiseLite.NoiseType.OpenSimplex2S));
        replaceBlocksForCaveBiome(chunk,blocks,x,z,caveBiomeDecoratorNoise, decorator.world, rand);

        for(int lx = 0;lx<16;lx++)
        {
            for(int lz = 0;lz<16;lz++)
            {
                for(int ly = decorator.world.getHeightBlocks();ly>0;ly--)
                {
                    if(ly>110&&caveBiomeDecoratorNoise[lx][lz]>0&&rand.nextInt(2)==0&&chunk.getBlockID(lx,ly,lz)==0&&chunk.getBlockID(lx,ly-1,lz)==Block.stone.id&&chunk.canBlockSeeTheSky(lx,ly,lz))
                    {
                        chunk.setBlockID(lx,ly-1,lz,Block.gravel.id);
                    }
                    if((rand.nextInt(2)==0&&caveBiomeDecoratorNoise[lx][lz]>0f && caveBiomeDecoratorNoise[lx][lz]<=0.5f)&&(decorator.world.getBlockId(lx+x,ly,lz+z) == CaveUberhaul.flowstone.id|| decorator.world.getBlockId(lx+x,ly,lz+z) == CaveUberhaul.flowstonePillar.id) && decorator.world.isAirBlock(lx+x,ly-1,lz+z))
                    {
                        int length =Math.round(Math.abs(8*caveBiomeDecoratorNoise[lx][lz]))+ rand.nextInt(2);
                        int plength = rand.nextInt(length/2+1);
                        if(rand.nextInt(4)==1&&UberUtil.isSurrounded(lx+x,ly+1,lz+z,decorator.world))
                        {
                            decorator.world.setBlock(lx+x,ly+1,lz+z,Block.fluidWaterStill.id);
                        }
                        new WorldFeatureFlowstonePillar(length,plength).generate(decorator.world,rand,lx+x,ly-1,lz+z);
                    }
                    if(rand.nextInt(6)==0&&caveBiomeDecoratorNoise[lx][lz]>0f && caveBiomeDecoratorNoise[lx][lz]<0.5f)
                    {
                        new WorldFeatureBigIcicle().generate(decorator.world,rand,lx+x,ly,lz+z);
                    }
                }
            }
        }
        return null;
    }
    public static Void generateLaveSwamp(Parameters parameters){
        if (parameters.random.nextFloat() < 0.92f) {return null;}
        int x = parameters.chunk.xPosition * 16;
        int z = parameters.chunk.zPosition * 16;
        // Generate Lava Swamp
        int lsx = x + parameters.random.nextInt(16);
        int lsz = z + parameters.random.nextInt(16);
        new WorldFeatureLavaSwamp().generate(parameters.decorator.world, parameters.random, lsx, 10, lsz);
        return null;
    }
    public static Void func_01(Parameters parameters){
        if(parameters.random.nextFloat()>0.0001f) {return null;}
        int x = parameters.chunk.xPosition * 16;
        int z = parameters.chunk.zPosition * 16;
        int px = x + parameters.random.nextInt(16);
        int pz = z + parameters.random.nextInt(16);
        int py = parameters.random.nextInt(parameters.decorator.rangeY/2);
        if(parameters.decorator.world.isAirBlock(px,py,pz) && parameters.decorator.world.isBlockNormalCube(px, py, pz + 1)) {
            parameters.decorator.world.setBlock(px, py, pz, Block.torchCoal.id);
            parameters.decorator.world.setBlockMetadataWithNotify(px, py, pz, 4);
        }
        return null;
    }
    public static Void func_02(Parameters parameters){
        if(parameters.random.nextFloat()>0.01f) {return null;}
        int x = parameters.chunk.xPosition * 16;
        int z = parameters.chunk.zPosition * 16;
        int rx = x + parameters.random.nextInt(16);
        int ry = parameters.random.nextInt(parameters.decorator.rangeY);
        int rz = z + parameters.random.nextInt(16);
        if(parameters.decorator.world.isAirBlock(rx,ry,rz)&& !parameters.decorator.world.isAirBlock(rx,ry-1,rz)&&Block.getBlock(parameters.decorator.world.getBlockId(rx,ry-1,rz)).blockMaterial.isSolid()) {
            EntityWolf entityWolf = new EntityWolf(parameters.decorator.world);
            parameters.decorator.world.entityJoinedWorld(entityWolf);
            entityWolf.setPos(rx,ry,rz);
            entityWolf.setRot(parameters.decorator.world.rand.nextFloat() * 360.0F, 0.0F);
            entityWolf.setWolfTamed(true);
            entityWolf.setWolfOwner("");
            entityWolf.setWolfAngry(true);
        }
        return null;
    }
    public static Void func_03(Parameters parameters){
        if(parameters.random.nextFloat()>0.00001f){return null;}
        int x = parameters.chunk.xPosition * 16;
        int z = parameters.chunk.zPosition * 16;
        int px = x + parameters.random.nextInt(16);
        int pz = z + parameters.random.nextInt(16);
        int py = parameters.random.nextInt(parameters.decorator.rangeY/3);
        for(int i=0;i<= parameters.random.nextInt(39)+11;i++) {
            parameters.decorator.world.setBlock(px+i, py, pz, 0);
            parameters.decorator.world.setBlock(px+i, py+1, pz, 0);
            if(i%6==0&& Block.torchRedstoneActive.canPlaceBlockAt(parameters.decorator.world,px+i,py+1,pz))
            {
                parameters.decorator.world.setBlock(px+i, py+1, pz, Block.torchRedstoneActive.id);
                parameters.decorator.world.setBlockMetadataWithNotify(px+i,py+1,pz,3);
            }
        }
        return null;
    }
    public static Void func_04(Parameters parameters){
        int x = parameters.chunk.xPosition * 16;
        int z = parameters.chunk.zPosition * 16;
        for (int k1 = 0; k1 < (8 * parameters.decorator.oreHeightModifier); k1++) {
            if(parameters.random.nextInt(100000)!=0) {continue;}
            int j5 = x + parameters.random.nextInt(16) + 8;
            int k8 = parameters.decorator.minY + parameters.random.nextInt(parameters.decorator.rangeY);
            int j11 = z + parameters.random.nextInt(16) + 8;
            new WorldFeatureDungeoni(Block.cobbleStone.id, Block.cobbleStoneMossy.id, null).generate(parameters.decorator.world, parameters.random, j5, k8, j11);
        }
        return null;
    }

    private static void placePillars(int x, int y, int z, World worldObj, Random rand, CaveBiome cb)
    {
        if(worldObj.isBlockNormalCube(x,y,z)&&worldObj.isAirBlock(x,y+1,z)) {
            if(cb !=null)
            {
                switch(cb.id)
                {
                    case 1:
                    {
                        if(rand.nextInt(cb.smallPillarChance)==0) {
                            if (rand.nextInt(cb.bigPillarChance) == 0) {
                                new WorldFeatureCavePillar(UberUtil.getCeiling(x, y + 1, z, 40, worldObj), cb.blockList[1].id, cb.blockList[1].id).generate(worldObj, rand, x, y+1, z);
                            } else {
                                new WorldFeatureSmallCavePillar(UberUtil.getCeiling(x, y + 1, z, 18, worldObj), cb.blockList[1].id, cb.blockList[1].id,1.4f+ rand.nextInt(2),18).generate(worldObj, rand, x, y+1, z);
                            }
                        }
                        break;
                    }
                    case 2:
                    {
                        if(rand.nextInt(cb.smallPillarChance)==0) {
                            if (rand.nextInt(cb.bigPillarChance) == 0) {
                                if(rand.nextInt(2)==0) {
                                    new WorldFeatureCavePillar(UberUtil.getCeiling(x, y + 1, z, 40, worldObj), cb.blockList[1].id, cb.blockList[1].id).generate(worldObj, rand, x, y+1, z);
                                }
                                else
                                {
                                    new WorldFeatureCavePillar(UberUtil.getCeiling(x, y + 1, z, 40, worldObj), cb.blockList[2].id, cb.blockList[2].id).generate(worldObj, rand, x, y+1, z);
                                }
                            } else {
                                if(rand.nextInt(2)==0) {
                                    new WorldFeatureSmallCavePillar(UberUtil.getCeiling(x, y + 1, z, 18, worldObj), cb.blockList[1].id, cb.blockList[1].id,1.4f+ rand.nextInt(2),18).generate(worldObj, rand, x, y+1, z);
                                }
                                else
                                {
                                    new WorldFeatureSmallCavePillar(UberUtil.getCeiling(x, y + 1, z, 18, worldObj), cb.blockList[2].id, cb.blockList[2].id,1.4f+ rand.nextInt(2),18).generate(worldObj, rand, x, y+1, z);
                                }
                            }
                        }
                        break;
                    }
                }
            }
            else
            {
                if(rand.nextInt(500)==0) {
                    if (rand.nextInt(5) == 0) {
                        int top = UberUtil.getCeiling(x, y + 1, z, 40, worldObj);
                        new WorldFeatureCavePillar(top, UberUtil.getPillarBlock(x,y,z,worldObj), UberUtil.getPillarBlock(x,top,z,worldObj)).generate(worldObj, rand, x, y+1 , z);
                    } else {
                        int top = UberUtil.getCeiling(x, y + 1, z, 18, worldObj);
                        new WorldFeatureSmallCavePillar(top, UberUtil.getPillarBlock(x,y,z,worldObj), UberUtil.getPillarBlock(x,top,z,worldObj),1.4f+ rand.nextInt(2),18).generate(worldObj, rand, x, y+1, z);
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
                    placePillars(gx,ly,gz,worldObj, rand, cbp.getCaveBiomeAt(lx,ly,lz,worldObj));
                }
            }
        }
        chunk.blocks = data;
    }
}
