package diarr.caveuberhaul.gen.cavebiomes;

import java.util.ArrayList;
import java.util.List;

public class CaveBiomes {

    public static List<CaveBiome>caveBiomeList;
    public static final CaveBiome CAVE_FLOWSTONE;
    public static final CaveBiome CAVE_FROST;

    static {
        caveBiomeList = new ArrayList<>();
        //TODO adjust values so it doesnt look doodoo
        CAVE_FLOWSTONE = new CaveBiomeFlowstone(1,0.0,0.2,0.2,0.3,2,120);
        CAVE_FROST = new CaveBiomeFrost(2,0.0,0.0,0.0,0.0,4,110);
        caveBiomeList.add(CAVE_FLOWSTONE);
    }
}
