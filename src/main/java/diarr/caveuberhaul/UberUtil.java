package diarr.caveuberhaul;

import net.minecraft.core.Minecraft;
import net.minecraft.core.block.*;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;

public class UberUtil
{
    // Recursive binary search, this search always converges on the surface in 8 in cycles for the range 255 >= y >= 0
    public int recursiveBinarySurfaceSearchUp(int localX, int localZ, int searchTop, int searchBottom, short[] data)
    {
        int top = searchTop;
        if (searchTop > searchBottom)
        {
            int searchMid = (searchBottom + searchTop) / 2;
            if (isRockBlock(Block.getBlock(data[localX << Minecraft.WORLD_HEIGHT_BITS + 4 | localZ << Minecraft.WORLD_HEIGHT_BITS | searchMid])))
            {
                top = recursiveBinarySurfaceSearchUp(localX, localZ, searchTop, searchMid + 1,data);
            } else
            {
                top = recursiveBinarySurfaceSearchUp(localX, localZ, searchMid, searchBottom,data);
            }
        }
        return top;
    }

    public boolean isRockBlock(Block block)
    {
        // Replace anything that's made of rock which should hopefully work for most modded type stones (and maybe not break everything)
        if(block == null)
        {
            return false;
        }
        else {
            return block.blockMaterial == Material.rock;
        }
    }

    public boolean solidBlockExists(int x,int y,int z, World world)
    {
        return !world.isAirBlock(x,y,z) && !(Block.getBlock(world.getBlockId(x,y,z))instanceof BlockFluid);
    }
    public float lerp(float a, float b, float t)
    {
        return a+(b-a)*t;
    }
    public float lerp(float a, float b, double t)
    {
        return (float) (a+(b-a)*t);
    }
    public float lerp(int a, int b, float t)
    {
        return (a+(b-a)*t);
    }
    public float lerp(int a, int b, double t)
    {
        return (float)(a+(b-a)*t);
    }
    public int clampedLerp(int a, int b, int t,int min, int max)
    {
        return clamp((a+(b-a)*t),min,max);
    }
    public float clampedLerp(float a, float b, double t,float min, float max)
    {
        return clamp(lerp(a,b,t),min,max);
    }
    public float clampedLerp(int a, int b, double t,int min, int max)
    {
        return clamp(lerp(a,b,t),min,max);
    }
    public float clamp(float val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }

    public int clamp(int val, int min, int max) {
        return val < min ? min : val > max ? max : val;
    }

    public double distanceAB(int x1,int y1,int z1,int x2, int y2, int z2)
    {
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2)+Math.pow(z1-z2,2));
    }

    public boolean isSurroundedAndFreeAbove(int x, int y, int z, World world)
    {
        return (isNeitherAirNorWater(x-1,y,z,world) && isNeitherAirNorWater(x+1,y,z,world)  && isNeitherAirNorWater(x,y,z-1,world)  && isNeitherAirNorWater(x,y,z+1,world)  && world.isAirBlock(x,y+1,z));
    }

    public boolean isNeitherAirNorWater(int x,int y,int z, World world)
    {
        return !(world.isAirBlock(x,y,z) || world.getBlock(x,y,z).blockMaterial == Material.water);
    }

    public int GetFirstAirBlock(int x,int y,int z, World world)
    {
        if(world.isAirBlock(x,y,z)&& !world.isAirBlock(x,y-1,z))
        {
            return GetBlocksDown(x,y,z,0,10,world);
        }
        else if(world.isAirBlock(x,y,z)&&world.isAirBlock(x,y-1,z))
        {
            return y;
        }
        else
        {
            return GetBlocksUp(x,y,z,0,10,world);
        }
    }

    public int GetBlocksDown(int x,int y,int z,int index,int limit, World world)
    {
        if(y>=y-limit+index&&y>8)
        {
            if(solidBlockExists(x,y-1,z,world))
            {
                return y;
            }
            else
            {
                index++;
                return GetBlocksDown(x, y - 1, z,index,limit,world);
            }
        }
        else
        {
            return 0;
        }
    }

    public int GetBlocksUp(int x,int y,int z,int index,int limit,World world)
    {
        if(y<=y+limit-index&&y>8)
        {
            if(solidBlockExists(x,y+1,z,world))
            {
                return y;
            }
            else
            {
                index++;
                return GetBlocksUp(x, y + 1, z,index,limit,world);
            }
        }
        else
        {
            return 0;
        }
    }

    public int GetPillarBlock(int x,int y,int z,World world)
    {
        int blockId = world.getBlockId(x,y,z);
        if(blockId == Block.bedrock.id||blockId==0||blockId == Block.dirt.id||blockId==Block.gravel.id||Block.getBlock(blockId)instanceof BlockFluid)
        {
            return 1;
        }
        else
        {
            return blockId;
        }
    }

}
