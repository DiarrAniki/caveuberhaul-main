package diarr.caveuberhaul.gen.cavebiomes;

import diarr.caveuberhaul.CaveUberhaul;
import net.minecraft.core.block.Block;

public class CaveBiomeFlowstone extends CaveBiome {
    public CaveBiomeFlowstone(int id,double minT,double maxT,double minW, double maxW,int bpc,int spc){
        super(id,minT,maxT,minW,maxW,bpc,spc);
        this.blockList = new Block[]{CaveUberhaul.flowstone, CaveUberhaul.flowstonePillar};
    }
}
