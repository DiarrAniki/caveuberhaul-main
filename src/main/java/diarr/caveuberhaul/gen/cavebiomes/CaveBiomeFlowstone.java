package diarr.caveuberhaul.gen.cavebiomes;

import diarr.caveuberhaul.CaveUberhaul;
import net.minecraft.core.block.Block;

public class CaveBiomeFlowstone extends CaveBiome {
    public CaveBiomeFlowstone(int id,double minT,double maxT,double minW, double maxW,double minH,double maxH){
        super(id,minT,maxT,minW,maxW,minH,maxH);
        this.blockList = new Block[]{CaveUberhaul.flowstone, CaveUberhaul.flowstonePillar};
    }
}
