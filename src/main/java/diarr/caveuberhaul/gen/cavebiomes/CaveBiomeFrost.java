package diarr.caveuberhaul.gen.cavebiomes;

import net.minecraft.core.block.Block;

public class CaveBiomeFrost extends CaveBiome {
    public CaveBiomeFrost(int id,double minT,double maxT,double minW, double maxW,int bpc,int spc){
        super(id,minT,maxT,minW,maxW,bpc,spc);
        this.blockList = new Block[]{Block.permafrost,Block.permaice,Block.ice,Block.blockSnow};
    }
}
