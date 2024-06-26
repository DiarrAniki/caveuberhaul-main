package diarr.caveuberhaul;

import diarr.caveuberhaul.blocks.BlockFlowstone;
import diarr.caveuberhaul.blocks.BlockIcicle;
import diarr.caveuberhaul.blocks.BlockStalagmite;
import diarr.caveuberhaul.blocks.BlockStalagtite;
import diarr.caveuberhaul.blocks.EntityFallingIcicle;
import diarr.caveuberhaul.blocks.EntityFallingStalactite;
import diarr.caveuberhaul.blocks.RenderFallingIcicle;
import diarr.caveuberhaul.blocks.RenderFallingStalactite;
import diarr.caveuberhaul.items.ItemFlowstoneItem;
import diarr.caveuberhaul.particles.EntityDripFx;
import diarr.caveuberhaul.particles.EntityVoidFogFX;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.entity.fx.EntityFX;
import net.minecraft.client.entity.fx.ParticleLambda;
import net.minecraft.client.render.block.model.BlockModelCrossedSquares;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.item.Item;
import net.minecraft.core.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.ParticleHelper;
import turniplabs.halplibe.util.ConfigHandler;
import turniplabs.halplibe.util.GameStartEntrypoint;

import java.util.Properties;


public class CaveUberhaul implements ModInitializer, GameStartEntrypoint {

    public static ConfigHandler config;
    static {
        // this is here to possibly fix some class loading issues but might not work anyway, delete if it causes even more problems
        try {
            Class.forName("net.minecraft.core.block.Block");
            Class.forName("net.minecraft.core.item.Item");
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static final String MOD_ID = "caveuberhaul";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static String name(String name) {
        return CaveUberhaul.MOD_ID + "." + name;
    }

    static{
        Properties prop = new Properties();
        prop.setProperty("ItemStartId","20000");
        prop.setProperty("BlockStartId","2000");
        prop.setProperty("Additional_Old_Caves","true");
        config = new ConfigHandler(MOD_ID,prop);
    }
    public static final Item flowstoneStalagtiteItem = new ItemBuilder(MOD_ID)
    .setIcon(MOD_ID + ":block/stalagtite1")
    .build(new ItemFlowstoneItem("flowstone", config.getInt("ItemStartId")+1));

    public static final Block flowstone = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures(MOD_ID + ":block/flowstone")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE)
            .setTickOnLoad()
            .build(new BlockFlowstone("flowstone",config.getInt("BlockStartId"),Material.stone));
    public static final Block flowstonePillar = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures(MOD_ID + ":block/flowstonePillar")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE)
            .setTickOnLoad()
            .build(new BlockFlowstone("flowstone.Pillar",config.getInt("BlockStartId")+1,Material.stone));
    public static final Block flowstoneStalagtite1 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures(MOD_ID + ":block/st1")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockStalagtite("cu.flowstone.st1",config.getInt("BlockStartId")+2, 0));
    public static final Block flowstoneStalagtite2 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures(MOD_ID + ":block/st2")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockStalagtite("cu.flowstone.st2",config.getInt("BlockStartId")+3, 1));
    public static final Block flowstoneStalagtite3 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures(MOD_ID + ":block/st3")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockStalagtite("cu.flowstone.st3",config.getInt("BlockStartId")+4, 2));
    public static final Block flowstoneStalagtite4 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures(MOD_ID + ":block/st4")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockStalagtite("cu.flowstone.st4",config.getInt("BlockStartId")+5, 3));
    public static final Block flowstoneStalagtiteConnected = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures(MOD_ID + ":block/st1c")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockStalagtite("cu.flowstone.st1c",config.getInt("BlockStartId")+6, 0));

    public static final Block flowstoneStalagmite1 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures(MOD_ID + ":block/sm1")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockStalagmite("cu.flowstone.sm1",config.getInt("BlockStartId")+7,0));
    public static final Block flowstoneStalagmite2 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures(MOD_ID + ":block/sm2")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockStalagmite("cu.flowstone.sm2",config.getInt("BlockStartId")+8,1));
    public static final Block flowstoneStalagmite3 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures(MOD_ID + ":block/sm3")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockStalagmite("cu.flowstone.sm3",config.getInt("BlockStartId")+9,2));
    public static final Block flowstoneStalagmite4 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures(MOD_ID + ":block/sm4")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockStalagmite("cu.flowstone.sm4",config.getInt("BlockStartId")+10,3));
    public static final Block flowstoneStalagmiteConnected = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures(MOD_ID + ":block/sm1c")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockStalagmite("cu.flowstone.sm1c",config.getInt("BlockStartId")+11,0));

    public static final Block icicle1 = new BlockBuilder(MOD_ID)
            .setHardness(0.8f)
            .setResistance(4f)
            .setTextures(MOD_ID + ":block/ist1")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockIcicle("cu.icicle1",config.getInt("BlockStartId")+12,Material.ice,0));
    public static final Block icicle2 = new BlockBuilder(MOD_ID)
            .setHardness(0.8f)
            .setResistance(4f)
            .setTextures(MOD_ID + ":block/ist2")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockIcicle("cu.icicle2",config.getInt("BlockStartId")+13,Material.ice,1));
    public static final Block icicle3 = new BlockBuilder(MOD_ID)
            .setHardness(0.8f)
            .setResistance(4f)
            .setTextures(MOD_ID + ":block/ist3")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockIcicle("cu.icicle3",config.getInt("BlockStartId")+14,Material.ice,2));
    public static final Block icicle4 = new BlockBuilder(MOD_ID)
            .setHardness(0.8f)
            .setResistance(4f)
            .setTextures(MOD_ID + ":block/ist4")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(BlockModelCrossedSquares::new)
            .setTickOnLoad()
            .build(new BlockIcicle("cu.icicle4",config.getInt("BlockStartId")+15,Material.ice,3));

    @Override
    public void onInitialize() {
        LOGGER.info("Duct Tape applied, CaveUberhaul initialized.");
    }

    @Override
    public void beforeGameStart() {
        ParticleHelper.createParticle("drip", (world, x, y, z, motionX, motionY, motionZ, data) -> new EntityDripFx(world, x, y, z, motionX, motionY, motionZ));
        ParticleHelper.createParticle("voidFog", (world, x, y, z, motionX, motionY, motionZ, data) -> new EntityVoidFogFX(world, x, y, z, motionX, motionY, motionZ));

        EntityHelper.createEntity(EntityFallingStalactite.class, 468, "caveuberhaul$falling_stalactite", RenderFallingStalactite::new);
        EntityHelper.createEntity(EntityFallingIcicle.class, 469, "caveuberhaul$falling_icicle", RenderFallingIcicle::new);
    }

    @Override
    public void afterGameStart() {

    }
}
