package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.*;
import net.minecraft.shared.Minecraft;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

//Huge thanks to Worley and the Worley Caves mod https://www.curseforge.com/minecraft/mc-mods/worleys-caves for explaining how alot of this works.
@Mixin(value= ChunkProviderGenerateOverworld.class,remap = false)
public class ChunkProviderGenerateOverworldMixin {

    private UberUtil uberUtil = new UberUtil();

    private CaveBiomeProvider caveBiomeProvider = new CaveBiomeProvider();
    private static FastNoiseLite noFlowstoneNoiseMap = new FastNoiseLite();

    protected MapGenBase caveGen = new MapGenNoiseCaves(false);
    private int[][][] caveBiomeValues;

    @Shadow
    protected World worldObj;
    @Shadow
    protected Random rand;
    @Shadow
    protected int terrainMaxHeight;
    @Shadow
    protected int heightModifier;
    @Shadow
    protected int treeDensityOverride;

    @Shadow
    public NoiseGeneratorOctaves mobSpawnerNoise;

    @Shadow
    protected double[] generatedTemperatures;
    @Shadow
    protected BiomeGenBase[] biomesForGeneration;
     @Shadow
     protected int oceanHeight;

    //@Shadow protected MapGenBase caveGen;

    @Inject(method = "provideChunk", at = @At("HEAD"),cancellable = true)
    public void provideChunk(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
        short[] ashort0 = new short[256 * Minecraft.WORLD_HEIGHT_BLOCKS];
        Chunk chunk = new Chunk(this.worldObj, ashort0, x, z);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16);

        for(int i = 0; i < 256; ++i) {
            chunk.biome[i] = (byte)this.biomesForGeneration[i].id;
        }

