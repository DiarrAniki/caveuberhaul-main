package diarr.caveuberhaul.gen.cavebiomes;

import net.minecraft.core.block.Block;

public class CaveBiomeJungle extends  CaveBiome{
    public CaveBiomeJungle(int id, double minT, double maxT, double minW, double maxW, double minH, double maxH) {
        super(id, minT, maxT, minW, maxW, minH, maxH);
        this.blockList = new Block[]{Block.dirt,Block.mossStone,Block.mud,Block.blockClay};
    }
}
