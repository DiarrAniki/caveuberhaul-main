package diarr.caveuberhaul.gen.cavebiomes;

import java.util.ArrayList;
import java.util.List;

public class CaveBiomes {

    public static List<CaveBiome>caveBiomeList;
    public static final CaveBiome CAVE_FLOWSTONE;

    static {
        caveBiomeList = new ArrayList<>();
        //TODO adjust values so it doesnt look doodoo
        CAVE_FLOWSTONE = new CaveBiomeFlowstone(0.0,1.0,-1.0,-0.6,2,100);
        caveBiomeList.add(CAVE_FLOWSTONE);
    }
}
