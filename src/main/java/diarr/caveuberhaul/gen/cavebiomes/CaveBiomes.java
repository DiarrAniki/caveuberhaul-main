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
        CAVE_FLOWSTONE = new CaveBiomeFlowstone(1,0.0,1.0,-1.0,-0.5,2,100);
        CAVE_FROST = new CaveBiomeFrost(2,0.0,0.0,0.0,0.0,4,200);
        caveBiomeList.add(CAVE_FLOWSTONE);
    }
}
