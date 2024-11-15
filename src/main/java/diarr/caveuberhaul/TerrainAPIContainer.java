package diarr.caveuberhaul;

import diarr.caveuberhaul.features.*;
import diarr.caveuberhaul.gen.FastNoiseLite;
import diarr.caveuberhaul.gen.cavebiomes.CaveBiome;
import diarr.caveuberhaul.gen.cavebiomes.CaveBiomeChunkMap;
import diarr.caveuberhaul.gen.cavebiomes.CaveBiomeProvider;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.block.BlockStone;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.animal.EntityWolf;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkPosition;
import net.minecraft.core.world.chunk.ChunkSection;
import net.minecraft.core.world.generate.feature.WorldFeatureOre;
import net.minecraft.core.world.generate.feature.WorldFeatureRichScorchedDirt;
import useless.terrainapi.api.TerrainAPI;
import useless.terrainapi.generation.ChunkDecoratorAPI;
import useless.terrainapi.generation.Parameters;
import useless.terrainapi.generation.overworld.OverworldConfig;
import useless.terrainapi.generation.overworld.api.ChunkDecoratorOverworldAPI;

import java.util.Random;

import static diarr.caveuberhaul.UberUtil.setBlockDirectely;

public class TerrainAPIContainer implements TerrainAPI {
    private static final FastNoiseLite caveBiomeDecoratorNoiseMap = new FastNoiseLite();
    public static final OverworldConfig overworldConfig = ChunkDecoratorOverworldAPI.overworldConfig;
    @Override
    public String getModID() {
        return CaveUberhaul.MOD_ID;
    }
    public void onInitialize() {
        //ChunkDecoratorOverworldAPI.structureFeatures.addFeature(TerrainAPIContainer::generateLaveSwamp, null);
        ChunkDecoratorOverworldAPI.structureFeatures.addFeature(TerrainAPIContainer::generateCaveBiomes, null);
        ChunkDecoratorOverworldAPI.structureFeatures.addFeature(TerrainAPIContainer::func_01, null);
        ChunkDecoratorOverworldAPI.structureFeatures.addFeature(TerrainAPIContainer::func_02, null);
        ChunkDecoratorOverworldAPI.structureFeatures.addFeature(TerrainAPIContainer::func_03, null);
        ChunkDecoratorOverworldAPI.structureFeatures.addFeature(TerrainAPIContainer::func_04, null);

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
    }
    public static Void generateCaveBiomes(Parameters parameters){

        Random rand = parameters.random;
        Chunk chunk = parameters.chunk;
        ChunkDecoratorAPI decorator = parameters.decorator;

        CaveBiomeProvider cbp = CaveBiomeChunkMap.map.get(new ChunkPosition(chunk.xPosition,0,chunk.zPosition));
        if(cbp == null)
        {
            cbp = new CaveBiomeProvider(decorator.world,chunk.xPosition,chunk.zPosition);
        }

        if(cbp.getCaveBiomesInChunk().length != 0) {
            int x = chunk.xPosition * 16;
            int z = chunk.zPosition * 16;
            float[][] caveBiomeDecoratorNoise = UberUtil.getInterpolatedNoiseValue2D(UberUtil.sampleNoise2D(chunk.xPosition, chunk.zPosition, 0.08f, decorator.world, caveBiomeDecoratorNoiseMap, FastNoiseLite.NoiseType.Perlin));
            replaceBlocksForCaveBiome(chunk, x, z, caveBiomeDecoratorNoise, decorator.world, rand, cbp);
            placeBiomeStructures(chunk,x,z,decorator.world,rand,cbp);
        }
        return null;
    }

