package diarr.caveuberhaul.blocks;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;

import java.util.Random;

public class BlockFlowstone extends Block {
    public BlockFlowstone(String s, int i, Material material) {
        super(s,i, material);
        this.setTicking(true);
    }

    public int tickRate() {
        //return 12000; //10 min
        return 1500;
    }


    public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
        world.scheduleBlockUpdate(i, j, k, this.id, this.tickRate());
    }

    public void updateTick(World world, int x, int y, int z, Random rand){
        boolean flag = world.scheduledUpdatesAreImmediate;
        world.scheduledUpdatesAreImmediate = false;
        Block b = Block.getBlock(world.getBlockId(x,y-1,z));
        if(b instanceof BlockStalagtite && !world.isAirBlock(x,y+1,z)&&(world.getBlockId(x,y+2,z) == Block.fluidWaterStill.id||world.getBlockId(x,y+2,z) == Block.fluidWaterFlowing.id)) {
            if (world.getBlockId(x, y + 1, z) == Block.gravel.id) {
                world.setBlockWithNotify(x, y + 1, z, Block.sand.id);
                world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
            } else if (world.getBlockId(x, y + 1, z) == Block.sand.id) {
                world.setBlockWithNotify(x, y + 1, z, Block.mud.id);
                world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
            } else if (world.getBlockId(x, y + 1, z) == Block.mud.id) {
                world.setBlockWithNotify(x, y + 1, z, Block.blockClay.id);
            }
        }
        world.scheduledUpdatesAreImmediate = flag;
    }
}
