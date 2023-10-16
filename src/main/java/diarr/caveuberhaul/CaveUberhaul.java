package diarr.caveuberhaul;

import diarr.caveuberhaul.blocks.BlockFlowstone;
import diarr.caveuberhaul.blocks.BlockStalagmite;
import diarr.caveuberhaul.blocks.BlockStalagtite;
import diarr.caveuberhaul.items.ItemFlowstoneItem;
import diarr.caveuberhaul.particles.EntityDripFx;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.block.model.BlockModelRenderBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.fx.EntityFX;
import net.minecraft.core.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.ItemHelper;
import turniplabs.halplibe.helper.ParticleHelper;
import turniplabs.halplibe.util.ConfigHandler;
import java.util.Properties;


public class CaveUberhaul implements ModInitializer {

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
        prop.setProperty("BlockStartId","1000");
        config = new ConfigHandler(MOD_ID,prop);
    }

    public static final Item flowstoneStalagtiteItem = ItemHelper.createItem(MOD_ID, new ItemFlowstoneItem(config.getInt("ItemStartId")+1), "flowstone.item", "stalagtite1.png");

    public static final Block flowstone = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures("flowstone.png")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE)
            .setTickOnLoad()
            .build(new BlockFlowstone("flowstone",config.getInt("BlockStartId"),Material.stone));
    public static final Block flowstonePillar = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures("flowstonePillar.png")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE)
            .setTickOnLoad()
            .build(new BlockFlowstone("flowstone.Pillar",config.getInt("BlockStartId")+1,Material.stone));
    public static final Block flowstoneStalagtite1 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures("st1.png")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(new BlockModelRenderBlocks(1))
            .setTickOnLoad()
            .build(new BlockStalagtite("cu.flowstone.st1",config.getInt("BlockStartId")+2,Material.stone,0));
    public static final Block flowstoneStalagtite2 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures("st2.png")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(new BlockModelRenderBlocks(1))
            .setTickOnLoad()
            .build(new BlockStalagtite("cu.flowstone.st2",config.getInt("BlockStartId")+3,Material.stone,1));
    public static final Block flowstoneStalagtite3 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures("st3.png")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(new BlockModelRenderBlocks(1))
            .setTickOnLoad()
            .build(new BlockStalagtite("cu.flowstone.st3",config.getInt("BlockStartId")+4,Material.stone,2));
    public static final Block flowstoneStalagtite4 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures("st4.png")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(new BlockModelRenderBlocks(1))
            .setTickOnLoad()
            .build(new BlockStalagtite("cu.flowstone.st4",config.getInt("BlockStartId")+5,Material.stone,3));
    public static final Block flowstoneStalagtiteConnected = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures("st1c.png")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(new BlockModelRenderBlocks(1))
            .setTickOnLoad()
            .build(new BlockStalagtite("cu.flowstone.st1c",config.getInt("BlockStartId")+6,Material.stone,0));

    public static final Block flowstoneStalagmite1 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures("sm1.png")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(new BlockModelRenderBlocks(1))
            .setTickOnLoad()
            .build(new BlockStalagmite("cu.flowstone.sm1",config.getInt("BlockStartId")+7,Material.stone,0));
    public static final Block flowstoneStalagmite2 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures("sm2.png")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(new BlockModelRenderBlocks(1))
            .setTickOnLoad()
            .build(new BlockStalagmite("cu.flowstone.sm2",config.getInt("BlockStartId")+8,Material.stone,1));
    public static final Block flowstoneStalagmite3 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures("sm3.png")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(new BlockModelRenderBlocks(1))
            .setTickOnLoad()
            .build(new BlockStalagmite("cu.flowstone.sm3",config.getInt("BlockStartId")+9,Material.stone,2));
    public static final Block flowstoneStalagmite4 = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures("sm4.png")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(new BlockModelRenderBlocks(1))
            .setTickOnLoad()
            .build(new BlockStalagmite("cu.flowstone.sm4",config.getInt("BlockStartId")+10,Material.stone,3));
    public static final Block flowstoneStalagmiteConnected = new BlockBuilder(MOD_ID)
            .setHardness(1.2f)
            .setResistance(8f)
            .setTextures("sm1c.png")
            .setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.NOT_IN_CREATIVE_MENU)
            .setBlockModel(new BlockModelRenderBlocks(1))
            .setTickOnLoad()
            .build(new BlockStalagmite("cu.flowstone.sm1c",config.getInt("BlockStartId")+11,Material.stone,0));

    @Override
    public void onInitialize() {
        LOGGER.info("Duct Tape applied, CaveUberhaul initialized.");
    }
}
