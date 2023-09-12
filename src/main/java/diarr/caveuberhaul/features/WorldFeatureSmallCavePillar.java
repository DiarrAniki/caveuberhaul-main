package diarr.caveuberhaul.features;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;

public class WorldFeatureSmallCavePillar extends WorldFeature
{
    int lowerpos,highpos;
    int blockFloor,blockCeil;

    public WorldFeatureSmallCavePillar(int lowerpos,int highpos,int blockFloor,int blockCeil)
    {
        this.lowerpos = lowerpos;
        this.highpos = highpos;
        this.blockFloor = blockFloor;
        this.blockCeil = blockCeil;
    }
    public boolean generate(World world, Random random, int i, int j, int k) {

        int lower = lowerpos;//UberUtil.getFloor(i,j,k,16,world);
        int upper = highpos;//UberUtil.getCeiling(i,j,k,16,world);
        int blockIdFloor = blockFloor;//UberUtil.getPillarBlock(i, lower - 1, k, world);
        int blockIdCeil = blockCeil;//UberUtil.getPillarBlock(i, upper + 1, k, world);
        if(blockIdCeil == 0 || blockIdFloor ==0||lower<10)
        {
            return false;
        }
        int heightdif = upper-lower;
        if(heightdif<28 && heightdif>3)
        {
            for(int x = i-1;x<=i+1;x++) {
                for (int z = k - 1; z <= k + 1; z++) {
                    for (int y = lower - 2; y <= upper + 2; y++) {
                        boolean setBlock = false;
                        if ((x != i&&z==k) || (z != k&&x==i)) {
                            int limit = random.nextInt(2) + Math.round(heightdif*0.1f);
                            if(y<=lower+limit||y>=upper-limit)
                            {
                                //world.setBlock(x, y, z, blockId);
                                setBlock = true;
                            }
                        }
                        else if(x != i && z != k)
                        {
                            int limit = random.nextInt(1)-1 + Math.round(heightdif*0.05f);
                            if(y<=lower+limit||y>=upper-limit)
                            {
                                //world.setBlock(x, y, z, blockId);
                                setBlock = true;
                            }
                        }
                        else
                        {
                           //world.setBlock(x, y, z, blockId);
                            setBlock = true;
                        }
                        if(setBlock&&world.isAirBlock(x,y,z)&&!world.canBlockSeeTheSky(x,y,z))
                        {
                            if(y<lower+(heightdif/2)+random.nextInt(4)-2) {
                                world.setBlock(x, y, z, blockIdFloor);
                            }
                            else
                            {
                                world.setBlock(x, y, z, blockIdCeil);
                            }
                        }
                    }
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }
}
