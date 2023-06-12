package diarr.caveuberhaul;



import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldFeatureCavePillar extends WorldGenerator {
    private UberUtil uberUtil = new UberUtil();

    public boolean generate(World world, Random random, int i, int j, int k) {
        //TODO rewrite the top and lower find function to use the noise maps for the caves instead. Also consider Sebastian Lagues video about planet generation and craters for merge between collumn noise and cave noise
        int lower = uberUtil.GetFloor(i, j, k,  32, world);
        int upper = uberUtil.GetCeiling(i, j, k, 32, world);
        int blockId = uberUtil.GetPillarBlock(i, lower - 1, k, world);
        int heightdif = upper - lower;
        double radius = heightdif*0.3f+random.nextInt(4)-2;
        int radius_int = (int) Math.round(radius);
        if (heightdif < 50 && heightdif > 4 && world.getBlockId(i,lower+1,k) != Block.fluidLavaStill.blockID && world.getBlockId(i,lower+1,k) != Block.fluidLavaFlowing.blockID) {
            float randLimit = random.nextFloat()*0.3f;
            if(canPlace(world,i,lower-3,k,radius_int-1)&&canPlace(world,i,upper+3,k,radius_int-1)) {
                for (int x = i - radius_int; x <= i + radius_int; x++) {
                    for (int z = k - radius_int; z <= k + radius_int; z++) {
                        for (int y = lower - 3; y <= upper + 3; y++) {
                            double dist = uberUtil.distanceAB(x, lower, z, i, lower, k);
                            int c = Math.round(uberUtil.clampedLerp(3,1,dist/radius, 1, 3));
                            int limit = (int) Math.round (Math.pow((radius - dist)*(0.9-randLimit), 2)  + random.nextInt(c));

                            if (dist <= radius && (y <= lower-3 + limit || y >= upper+3 - limit)) {
                                world.setBlock(x, y, z, blockId);
                            }
                                /*if (dist <= innerRad) {
                                    world.setBlock(x, y, z, blockId);
                                } else if (y <= lower + limit || y >= upper - limit) {
                                    world.setBlock(x, y, z, blockId);
                                }*/
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean canPlace(World world,int i, int j, int k,int radius)
    {
        //boolean state=true;
        for(int x=i-radius;x<=i+radius;x++)
        {
            for(int z=k-radius;z<=k+radius;z++)
            {
                if(world.isAirBlock(x,j,z))
                {
                    return false;
                }
            }
        }
        return true;
    }
}
