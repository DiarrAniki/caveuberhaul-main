package diarr.caveuberhaul.gen.cavebiomes;

import net.minecraft.core.block.Block;
import net.minecraft.core.entity.SpawnListEntry;

import java.util.List;

public class CaveBiome {
    public Block[] blockList;
    public final double minTemp;
    public final double maxTemp;
    public final double minWeird;
    public final double maxWeird;
    public final int bigPillarChance;
    public final int smallPillarChance;
    protected List<SpawnListEntry> spawnableMonsterList;
    protected List<SpawnListEntry> spawnableWaterCreatureList;
    protected List<SpawnListEntry> spawnableAmbientCreatureList;

    public CaveBiome(double minT,double maxT,double minW, double maxW,int bpc,int spc)
    {
        minTemp = minT;
        maxTemp = maxT;
        minWeird = minW;
        maxWeird = maxW;
        bigPillarChance = bpc;
        smallPillarChance = spc;
    }
}
