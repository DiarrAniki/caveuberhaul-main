package diarr.caveuberhaul.blocks;

import diarr.caveuberhaul.CaveUberhaul;
import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.world.World;

public class BlockIcicle extends BlockStalactiteBase {
    public BlockIcicle(String s, int i, Material material, int state) {
        super(s, i, material, state);

        switch (state) {
            case 0: {
                this.setBlockBounds(0.325F, 0.5F, 0.325F, 0.675F, 1F, 0.675F);
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
        this.setTicking(true);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        //((BlockStalagtite) Block.getBlock(world.getBlockId(x,y,z))).doConnectLogic(world,x,y,z);
        this.doConnectLogic(world,x,y,z);
        //this.checkForGrowthConditionAndPropagate(world,x,y,z);
        //world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
    }
    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
            tryToFall(world,i,j,k);
    }

    private void tryToFall(World world, int x, int y, int z) {
        Block block = Block.getBlock(world.getBlockId(x, y+1, z));
        if (block != Block.ice && block != Block.permaice && block != Block.permafrost && block != Block.blockSnow && !(block instanceof BlockIcicle)) {
                doFall(world,x,y,z);
        }
    }

    @Override
    public void doFall(World world, int x,int y,int z)
    {
        world.playBlockSoundEffect(null, x,y,z,Block.ice, EnumBlockSoundEffectType.MINE);
        while(Block.getBlock(world.getBlockId(x,y,z))instanceof BlockIcicle)
        {
            EntityFallingIcicle entityfallingIcicle = new EntityFallingIcicle(world, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), this.id);
            world.entityJoinedWorld(entityfallingIcicle);
            world.setBlockWithNotify(x, y, z, 0);
            y--;
        }
    }

    public static boolean canFallBelow(World world, int x, int y, int z) {
        int l = world.getBlockId(x, y, z);
        if (l == 0||Block.getBlock(l)instanceof BlockIcicle) {
            return true;
        } else if (l == Block.fire.id) {
            return true;
        } else {
            Material material = Block.blocksList[l].blockMaterial;
            if (material == Material.water) {
                return true;
            } else {
                return material == Material.lava;
            }
        }
    }
    @Override
    public void onBlockRemoved(World world, int x, int y, int z, int data) {
        if(Block.getBlock(world.getBlockId(x,y+1,z)) instanceof BlockIcicle)
        {
            this.doConnectLogic(world,x,y+1,z);
        }
    }

    @Override
    public void doConnectLogic(World world, int x, int y, int z) {
        int connectState;

        if (!(Block.getBlock(world.getBlockId(x, y + 1, z)) instanceof BlockIcicle)&&Block.getBlock(world.getBlockId(x, y - 1, z)) instanceof BlockIcicle&&((BlockIcicle) Block.getBlock(world.getBlockId(x, y - 1, z))).getConnectedState()==2) {
            //world.setBlockMetadata(x,y,z,3);
            connectState = 3;
        }
        else if (Block.getBlock(world.getBlockId(x, y - 1, z)) instanceof BlockIcicle) {
            connectState = UberUtil.clamp(((BlockIcicle) Block.getBlock(world.getBlockId(x, y - 1, z))).getConnectedState() + 1, 0, 2);
            //world.setBlockMetadata(x,y,z, world.getBlockMetadata(x,y-1,z)+1);
        }
        else
        {
            connectState = 0;
            //world.setBlockMetadata(x,y,z,0);
        }
        if(Block.getBlock(world.getBlockId(x, y + 1, z)) instanceof BlockIcicle)
        {
            ((BlockIcicle) Block.getBlock(world.getBlockId(x, y + 1, z))).doConnectLogic(world, x, y + 1, z);
        }


        //System.out.println(connectState);
        switch (connectState) {
            case 0: {
                /*if (Block.getBlock(world.getBlockId(x, y - 1, z)) instanceof BlockIcicle) {
                    world.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagtiteConnected.id);
                    world.setBlockWithNotify(x, y-1, z, CaveUberhaul.flowstoneStalagmiteConnected.id);
                }
                else {
                    world.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagtite1.id);
                }*/
                world.setBlockWithNotify(x, y, z, CaveUberhaul.icicle1.id);
                break;
            }
            case 1: {
                world.setBlockWithNotify(x, y, z, CaveUberhaul.icicle2.id);
                break;
            }
            case 2: {
                world.setBlockWithNotify(x, y, z, CaveUberhaul.icicle3.id);
                break;
            }
            case 3: {
                world.setBlockWithNotify(x, y, z, CaveUberhaul.icicle4.id);
                break;
            }
        }
    }
    @Override
    public boolean canPlaceBlockAt(World world, int i, int j, int k) {
        int l = world.getBlockId(i, j, k);
        Block u = Block.getBlock(world.getBlockId(i, j+1, k));
        return (l == 0 && (u == Block.ice || u == Block.permaice || u == Block.permafrost || u instanceof BlockIcicle));
    }
}
