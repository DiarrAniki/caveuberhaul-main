package diarr.caveuberhaul.gen.cavebiomes;

import net.minecraft.core.block.Block;

public class CaveBiomeFrost extends CaveBiome {
    public CaveBiomeFrost(int id,double minT,double maxT,double minW, double maxW,double minH,double maxH){
        super(id,minT,maxT,minW,maxW,minH,maxH);
        this.blockList = new Block[]{Block.permafrost,Block.permaice,Block.ice,Block.blockSnow};
    }
}
