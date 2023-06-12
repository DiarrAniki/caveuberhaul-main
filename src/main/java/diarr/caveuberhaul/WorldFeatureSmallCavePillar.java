package diarr.caveuberhaul;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldFeatureSmallCavePillar extends WorldGenerator
{
    private UberUtil uberUtil = new UberUtil();

    public boolean generate(World world, Random random, int i, int j, int k) {

        int lower = uberUtil.GetFloor(i,j,k,16,world);
        int upper = uberUtil.GetCeiling(i,j,k,16,world);
        int blockId = uberUtil.GetPillarBlock(i,lower-1,k,world);
        int heightdif = upper-lower;
        if(heightdif<28 && heightdif>4 && world.getBlockId(i,lower+1,k) != Block.fluidLavaStill.blockID && world.getBlockId(i,lower+1,k) != Block.fluidLavaFlowing.blockID)
        {
            for(int x = i-1;x<=i+1;x++) {
                for (int z = k - 1; z <= k + 1; z++) {
                    for (int y = lower - 2; y <= upper + 2; y++) {
                        if ((x != i&&z==k) || (z != k&&x==i)) {
                            int limit = random.nextInt(2) + Math.round(heightdif*0.1f);
                            if(y<=lower+limit||y>=upper-limit)
                            {
                                world.setBlock(x, y, z, blockId);
                            }
                        }
                        else if(x != i && z != k)
                        {
                            int limit = random.nextInt(1)-1 + Math.round(heightdif*0.05f);
                            if(y<=lower+limit||y>=upper-limit)
                            {
                                world.setBlock(x, y, z, blockId);
                            }
                        }
                        else
                        {
                            world.setBlock(x, y, z, blockId);
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