    private static void placeBiomeStructures(Chunk chunk, int x,int z,World worldObj, Random rand,CaveBiomeProvider cbp)
    {
        for (int s = 0; s < Chunk.CHUNK_SECTIONS; s++) {
            ChunkSection section = chunk.getSection(s);

            short[] data = section.blocks;
            if (data == null) continue;

            for (int lx = 0; lx < Chunk.CHUNK_SIZE_X; lx++) {
                int gx = lx + x;

                for (int lz = 0; lz < Chunk.CHUNK_SIZE_Z; lz++) {
                    int gz = lz + z;

                    for (int ly = 0; ly < ChunkSection.SECTION_SIZE_Y; ly++) {
                        int realY = s * 16 + ly;
                        if (cbp.getCaveBiomeAt(lx, realY, lz) != null) {
                            CaveBiome cb = cbp.getCaveBiomeAt(lx, realY, lz);
                            if (cb != null) {
                                switch (cb.id) {
                                    case 4:
                                        if(rand.nextInt(75)==1)
                                        {
                                            new WorldFeatureSmoker().generate(worldObj,rand,gx,realY,gz);
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void replaceBlocksForCaveBiome(Chunk chunk, int x, int z, float[][] biomeDecNoise, World worldObj, Random rand,CaveBiomeProvider cbp)
    {
        for (int s = 0; s < Chunk.CHUNK_SECTIONS; s++) {
            ChunkSection section = chunk.getSection(s);
            short[] data = section.blocks;
            if (data == null) continue;

            for(int lx = 0; lx < Chunk.CHUNK_SIZE_X; lx++)
            {
                int gx = lx+x;

                for(int lz = 0; lz < Chunk.CHUNK_SIZE_Z; lz++)
                {
                    int gz = lz+z;

                    for(int ly = 0; ly < ChunkSection.SECTION_SIZE_Y; ly++)
                    {
                        int realY = s * 16 + ly;
                        if(cbp.getCaveBiomeAt(lx,realY,lz)!=null) {
                            CaveBiome cb = cbp.getCaveBiomeAt(lx, realY, lz);
                            if (cb != null) {
                                switch (cb.id) {
                                    //Flowstone
                                    case 1: {
                                        //Base Blocks
                                        if (data[ChunkSection.makeBlockIndex(lx, ly, lz)] != 0 && data[ChunkSection.makeBlockIndex(lx, ly, lz)] != Block.bedrock.id && Block.getBlock(data[ChunkSection.makeBlockIndex(lx, ly, lz)]) instanceof BlockStone && Math.abs(biomeDecNoise[lx][lz])>0.2f) {
                                            data[ChunkSection.makeBlockIndex(lx, ly, lz)] = (short) cb.blockList[0].id;
                                        }

                                        //Water Puddles
                                        if (data[ChunkSection.makeBlockIndex(lx, ly, lz)] != 0 && Block.getBlock(data[ChunkSection.makeBlockIndex(lx, ly, lz)]).blockMaterial == Material.stone && rand.nextFloat() >= 0.4f && UberUtil.isSurroundedFreeAboveNoLava(gx, realY, gz, worldObj)) {
                                            if (worldObj.isAirBlock(gx, realY - 1, gz)) {
                                                data[ChunkSection.makeBlockIndex(lx, ly, lz)] = (short) Block.fluidWaterFlowing.id;
                                            } else {
                                                data[ChunkSection.makeBlockIndex(lx, ly, lz)] = (short) Block.fluidWaterStill.id;
                                            }
                                        }

                                        //Pillars
                                        if(rand.nextInt(100)==0) {
                                            new WorldFeatureSmallCavePillar(UberUtil.getCeiling(x, realY + 1, z, 18, worldObj), CaveUberhaul.flowstonePillar.id, CaveUberhaul.flowstonePillar.id, 1.4f + rand.nextInt(2), 18).generate(worldObj, rand, x, realY + 1, z);
                                        }

                                        if((rand.nextInt(20)<=Math.abs(biomeDecNoise[lx][lz]*10)&&(worldObj.getBlockId(gx,realY,gz) == CaveUberhaul.flowstone.id|| worldObj.getBlockId(gx,realY,gz) == CaveUberhaul.flowstonePillar.id) && worldObj.isAirBlock(gx,realY-1,gz)))
                                        {
                                            int length =Math.round(Math.abs(8*biomeDecNoise[lx][lz]))+ rand.nextInt(2);
                                            int pLength = rand.nextInt(length/2+1);
                                            if(rand.nextInt(4)==1&&UberUtil.isSurrounded(lx+x,realY+1,lz+z,worldObj))
                                            {
                                                worldObj.setBlock(lx+x,realY+1,lz+z,Block.fluidWaterStill.id);
                                            }
                                            new WorldFeatureFlowstonePillar(length,pLength).generate(worldObj,rand,lx+x,realY-1,lz+z);
                                        }
                                        break;
                                    }
                                    //Frost
                                    case 2: {
                                        if (data[ChunkSection.makeBlockIndex(lx, ly, lz)] != 0  && Block.getBlock(data[ChunkSection.makeBlockIndex(lx, ly, lz)]) instanceof BlockStone && biomeDecNoise[lx][lz]>-0.2f) {
                                            data[ChunkSection.makeBlockIndex(lx, ly, lz)] = (short) cb.blockList[0].id;
                                        }
                                        if(biomeDecNoise[lx][lz]>0.4)
                                        {
                                            if(Block.getBlock(data[ChunkSection.makeBlockIndex(lx, ly, lz)]) instanceof BlockStone&&worldObj.isAirBlock(gx,realY-1,gz))
                                            {
                                                if(biomeDecNoise[lx][lz]>0.6) {
                                                    setBlockDirectely(chunk, lx, realY + 1, lz, cb.blockList[3].id);
                                                    for(int h = 0;h<=3;h++)
                                                    {
                                                        setBlockDirectely(chunk, lx, realY - h, lz,cb.blockList[1].id);
                                                    }
                                                    setBlockDirectely(chunk, lx, realY - 4, lz,cb.blockList[2].id);
                                                }
                                                else
                                                {
                                                    for(int h = 0;h<=3;h++)
                                                    {
                                                        setBlockDirectely(chunk, lx, realY - h, lz, cb.blockList[2].id);
                                                    }
                                                }
                                            }
                                            if(Block.getBlock(data[ChunkSection.makeBlockIndex(lx, ly, lz)]) instanceof BlockStone&&worldObj.isAirBlock(gx,realY+1,gz))
                                            {
                                                setBlockDirectely(chunk,lx, realY - 1, lz, cb.blockList[3].id);
                                                if(biomeDecNoise[lx][lz]>0.6) {
                                                    setBlockDirectely(chunk, lx, realY + 1, lz, cb.blockList[3].id);
                                                    for(int h = 0;h<=3;h++)
                                                    {
                                                        setBlockDirectely(chunk, lx, realY + h, lz, cb.blockList[1].id);
                                                    }
                                                    setBlockDirectely(chunk,lx, realY + 4, lz, cb.blockList[2].id);
                                                }
                                                else
                                                {
                                                    for(int h = 0;h<=3;h++)
                                                    {
                                                        setBlockDirectely(chunk,lx, realY + h, lz, cb.blockList[2].id);
                                                    }
                                                }
                                            }
                                        }
                                        if((rand.nextInt(32)<=Math.abs(biomeDecNoise[lx][lz]*10)))
                                        {
                                            new WorldFeatureBigIcicle().generate(worldObj,rand,gx,realY,gz);
                                        }
                                        break;
                                    }
                                    //Magma Caves
                                    case 4:
                                    {
                                        if(Math.abs(biomeDecNoise[lx][lz])>0.2f&&data[ChunkSection.makeBlockIndex(lx, ly, lz)]!=0) {
                                            //Create Lava swamps
                                            if (UberUtil.isSurroundedAndFreeAbove(gx, realY, gz, worldObj)) {
                                                if ((rand.nextInt(11) <= Math.abs(biomeDecNoise[lx][lz] * 10))) {
                                                    //data[ChunkSection.makeBlockIndex(lx, ly, lz)] = (short) cb.blockList[2].id;
                                                    worldObj.setBlock(gx,realY,gz,cb.blockList[2].id);
                                                }
                                            }
                                            //Place Blocks
                                            if(data[ChunkSection.makeBlockIndex(lx, ly, lz)]!=Block.fluidLavaStill.id)
                                            {
                                                if(UberUtil.bordersLavaSource(gx,realY,gz,worldObj))
                                                {
                                                    data[ChunkSection.makeBlockIndex(lx, ly, lz)] = (short) cb.blockList[1].id;
                                                }
                                                else {
                                                    data[ChunkSection.makeBlockIndex(lx, ly, lz)] = (short) cb.blockList[0].id;
                                                }
                                            }
                                        }
                                        if(data[ChunkSection.makeBlockIndex(lx, ly, lz)]!=0&&UberUtil.inRange(Math.abs(biomeDecNoise[lx][lz]),0,0.065))
                                        {data[ChunkSection.makeBlockIndex(lx, ly, lz)] = (short) cb.blockList[1].id;}
                                        //Place Smokers
                                        break;
                                    }
                                }
                            }
                        }
                        //placePillars(gx,realY,gz,worldObj, rand, cbp.getCaveBiomeAt(lx,realY,lz,worldObj));
                    }
                }
            }
            section.blocks = data;
        }
    }

    public static Void generateLaveSwamp(Parameters parameters){
        if (parameters.random.nextFloat() < 0.92f) {return null;}
        int x = parameters.chunk.xPosition * 16;
        int z = parameters.chunk.zPosition * 16;
        // Generate Lava Swamp
        int lsx = x + parameters.random.nextInt(16);
        int lsz = z + parameters.random.nextInt(16);
        new WorldFeatureLavaSwamp().generate(parameters.decorator.world, parameters.random, lsx, 14, lsz);
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

}
