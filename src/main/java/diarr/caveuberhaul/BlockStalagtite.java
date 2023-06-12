package diarr.caveuberhaul;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import turniplabs.halplibe.helper.TextureHelper;

public class BlockStalagtite extends Block {

    public int blockState = 0;

    public BlockStalagtite(int i, Material material) {
        super(i, Material.rock);
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        if(canPlaceBlockAt(world,x,y,z) ) {
            doStalagtiteLogic(world, x, y, z);
        }
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
        if(world.isAirBlock(i,j+1,k))
        {
            world.setBlock(i,j,k,0);
        }
        doStalagtiteLogic(world, i, j, k);
    }

    public boolean isCollidable() {
        return true;
    }

    public void doStalagtiteLogic(World world, int x, int y, int z)
    {
        if(Block.getBlock(world.getBlockId(x,y+1,z))instanceof BlockStalagtite && Block.getBlock(world.getBlockId(x,y-1,z))instanceof BlockStalagmite)
        {
            blockState = 6;
        }
        if(Block.getBlock(world.getBlockId(x,y+1,z))instanceof BlockStalagtite)
        {
            blockState = ((BlockStalagtite) Block.getBlock(world.getBlockId(x,y+1,z))).blockState+1;
        }
        else
        {blockState = 0;}
        System.out.println(blockState);
        switch(blockState)
        {
            case 0:
            {
                //Texture = tip
                this.setTexCoords(CaveUberhaul.stalagtite1[0],CaveUberhaul.stalagtite1[1] );
                break;
            }
            case 1:
            {
                //setTexCoords(x,y-1);
                this.setTexCoords(CaveUberhaul.stalagtite2[0],CaveUberhaul.stalagtite2[1] );
                break;
            }
            case 2:
            {
                //setTexCoords(x,y-2);

                break;
            }
            case 3:
            {
                //setTexCoords(x,y-3);
                break;
            }
            case 4:
            {
                //setTexCoords(x,y-4);
                break;
            }
            case 5:
            {
                //setTexCoords(x,y-5);
                break;
            }
            case 6:
            {
                //setTexCoords(x,y-6);
                break;
            }
        }
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k) {
        int l = world.getBlockId(i, j, k);
        int u = world.getBlockId(i, j+1, k);
        return l == 0 && (u == CaveUberhaul.flowstone.blockID || u == CaveUberhaul.flowstonePillar.blockID ||u == CaveUberhaul.flowstoneStalagtite.blockID);
    }

    public int getRenderType() {
        return 1;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean blocksLight() {
        return false;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return null;
    }
}
