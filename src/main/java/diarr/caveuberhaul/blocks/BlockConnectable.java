package diarr.caveuberhaul.blocks;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;

public abstract class BlockConnectable extends Block {

    protected int state;
    public BlockConnectable(String s, int i, Material material, int state) {
        super(s,i, material);
        this.state = state;
    }
    public int getConnectedState()
    {
        return state;
    }
    public abstract void doConnectLogic(World world, int x, int y, int z);

    public int getRenderBlockPass() {
        return 1;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

}