        ((ChunkProviderGenerateOverworld)(Object)this).generateTerrain(x, z, ashort0);
        ((ChunkProviderGenerateOverworld)(Object)this).replaceBlocksForBiome(x, z, ashort0, this.biomesForGeneration);
        caveGen.generate(((ChunkProviderGenerateOverworld)(Object)this), this.worldObj, x, z, ashort0);
        chunk.func_1024_c();
        cir.setReturnValue(chunk);
    }

    @Inject(method = "populate", at = @At("HEAD"),cancellable = true)
    public void populate(IChunkProvider ichunkprovider, int chunkX, int chunkZ, CallbackInfo ci)
    {
        BlockSand.fallInstantly = true;
        int x = chunkX * 16;
        int z = chunkZ * 16;
        BiomeGenBase biomegenbase = this.worldObj.getWorldChunkManager().getBiomeGenAt(x + 16, z + 16);
        caveBiomeValues = caveBiomeProvider.provideCaveBiomeValueChunk(chunkX,chunkZ,worldObj);
        this.rand.setSeed(this.worldObj.getRandomSeed());
        long l1 = this.rand.nextLong() / 2L * 2L + 1L;
        long l2 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)chunkX * l1 + (long)chunkZ * l2 ^ this.worldObj.getRandomSeed());
        double d = 0.25;

        int pillarChance = 4;

        Chunk chunk = worldObj.getChunkFromChunkCoords(chunkX,chunkZ);
        short[] blocks = chunk.blocks;
        replaceBlocksForCaveBiome(chunk,blocks,x,z,worldObj);


        if(rand.nextFloat()>0.92f) {
            int lsx = x + rand.nextInt(16);
            int lsz = z + rand.nextInt(16);
            new WorldFeatureLavaSwamp().generate(worldObj, rand, lsx, 10, lsz);
        }

        for(int i=0;i<=rand.nextInt(8);i++) {
            int px = x + rand.nextInt(16);
            int pz = z + rand.nextInt(16);
            int py = 11+rand.nextInt(terrainMaxHeight-11);
            if(worldObj.isAirBlock(px,py,pz)) {
                new WorldFeatureCavePillar().generate(worldObj, rand, px, py, pz);
            }
        }

        for(int i=0;i<=4+rand.nextInt(12);i++) {
            int px = x + rand.nextInt(16);
            int pz = z + rand.nextInt(16);
            int py = 40 + rand.nextInt(terrainMaxHeight-40);
            if(worldObj.isAirBlock(px,py,pz)) {
                new WorldFeatureSmallCavePillar().generate(worldObj, rand, px, py, pz);
            }
        }

        for(int i=0;i<=rand.nextInt(4);i++) {
            int px = x + rand.nextInt(16);
            int pz = z + rand.nextInt(16);
            int py = rand.nextInt(terrainMaxHeight);
            if(worldObj.isAirBlock(px,py,pz)) {
                new WorldFeatureSmallCavePillar().generate(worldObj, rand, px, py, pz);
            }
        }


        int lakeChance = 4;
        if (biomegenbase == BiomeGenBase.swampland) {
            lakeChance = 2;
        }

        if (biomegenbase == BiomeGenBase.desert) {
            lakeChance = 0;
        }
        if (lakeChance != 0 && rand.nextInt(lakeChance) == 0) {
            int i1 = x + rand.nextInt(16) + 8;
            int l4 = rand.nextInt(terrainMaxHeight);
            int i8 = z + rand.nextInt(16) + 8;
            (new WorldGenLakes(Block.fluidWaterStill.blockID)).generate(worldObj, rand, i1, l4, i8);
        }
        if (rand.nextInt(8) == 0) {
            int xf = x + rand.nextInt(16) + 8;
            int yf = this.rand.nextInt(this.rand.nextInt(this.terrainMaxHeight - this.terrainMaxHeight / 16) + this.terrainMaxHeight / 16);
            int zf = z + rand.nextInt(16) + 8;
            if (yf < 64 || this.rand.nextInt(10) == 0) {
                (new WorldGenLakes(Block.fluidLavaStill.blockID)).generate(this.worldObj, this.rand, xf, yf, zf);
            }
        }



        for (int k1 = 0; k1 < (8 * this.heightModifier); k1++) {
            int j5 = x + rand.nextInt(16) + 8;
            int k8 = this.rand.nextInt(this.terrainMaxHeight);
            int j11 = z + rand.nextInt(16) + 8;
            if (rand.nextInt(2) == 0) {
                (new WorldGenDungeon(Block.brickClay.blockID, Block.brickClay.blockID, null)).generate(worldObj, rand, j5, k8, j11);
            } else {
                (new WorldGenDungeon(Block.cobbleStone.blockID, Block.cobbleStoneMossy.blockID, null)).generate(worldObj, rand, j5, k8, j11);
            }
        }

        for (int k1 = 0; k1 < 1; k1++) {
            int j5 = x + rand.nextInt(16) + 8;
            int j11 = z + rand.nextInt(16) + 8;
            int k8 = worldObj.getHeightValue(j5, j11) - (rand.nextInt(2) + 2);

            //chance to sink labyrinth
            if (rand.nextInt(5) == 0)
                k8 -= rand.nextInt(10) + 30;

            if (rand.nextInt(700) == 0) {
                (new WorldGenLabyrinth()).generate(worldObj, rand, j5, k8, j11);
            }
        }

        for (int i2 = 0; i2 < (20 * this.heightModifier); i2++) {
            int k5 = x + rand.nextInt(16);
            int l8 = this.rand.nextInt(this.terrainMaxHeight);
            int k11 = z + rand.nextInt(16);
            (new WorldGenClay(32)).generate(worldObj, rand, k5, l8, k11);
        }
        if (biomegenbase == BiomeGenBase.outback) {
            int l5 = x + rand.nextInt(16);
            int l11 = z + rand.nextInt(16);
            int i9 = worldObj.getHeightValue(l5, l11);
            (new WorldGenRichDirt(20)).generate(worldObj, rand, l5, i9, l11);
        }

        for (int j2 = 0; j2 < (20 * this.heightModifier); j2++) {
            int l5 = x + rand.nextInt(16);
            int i9 = 32+this.rand.nextInt(this.terrainMaxHeight-32);
            int l11 = z + rand.nextInt(16);
            (new WorldGenMinable(Block.dirt.blockID, 32, false)).generate(worldObj, rand, l5, i9, l11);
        }

        for (int k2 = 0; k2 < (10 * this.heightModifier); k2++) {
            int i6 = x + rand.nextInt(16);
            int j9 = this.rand.nextInt(this.terrainMaxHeight);
            int i12 = z + rand.nextInt(16);
            (new WorldGenMinable(Block.gravel.blockID, 32, false)).generate(worldObj, rand, i6, j9, i12);
        }

        for (int i3 = 0; i3 < (20 * this.heightModifier); i3++) {
            int j6 = x + rand.nextInt(16);
            int k9 = 40+rand.nextInt(this.terrainMaxHeight-40);
            int j12 = z + rand.nextInt(16);
            (new WorldGenMinable(Block.oreCoalStone.blockID, 16, true)).generate(worldObj, rand, j6, k9, j12);
        }

        for (int j3 = 0; j3 < (20 * this.heightModifier); j3++) {
            int k6 = x + rand.nextInt(16);
            int l9 = 30 + rand.nextInt(this.terrainMaxHeight / 2-30);
            int k12 = z + rand.nextInt(16);
            (new WorldGenMinable(Block.oreIronStone.blockID, 8, true)).generate(worldObj, rand, k6, l9, k12);
        }
        for (int j3 = 0; j3 < (5 * this.heightModifier); j3++) {
            int k6 = x + rand.nextInt(16);
            int l9 = (rand.nextInt(30));
            int k12 = z + rand.nextInt(16);
            (new WorldGenMinable(Block.oreIronStone.blockID, 8, true)).generate(worldObj, rand, k6, l9, k12);
        }

        for (int k3 = 0; k3 < (2 * this.heightModifier); k3++) {
            int l6 = x + rand.nextInt(16);
            int i10 = rand.nextInt(this.terrainMaxHeight / 4);
            int l12 = z + rand.nextInt(16);
            (new WorldGenMinable(Block.oreGoldStone.blockID, 8, true)).generate(worldObj, rand, l6, i10, l12);
        }

        for (int l3 = 0; l3 < (8 * this.heightModifier); l3++) {
            int i7 = x + rand.nextInt(16);
            int j10 = rand.nextInt(this.terrainMaxHeight / 8);
            int i13 = z + rand.nextInt(16);
            (new WorldGenMinable(Block.oreRedstoneStone.blockID, 7, true)).generate(worldObj, rand, i7, j10, i13);
        }

        for (int i4 = 0; i4 < this.heightModifier/2; i4++) {
            int j7 = x + rand.nextInt(16);
            int k10 = rand.nextInt(this.terrainMaxHeight / 8);
            int j13 = z + rand.nextInt(16);
            (new WorldGenMinable(Block.oreDiamondStone.blockID, 7, true)).generate(worldObj, rand, j7, k10, j13);
        }

        for (int i4 = 0; i4 < this.heightModifier; i4++) {
            int j7 = x + rand.nextInt(16);
            int k10 = 40 + rand.nextInt(this.terrainMaxHeight / 2-40);
            int j13 = z + rand.nextInt(16);
            (new WorldGenMinable(Block.mossStone.blockID, 32, true)).generate(worldObj, rand, j7, k10, j13);
        }

        for (int j4 = 0; j4 < this.heightModifier; j4++) {
            int k7 = x + rand.nextInt(16);
            int l10 = rand.nextInt(this.terrainMaxHeight / 8) + rand.nextInt(this.terrainMaxHeight / 8);
            int k13 = z + rand.nextInt(16);
            (new WorldGenMinable(Block.oreLapisStone.blockID, 6, true)).generate(worldObj, rand, k7, l10, k13);
        }


        d = 0.5;
        int k4 = (int)((this.mobSpawnerNoise.func_806_a((double)x * d, (double)z * d) / 8.0 + this.rand.nextDouble() * 4.0 + 4.0) / 3.0);
        int treeDensity = 0;
        if (this.treeDensityOverride != -1) {
            treeDensity = this.treeDensityOverride;
        } else {
            if (this.rand.nextInt(10) == 0) {
                ++treeDensity;
            }

            if (biomegenbase == BiomeGenBase.forest) {
                treeDensity += k4 + 5;
            }

            if (biomegenbase == BiomeGenBase.rainforest) {
                treeDensity += k4 + 10;
            }

            if (biomegenbase == BiomeGenBase.seasonalForest) {
                treeDensity += k4 + 2;
            }

            if (biomegenbase == BiomeGenBase.taiga) {
                treeDensity += k4 + 5;
            }

            if (biomegenbase == BiomeGenBase.borealForest) {
                treeDensity += k4 + 3;
            }

            if (biomegenbase == BiomeGenBase.desert) {
                treeDensity += k4 + 1;
            }

            if (biomegenbase == BiomeGenBase.tundra) {
                treeDensity -= 20;
            }

            if (biomegenbase == BiomeGenBase.plains) {
                treeDensity -= 20;
            }

            if (biomegenbase == BiomeGenBase.swampland) {
                treeDensity += k4 + 4;
            }
        }

        if (treeDensityOverride != -1) treeDensity = treeDensityOverride;
        for (int i11 = 0; i11 < treeDensity; i11++) {
            int l13 = x + rand.nextInt(16) + 8;
            int j14 = z + rand.nextInt(16) + 8;
            WorldGenerator worldgenerator = biomegenbase.getRandomWorldGenForTrees(this.rand);
            worldgenerator.func_517_a(1.0D, 1.0D, 1.0D);
            worldgenerator.generate(worldObj, rand, l13, worldObj.getHeightValue(l13, j14), j14);
        }
        byte byteReeds = 0;
        if (biomegenbase == BiomeGenBase.rainforest) {
            byteReeds = 1;
        }
        for (int i11 = 0; i11 < byteReeds; i11++) {
            int i18 = x + rand.nextInt(16) + 8;
            int i23 = z + rand.nextInt(16) + 8;
            int i21 = worldObj.getHeightValue(i18, i23);
            (new WorldGenReedTall()).generate(worldObj, rand, i18, i21, i23);
        }

        byte byteMeadow = 0;
        if (biomegenbase == BiomeGenBase.seasonalForest) {
            byteMeadow = 1;
        }

        if (biomegenbase == BiomeGenBase.meadow) {
            byteMeadow = 2;
        }

        if (biomegenbase == BiomeGenBase.borealForest) {
            byteMeadow = 2;
        }

        if (biomegenbase == BiomeGenBase.shrubland) {
            byteMeadow = 1;
        }
        for (int l14 = 0; l14 < byteMeadow; l14++) {
            int blockId = Block.flowerYellow.blockID;
            if (rand.nextInt(3) != 0) {
                blockId = Block.flowerRed.blockID;
            }
            int l19 = x + rand.nextInt(16) + 8;
            int k22 = rand.nextInt(Minecraft.WORLD_HEIGHT_BLOCKS);
            int j24 = z + rand.nextInt(16) + 8;
            (new WorldGenTallGrass(blockId)).generate(worldObj, rand, l19, k22, j24);
        }

        byte byte0 = 0;
        if (biomegenbase == BiomeGenBase.forest) {
            byte0 = 2;
        }

        if (biomegenbase == BiomeGenBase.swampland) {
            byte0 = 2;
        }

        if (biomegenbase == BiomeGenBase.taiga) {
            byte0 = 2;
        }

        if (biomegenbase == BiomeGenBase.plains) {
            byte0 = 3;
        }
        for (int i14 = 0; i14 < byte0; i14++) {
            int k14 = x + rand.nextInt(16) + 8;
            int l16 = rand.nextInt(this.terrainMaxHeight);
            int k19 = z + rand.nextInt(16) + 8;
            (new WorldGenFlowers(Block.flowerYellow.blockID)).generate(worldObj, rand, k14, l16, k19);
        }

        byte byte1 = 0;
        if (biomegenbase == BiomeGenBase.forest) {
            byte1 = 2;
        }

        if (biomegenbase == BiomeGenBase.meadow) {
            byte1 = 2;
        }

        if (biomegenbase == BiomeGenBase.rainforest) {
            byte1 = 10;
        }

        if (biomegenbase == BiomeGenBase.desert) {
            byte1 = 5;
        }

        if (biomegenbase == BiomeGenBase.seasonalForest) {
            byte1 = 2;
        }

        if (biomegenbase == BiomeGenBase.taiga) {
            byte1 = 1;
        }

        if (biomegenbase == BiomeGenBase.borealForest) {
            byte1 = 5;
        }

        if (biomegenbase == BiomeGenBase.plains) {
            byte1 = 10;
        }

        if (biomegenbase == BiomeGenBase.swampland) {
            byte1 = 4;
        }

        if (biomegenbase == BiomeGenBase.shrubland) {
            byte1 = 2;
        }
        for (int l14 = 0; l14 < byte1; l14++) {
            int type = Block.tallgrass.blockID;
            if ((biomegenbase == BiomeGenBase.rainforest || biomegenbase == BiomeGenBase.swampland || biomegenbase == BiomeGenBase.borealForest || biomegenbase == BiomeGenBase.taiga) && this.rand.nextInt(3) != 0) {
                type = Block.tallgrassFern.blockID;
            }
            int l19 = x + rand.nextInt(16) + 8;
            int k22 = rand.nextInt(this.terrainMaxHeight);
            int j24 = z + rand.nextInt(16) + 8;
            (new WorldGenTallGrass(type)).generate(worldObj, rand, l19, k22, j24);
        }


        byte1 = 0;
        if (biomegenbase == BiomeGenBase.outback) {
            byte1 = 2;
        }
        for (int i15 = 0; i15 < byte1; i15++) {
            int i17 = x + rand.nextInt(16) + 8;
            int i20 = rand.nextInt(this.terrainMaxHeight);
            int l22 = z + rand.nextInt(16) + 8;
            (new WorldGenTallGrass(Block.spinifex.blockID)).generate(worldObj, rand, i17, i20, l22);
        }

        byte1 = 0;
        if (biomegenbase == BiomeGenBase.desert) {
            byte1 = 2;
        }
        for (int i15 = 0; i15 < byte1; i15++) {
            int i17 = x + rand.nextInt(16) + 8;
            int i20 = rand.nextInt(this.terrainMaxHeight);
            int l22 = z + rand.nextInt(16) + 8;
            (new WorldGenDeadBush(Block.deadbush.blockID)).generate(worldObj, rand, i17, i20, l22);
        }

        if (rand.nextInt(2) == 0) {
            int j15 = x + rand.nextInt(16) + 8;
            int j17 = rand.nextInt(this.terrainMaxHeight);
            int j20 = z + rand.nextInt(16) + 8;
            (new WorldGenFlowers(Block.flowerRed.blockID)).generate(worldObj, rand, j15, j17, j20);
        }
        if (rand.nextInt(4) == 0) {
            int k15 = x + rand.nextInt(16) + 8;
            int k17 = rand.nextInt(this.terrainMaxHeight);
            int k20 = z + rand.nextInt(16) + 8;
            (new WorldGenFlowers(Block.mushroomBrown.blockID)).generate(worldObj, rand, k15, k17, k20);
        }
        if (rand.nextInt(8) == 0) {
            int l15 = x + rand.nextInt(16) + 8;
            int l17 = rand.nextInt(this.terrainMaxHeight);
            int l20 = z + rand.nextInt(16) + 8;
            (new WorldGenFlowers(Block.mushroomRed.blockID)).generate(worldObj, rand, l15, l17, l20);
        }
        if(rand.nextInt(5) == 0) {
            int i18 = x + rand.nextInt(16) + 8;
            int i23 = z + rand.nextInt(16) + 8;
            int i21 = worldObj.getHeightValue(i18, i23);
            (new WorldGenReed()).generate(worldObj, rand, i18, i21, i23);
        }

        if(rand.nextInt(128) == 0)
        {
            int j16 = x + rand.nextInt(16) + 8;
            int j21 = z + rand.nextInt(16) + 8;
            int i22 = worldObj.getHeightValue(j16, j21);
            (new WorldGenPumpkin()).generate(worldObj, rand, j16, i22, j21);
        }
        if(rand.nextInt(64) == 0)
        {
            int j16 = x + rand.nextInt(16) + 8;
            int j21 = z + rand.nextInt(16) + 8;
            int i22 = worldObj.getHeightValue(j16, j21);
            (new WorldGenSponge()).generate(worldObj, rand, j16, i22, j21);
        }

        int k16 = 0;
        if(biomegenbase == BiomeGenBase.desert)
        {
            k16 += 10;
        }
        for(int k18 = 0; k18 < k16; k18++)
        {
            int k21 = x + rand.nextInt(16) + 8;
            int j23 = rand.nextInt(this.terrainMaxHeight);
            int k24 = z + rand.nextInt(16) + 8;
            (new WorldGenCactus()).generate(worldObj, rand, k21, j23, k24);
        }

        for(int l18 = 0; l18 < 50; l18++)
        {
            int l21 = x + rand.nextInt(16) + 8;
            int k23 =  rand.nextInt(rand.nextInt(this.terrainMaxHeight - (this.terrainMaxHeight / 16)) + (this.terrainMaxHeight / 16));
            int l24 = z + rand.nextInt(16) + 8;
            (new WorldGenLiquids(Block.fluidWaterFlowing.blockID)).generate(worldObj, rand, l21, k23, l24);
        }

        for(int i19 = 0; i19 < 20; i19++)
        {
            int i22 = x + rand.nextInt(16) + 8;
            int l23 = rand.nextInt(rand.nextInt(rand.nextInt(this.terrainMaxHeight - (this.terrainMaxHeight / 8)) + (this.terrainMaxHeight / 16)) + (this.terrainMaxHeight / 16));
            int i25 = z + rand.nextInt(16) + 8;
            (new WorldGenLiquids(Block.fluidLavaFlowing.blockID)).generate(worldObj, rand, i22, l23, i25);
        }

        this.generatedTemperatures = this.worldObj.getWorldChunkManager().getTemperatures(this.generatedTemperatures, x + 8, z + 8, 16, 16);

        for(int j19 = x + 8; j19 < x + 8 + 16; ++j19) {
            for(int j22 = z + 8; j22 < z + 8 + 16; ++j22) {
                if (this.worldObj.getCurrentWeather() != null) {
                    this.worldObj.getCurrentWeather().doEnvironmentGenerate(this.worldObj, j19, j22);
                }

                BiomeGenBase biome = this.worldObj.getWorldChunkManager().getBiomeGenAt(j19, j22);
                int k25 = this.worldObj.getHeightValue(j19, j22);
                if ((biome.hasSurfaceSnow() || this.worldObj.dimension.worldType == WorldType.overworldWinter) && k25 > 0 && k25 < Minecraft.WORLD_HEIGHT_BLOCKS && this.worldObj.isAirBlock(j19, k25, j22) && this.worldObj.getBlockMaterial(j19, k25 - 1, j22).getIsSolid()) {
                    this.worldObj.setBlockWithNotify(j19, k25, j22, Block.layerSnow.blockID);
                }

                if ((biome.hasSurfaceSnow() || this.worldObj.dimension.worldType == WorldType.overworldWinter) && (this.worldObj.getBlockId(j19, this.oceanHeight - 1, j22) == Block.fluidWaterStill.blockID || this.worldObj.getBlockId(j19, this.oceanHeight - 1, j22) == Block.fluidWaterFlowing.blockID)) {
                    this.worldObj.setBlockWithNotify(j19, this.oceanHeight - 1, j22, Block.ice.blockID);
                }
            }
        }

        BlockSand.fallInstantly = false;
        ci.cancel();
    }

    private void replaceBlocksForCaveBiome(Chunk chunk, short[] data, int x, int z, World world)
    {
        float[][] noFlowstoneNoise = uberUtil.getInterpolatedNoiseValue2D(uberUtil.sampleNoise2D(chunk.xPosition,chunk.zPosition,0.08f,world, noFlowstoneNoiseMap, FastNoiseLite.NoiseType.OpenSimplex2S));
        boolean placeFlowstone;
        for(int lx = 0; lx<16; lx++)
        {
            for(int lz = 0; lz<16; lz++)
            {
                placeFlowstone = noFlowstoneNoise[lx][lz]>-0.1f;
                for(int ly = Minecraft.WORLD_HEIGHT_BLOCKS-1; ly > 0; ly--)
                {
                    if(caveBiomeValues[lx][ly][lz]==1) {
                        //Spawners get replaced, chests get replaced, only rock and stone should be replaced, fine tune block placement, fix wierd water placement.
                        if (!worldObj.isAirBlock(x + lx, ly, z + lz) && data[lx << Minecraft.WORLD_HEIGHT_BITS + 4 | lz << Minecraft.WORLD_HEIGHT_BITS | ly] != Block.bedrock.blockID && Block.getBlock(data[lx << Minecraft.WORLD_HEIGHT_BITS + 4 | lz << Minecraft.WORLD_HEIGHT_BITS | ly]) instanceof BlockStone && placeFlowstone)
                        {
                            //worldObj.setBlock(x+lx,ly,z+lz,CaveUberhaul.flowstone.blockID);
                            data[lx << Minecraft.WORLD_HEIGHT_BITS + 4 | lz << Minecraft.WORLD_HEIGHT_BITS | ly] = (short) CaveUberhaul.flowstone.blockID;
                            //System.out.println("Flowstone placed at x: "+(x+lx)+ " y: "+ ly+ " z: "+(z+lz));
                        }
                        if(worldObj.getBlockId(lx+x,ly,lz+z) == CaveUberhaul.flowstone.blockID && rand.nextFloat()>=0.45f&&uberUtil.isSurroundedFreeAboveNoLava(x+lx,ly,z+lz,worldObj))
                        {
                            //worldObj.setBlock(x + lx, ly, z + lz, Block.fluidWaterStill.blockID);
                            if(worldObj.isAirBlock(lx+x,ly-1,lz+z))
                            {
                                chunk.setBlockID(lx,ly,lz,Block.fluidWaterFlowing.blockID);
                            }
                            else {
                                data[lx << Minecraft.WORLD_HEIGHT_BITS + 4 | lz << Minecraft.WORLD_HEIGHT_BITS | ly] = (short) Block.fluidWaterStill.blockID;
                            }
                        }
                    }
                }
            }
        }
        chunk.blocks = data;
    }

}
