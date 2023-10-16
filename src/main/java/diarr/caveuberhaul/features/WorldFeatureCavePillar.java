package diarr.caveuberhaul.features;

import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;

public class WorldFeatureCavePillar extends WorldFeature {
    //private UberUtil uberUtil = new UberUtil();
    int lowerPos, highPos;
    int blockFloor,blockCeil;

    public WorldFeatureCavePillar(int lowerPos, int highPos, int blockFloor, int blockCeil)
    {
        this.lowerPos = lowerPos;
        this.highPos = highPos;
        this.blockFloor = blockFloor;
        this.blockCeil = blockCeil;
    }

    public boolean generate(World world, Random random, int i, int j, int k) {
        //TODO rewrite the top and lower find function to use the noise maps for the caves instead. Also consider Sebastian Lague's video about planet generation and craters for merge between column noise and cave noise
        int lower = lowerPos;//UberUtil.getFloor(i, j, k,  32, world);
        int upper = highPos;//UberUtil.getCeiling(i, j, k, 32, world);
        int blockIdFloor = blockFloor;//UberUtil.getPillarBlock(i, lower - 1, k, world);
        int blockIdCeil = blockCeil;//UberUtil.getPillarBlock(i, upper + 1, k, world);
        if(blockIdCeil == 0 || blockIdFloor ==0 || lower<10)
        {
            return false;
        }
        int heightDif = upper - lower;
        double radius = heightDif*0.3f+random.nextInt(4)-2;
        int radius_int = (int) Math.round(radius);
        if (heightDif < 50 && heightDif > 3 ) {
            float randLimit = random.nextFloat()*0.3f;
            if(canPlace(world,i,lower-3,k,radius_int-1)&&canPlace(world,i,upper+3,k,radius_int-1)) {
                for (int x = i - radius_int; x <= i + radius_int; x++) {
                    for (int z = k - radius_int; z <= k + radius_int; z++) {
                        for (int y = lower - 3; y <= upper + 3; y++) {
                            double dist = UberUtil.distanceAB(x, lower, z, i, lower, k);
                            int c = Math.round(UberUtil.clampedLerp(3,1,dist/radius, 1, 3));
                            int limit = (int) Math.round (Math.pow((radius - dist)*(0.9-randLimit), 2)  + random.nextInt(c));

                            if (dist <= radius && (y <= lower-3 + limit || y >= upper+3 - limit)&& world.isAirBlock(x,y,z)&&!world.canBlockSeeTheSky(x,y,z)) {
                                if(y<lower+(heightDif/2)+random.nextInt(4)-2) {
                                    world.setBlock(x, y, z, blockIdFloor);
                                }
                                else
                                {
                                    world.setBlock(x, y, z, blockIdCeil);
                                }
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
