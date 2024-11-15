package diarr.caveuberhaul.gen.cavebiomes;

import java.util.ArrayList;
import java.util.List;

public class CaveBiomes {

    public static List<CaveBiome>caveBiomeList;
    public static final CaveBiome CAVE_FLOWSTONE;
    public static final CaveBiome CAVE_FROST;
    public static final CaveBiome CAVE_JUNGLE;
    public static final CaveBiome CAVE_MAGMA;


    static {
        caveBiomeList = new ArrayList<>();
        CAVE_FLOWSTONE = new CaveBiomeFlowstone(1,0.0,0.2,0.2,0.3,0,1);
        CAVE_FROST = new CaveBiomeFrost(2,0.0,0.0,0.0,0.0,0.25,1);
        CAVE_JUNGLE = new CaveBiomeFrost(3,0.5,1,-1,-0.4,0.25,1);
        CAVE_MAGMA = new CaveBiomeMagma(4,0.5,1,0.0,0.4,0,0.25);
        caveBiomeList.add(CAVE_FLOWSTONE);
        caveBiomeList.add(CAVE_JUNGLE);
        caveBiomeList.add(CAVE_MAGMA);
    }
}
