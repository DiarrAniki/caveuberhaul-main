package diarr.caveuberhaul;

import net.minecraft.shared.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;


public class WorldFeatureLavaSwamp extends WorldGenerator {
    private UberUtil uberUtil = new UberUtil();
    private static FastNoiseLite swampNoise = new FastNoiseLite();
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
                            world.setBlock(circleX, circleY, circleZ, Block.fluidLavaStill.blockID);
                            world.setBlock(circleX, circleY-1, circleZ, Block.fluidLavaStill.blockID);
                        }
                    }
                }
            }
        }
        return true;
    }
}

