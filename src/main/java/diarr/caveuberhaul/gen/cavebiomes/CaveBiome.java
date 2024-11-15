package diarr.caveuberhaul.gen.cavebiomes;

import net.minecraft.core.block.Block;
import net.minecraft.core.entity.SpawnListEntry;

import java.util.List;

public class CaveBiome {
    public Block[] blockList;
    public final int id;
    public final double minTemp;
    public final double maxTemp;
    public final double minWeird;
    public final double maxWeird;
    public final double minHeight;
    public final double maxHeight;

    public CaveBiome(int id,double minT,double maxT,double minW, double maxW,double minH,double maxH)
    {
        this.id = id;
        minTemp = minT;
        maxTemp = maxT;
        minWeird = minW;
        maxWeird = maxW;
        minHeight = minH;
        maxHeight = maxH;
    }
}
