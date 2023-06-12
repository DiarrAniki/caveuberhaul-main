package diarr.caveuberhaul;

import net.fabricmc.api.ModInitializer;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.Material;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.BlockHelper;
import turniplabs.halplibe.helper.ItemHelper;
import turniplabs.halplibe.helper.TextureHelper;


public class CaveUberhaul implements ModInitializer {
    public static final String MOD_ID = "caveuberhaul";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static String name(String name) {
        return CaveUberhaul.MOD_ID + "." + name;
    }

    public static final Item flowstoneStalagtiteItem = ItemHelper.createItem(MOD_ID, new ItemFlowstoneItem(141), "flowstoneStalagtiteItem", "stalagtite1.png");

    public static final Block flowstone = BlockHelper.createBlock(MOD_ID, new Block(900, Material.rock), "flowstone", "flowstone.png", Block.soundStoneFootstep, 1.2f, 8f, 0.0f);
    public static final Block flowstonePillar = BlockHelper.createBlock(MOD_ID, new Block(901, Material.rock), "flowstonePillar", "flowstonePillar.png", Block.soundStoneFootstep, 1.2f, 8f, 0.0f);

    public static final Block flowstoneStalagtite = BlockHelper.createBlock(MOD_ID, new BlockStalagtite(902, Material.rock), "flowstoneStalagtite", "stalagtite1.png", Block.soundStoneFootstep, 1f, 5f, 0.0f);

    public static final int stalagtite1[] =TextureHelper.registerBlockTexture(MOD_ID, "stalagtite1.png");
    public static final int stalagtite2[] =TextureHelper.registerBlockTexture(MOD_ID, "stalagtite2.png");
    @Override
    public void onInitialize() {
        LOGGER.info("CaveUberhaul initialized.");
    }
}
