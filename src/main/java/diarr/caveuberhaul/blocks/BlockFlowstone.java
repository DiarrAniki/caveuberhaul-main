package diarr.caveuberhaul.blocks;

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
        if(Block.getBlock(world.getBlockId(i,j-1,k))instanceof BlockStalagtite && !world.isAirBlock(i,j+1,k)&&(world.getBlockId(i,j+2,k) == Block.fluidWaterStill.id||world.getBlockId(i,j+2,k) == Block.fluidWaterFlowing.id)) {
            world.scheduleBlockUpdate(i, j, k, this.id, this.tickRate());
            ((BlockStalagtite) Block.getBlock(world.getBlockId(i,j-1,k))).checkForGrowthConditionAndPropagate(world,i,j-1,k);
        }
    }

    public void updateTick(World world, int x, int y, int z, Random rand){
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
    }
}
