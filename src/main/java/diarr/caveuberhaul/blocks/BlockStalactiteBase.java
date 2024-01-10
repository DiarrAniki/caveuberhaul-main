package diarr.caveuberhaul.blocks;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;

public abstract class BlockStalactiteBase extends Block {

    protected int state;
    public BlockStalactiteBase(String s, int i, Material material, int state) {
        super(s,i, material);
        this.state = state;
    }
    public int getConnectedState()
    {
        return state;
    }
    public abstract void doConnectLogic(World world, int x, int y, int z);
    public void doFall(World world, int i,int j,int k){}

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    @Override
    public boolean isSolidRender() {
        return false;
    }

}
