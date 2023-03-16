package diarr.caveuberhaul;

import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;
import java.util.Random;


public class WorldFeatureLavaSwamp extends WorldFeature {
    private UberUtil uberUtil = new UberUtil();
    public boolean generate(World world, Random random, int i, int j, int k) {
        int radius = random.nextInt(20)+6;
        int height = random.nextInt(5)+1;
        //System.out.println("Swamp Center at x: "+rx+" z: "+rz);
        for(int circleX = i-radius;circleX<=i+radius;circleX++)
        {
            for(int circleZ = k-radius;circleZ<=k+radius;circleZ++)
            {
                for(int circleY = j;circleY<=j+height;circleY++)
                {
                    if(random.nextFloat()>=0.3f) {

                        double dist = uberUtil.distanceAB(circleX, circleY, circleZ, i, circleY, k);

                        if (uberUtil.isSurroundedAndFreeAbove(circleX, circleY, circleZ, world) && dist <= radius) {
                            //System.out.println("placed at x: "+(i+circleX)+" z: "+(k+circleZ));
                            for (int depth = circleY; depth >= circleY - random.nextInt(3); depth--) {
                                if(!world.isAirBlock(circleX,depth-1,circleZ))
                                world.setBlockRaw(circleX, depth, circleZ, Block.fluidLavaStill.id);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}

