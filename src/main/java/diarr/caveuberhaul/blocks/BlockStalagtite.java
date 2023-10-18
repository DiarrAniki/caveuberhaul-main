package diarr.caveuberhaul.blocks;

import diarr.caveuberhaul.CaveUberhaul;
import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

import java.util.Random;

public class BlockStalagtite extends BlockConnectable {

    public static boolean fallInstantly = false;
    public BlockStalagtite(String s,int i, Material material,  int state) {
        super(s,i, Material.stone,state);
        this.setTickOnLoad(true);
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
    }

    public boolean anyPlayerInRange(World world, int x,int y,int z) {
        return world.getClosestPlayer((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, 32.0) != null;
    }

    public void checkForGrowthConditionAndPropagate(World world,int i,int j,int k)
    {
        if(world.getBlock(i,j+1,k)instanceof BlockFlowstone&&(world.getBlockId(i,j+2,k) == Block.fluidWaterStill.id||world.getBlockId(i,j+2,k) == Block.fluidWaterFlowing.id))
        {
            if(this.state==0)
            {
                world.setBlockMetadataWithNotify(i,j,k,2);
            }
            else
            {
                world.setBlockMetadataWithNotify(i,j,k,1);
            }
        }
        else if(world.getBlock(i,j+1,k)instanceof BlockStalagtite && world.getBlockMetadata(i,j+1,k)==1)
        {
            if(this.state==0)
            {
                world.setBlockMetadataWithNotify(i,j,k,2);
            }
            else
            {
                world.setBlockMetadataWithNotify(i,j,k,1);
            }
        }
    }

    public int tickRate() {
        return 256;
    }

    public void updateTick(World world, int i, int j, int k, Random random) {
        if(this.state==0) {
            int length = 0;
            while(world.getBlock(i,j+length,k)instanceof BlockStalagtite)
            {
                length++;
            }
            if (length <= 7) {
                if (world.getBlockMetadata(i, j, k) == 2 && world.isAirBlock(i, j - 1, k)) {
                    if (random.nextInt(512) == 1) {
                        world.setBlockAndMetadataWithNotify(i, j - 1, k, CaveUberhaul.flowstoneStalagtite1.id, 2);
                    } else {
                        world.scheduleBlockUpdate(i, j, k, this.id, this.tickRate());
                    }
                } else {
                    if (random.nextInt(2048) == 1) {
                        world.setBlockAndMetadataWithNotify(i, j - 1, k, CaveUberhaul.flowstoneStalagtite1.id, 0);
                    } else {
                        world.scheduleBlockUpdate(i, j, k, this.id, this.tickRate());
                    }
                }
            }
        }
    }

    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {
        if(world.getBlockMetadata(x,y,z)==2&&rand.nextInt(3)==1) {
            if (anyPlayerInRange(world, x, y, z)) {
                double xp = (double)x + 0.5 + (rand.nextFloat()-0.5f)*0.1f;
                double zp = (double)z + 0.5 + (rand.nextFloat()-0.5f)*0.1f;
                world.spawnParticle("drip", xp, y, zp, 0.0, -0.5f, 0.0);
            }
        }
    }

    private void tryToFall(World world, int i, int j, int k) {
        if (!(world.getBlock(i, j + 1, k) instanceof BlockFlowstone)|| !(world.getBlock(i, j + 1, k) instanceof BlockStalagtite)) {
            if (!fallInstantly ) {
                doFall(world,i,j,k);
            }
        }
    }

    public void doFall(World world, int i,int j,int k)
    {
        world.playBlockSoundEffect(i,j,k,Block.stone, EnumBlockSoundEffectType.MINE);
        while(Block.getBlock(world.getBlockId(i,j,k))instanceof BlockStalagtite)
        {
            EntityFallingStalactite entityfallingStalactite = new EntityFallingStalactite(world, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), this.id);
            world.entityJoinedWorld(entityfallingStalactite);
            world.setBlockWithNotify(i, j, k, 0);
            j--;
        }
    }

    public static boolean canFallBelow(World world, int i, int j, int k) {
        int l = world.getBlockId(i, j, k);
        if (l == 0||Block.getBlock(l)instanceof BlockStalagtite) {
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

    public void onBlockAdded(World world, int x, int y, int z) {
        //((BlockStalagtite) Block.getBlock(world.getBlockId(x,y,z))).doConnectLogic(world,x,y,z);
        this.doConnectLogic(world,x,y,z);
        this.checkForGrowthConditionAndPropagate(world,x,y,z);
        world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
        if(world.isAirBlock(i,j+1,k))
        {
            tryToFall(world,i,j,k);
        }
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
        if(Block.getBlock(world.getBlockId(x,y+1,z)) instanceof BlockStalagtite)
        {
            this.doConnectLogic(world,x,y+1,z);
        }
    }


    public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
        return dropCause != EnumDropCause.IMPROPER_TOOL ? new ItemStack[]{new ItemStack(CaveUberhaul.flowstoneStalagtiteItem.id,1,0)} : null;
    }

    public void doConnectLogic(World world, int x, int y, int z)
    {
        int connectState;

        if (!(Block.getBlock(world.getBlockId(x, y + 1, z)) instanceof BlockStalagtite)&&Block.getBlock(world.getBlockId(x, y - 1, z)) instanceof BlockStalagtite&&((BlockStalagtite) Block.getBlock(world.getBlockId(x, y - 1, z))).getConnectedState()==2) {
            //world.setBlockMetadata(x,y,z,3);
            connectState = 3;
        }
        else if (Block.getBlock(world.getBlockId(x, y - 1, z)) instanceof BlockStalagtite) {
            connectState = UberUtil.clamp(((BlockStalagtite) Block.getBlock(world.getBlockId(x, y - 1, z))).getConnectedState() + 1, 0, 2);
            //world.setBlockMetadata(x,y,z, world.getBlockMetadata(x,y-1,z)+1);
        }
        else
        {
            connectState = 0;
            //world.setBlockMetadata(x,y,z,0);
        }
        if(Block.getBlock(world.getBlockId(x, y + 1, z)) instanceof BlockStalagtite)
        {
            ((BlockStalagtite) Block.getBlock(world.getBlockId(x, y + 1, z))).doConnectLogic(world, x, y + 1, z);
        }


        //System.out.println(connectState);
        switch (connectState) {
            case 0: {
                if (Block.getBlock(world.getBlockId(x, y - 1, z)) instanceof BlockStalagmite) {
                    world.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagtiteConnected.id);
                    world.setBlockWithNotify(x, y-1, z, CaveUberhaul.flowstoneStalagmiteConnected.id);
                }
                else {
                    world.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagtite1.id);
                }
                break;
            }
            case 1: {
                world.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagtite2.id);
                break;
            }
            case 2: {
                world.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagtite3.id);
                break;
            }
            case 3: {
                world.setBlockWithNotify(x, y, z, CaveUberhaul.flowstoneStalagtite4.id);
                break;
            }
        }
    }

    /*@Override
    public Block setNotInCreativeMenu() {
        super.notInCreativeMenu = notInCreativeMenu;
        return this;
    }*/

    public boolean canPlaceBlockAt(World world, int i, int j, int k) {
        int l = world.getBlockId(i, j, k);
        Block u = Block.getBlock(world.getBlockId(i, j+1, k));
        return l == 0 && (u == CaveUberhaul.flowstone || u == CaveUberhaul.flowstonePillar || u instanceof BlockStalagtite);
    }

}
