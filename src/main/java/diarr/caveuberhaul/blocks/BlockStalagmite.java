package diarr.caveuberhaul.blocks;

import diarr.caveuberhaul.CaveUberhaul;
import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

import java.util.Random;

public class BlockStalagmite extends BlockConnectable {
    public BlockStalagmite(String s, int i, int state) {
        super(s,i, Material.stone, state);
        this.setTickOnLoad(true);
        switch (state) {
            case 0: {
                this.setBlockBounds(0.325F, 0F, 0.325F, 0.675F, 0.5F, 0.675F);
                break;
            }
            case 1: {
                this.setBlockBounds(0.225F, 0.0F, 0.225F, 0.725F, 1F, 0.725F);
                break;
            }
            case 2: {
                this.setBlockBounds(0.15F, 0.0F, 0.15F, 0.85F, 1F, 0.85F);
                break;
            }
            case 3: {
                this.setBlockBounds(0f, 0.0F, 0F, 1F, 1F, 1F);
                break;
            }
        }
    }

    public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
        return dropCause != EnumDropCause.IMPROPER_TOOL ? new ItemStack[]{new ItemStack(CaveUberhaul.flowstoneStalagtiteItem.id,1,0)} : null;
    }
    public int tickRate() {
        return 256;
    }

    public void updateTick(World world, int i, int j, int k, Random random) {
        if (this.state == 0) {
            int length = 0;
            while (world.getBlock(i, j - length, k) instanceof BlockStalagmite) {
                length++;
            }
            if (length <= 7) {
                if (world.isAirBlock(i, j + 1, k)) {
                    if (random.nextInt(2048) == 1) {
                        world.setBlockWithNotify(i, j + 1, k, CaveUberhaul.flowstoneStalagmite1.id);
                    } else {
                        world.scheduleBlockUpdate(i, j, k, this.id, this.tickRate());
                    }
                }
            }
        }
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        this.doConnectLogic(world,x,y,z);
        world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
        if(world.isAirBlock(i,j-1,k))
        {
            while(Block.getBlock(world.getBlockId(i,j,k))instanceof BlockStalagmite)
            {
                world.setBlockWithNotify(i, j, k, 0);
                this.dropBlockWithCause(world,EnumDropCause.WORLD,i,j,k,0,null);
                j++;
            }
        }
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
        if(Block.getBlock(world.getBlockId(x,y-1,z)) instanceof BlockStalagmite)
        {
            this.doConnectLogic(world,x,y-1,z);
        }
    }
    public void doConnectLogic(World world, int x, int y, int z)
    {
        int connectState;

        if (!(Block.getBlock(world.getBlockId(x, y - 1, z)) instanceof BlockConnectable)&&Block.getBlock(world.getBlockId(x, y + 1, z)) instanceof BlockStalagmite&&((BlockStalagmite) Block.getBlock(world.getBlockId(x, y + 1, z))).getConnectedState()==2) {
            connectState = 3;
        }
        else if (Block.getBlock(world.getBlockId(x, y + 1, z)) instanceof BlockStalagmite) {
            connectState = UberUtil.clamp(((BlockStalagmite) Block.getBlock(world.getBlockId(x, y + 1, z))).getConnectedState() + 1,0,2 );
        }
        else
        {
            connectState = 0;
        }
        if(Block.getBlock(world.getBlockId(x, y - 1, z)) instanceof BlockStalagmite)
        {
            ((BlockStalagmite) Block.getBlock(world.getBlockId(x, y - 1, z))).doConnectLogic(world, x, y - 1, z);
        }

        //System.out.println(connectState);
        switch (connectState) {
            case 0: {
                if (Block.getBlock(world.getBlockId(x, y + 1, z)) instanceof BlockStalagtite) {
                    world.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagmiteConnected.id);
                    world.setBlockWithNotify(x, y+1, z, CaveUberhaul.flowstoneStalagtiteConnected.id);
                }
                else {
                    world.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagmite1.id);
                }
                break;
            }
            case 1: {
                world.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagmite2.id);
                break;
            }
            case 2: {
                world.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagmite3.id);
                break;
            }
            case 3: {
                world.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagmite4.id);
                break;
            }
        }
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k) {
        int l = world.getBlockId(i, j, k);
        Block u = Block.getBlock(world.getBlockId(i, j-1, k));
        return l == 0 && (u == CaveUberhaul.flowstone || u == CaveUberhaul.flowstonePillar || u instanceof BlockStalagmite);
    }
}
