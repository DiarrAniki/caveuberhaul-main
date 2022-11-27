package diarr.caveuberhaul;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CaveUberhaul implements ModInitializer {
    public static final String MOD_ID = "examplemod";

    // Ideally use this whenever you need to print text like so,
    //
    //      CaveUberhaul.LOGGER.info("some kind of error");
    //
    // The difference between using `System.out.println()` is that your message
    // will be appended with your mod's id, in this case:
    //
    //      (examplemod) some kind of error
    //
    // This makes it easier for other modders to know which mod causes what.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Use your mod's id in the translation keys to prevent conflicts with other mods.
    // Example:
    //
    //      new Item().setName(CaveUberhaul.MOD_ID + ".example.item");
    //
    // Will result in this in the lang file:
    //
    //      "item.examplemod.example.item"
    //
    // Or you can use a helper method:
    public static String name(String name) {
        return CaveUberhaul.MOD_ID + "." + name;
    }


    @Override
    public void onInitialize() {
        LOGGER.info("CaveUberhaul initialized.");
    }
}
