package diarr.caveuberhaul.blocks;

import diarr.caveuberhaul.Profiler;
import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.world.World;

import java.util.Random;

public class BlockFlowstone extends Block {
    public BlockFlowstone(String s, int i, Material material) {
        super(s,i, material);
        this.setTickOnLoad(true);
    }

    public int tickRate() {
        //return 12000; //10 min
        return 1500;
    }


    public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
        Block b = Block.getBlock(world.getBlockId(i,j-1,k));
        if(b instanceof BlockStalagtite && !world.isAirBlock(i,j+1,k)&&(world.getBlockId(i,j+2,k) == Block.fluidWaterStill.id||world.getBlockId(i,j+2,k) == Block.fluidWaterFlowing.id)) {
            world.scheduleBlockUpdate(i, j, k, this.id, this.tickRate());
            //if(world.getBlockMetadata(i,j-1,k)==0&&b instanceof BlockStalagtite) {
                //((BlockStalagtite) b).checkForGrowthConditionAndPropagate(world, i, j - 1, k);
           // }
        }
    }

    public void updateTick(World world, int x, int y, int z, Random rand){
        Profiler.methodStart("FlowstoneUpdate");
        if (world.getBlockId(x, y + 1, z) == Block.gravel.id)
        {
            world.setBlockWithNotify(x,y+1,z,Block.sand.id);
            world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
        }
        else if (world.getBlockId(x, y + 1, z) == Block.sand.id)
        {
            world.setBlockWithNotify(x,y+1,z,Block.mud.id);
            world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
        }
        else if (world.getBlockId(x, y + 1, z) == Block.mud.id) {
            world.setBlockWithNotify(x,y+1,z,Block.blockClay.id);
        }
        Profiler.methodEnd("FlowstoneUpdate");
    }
}
