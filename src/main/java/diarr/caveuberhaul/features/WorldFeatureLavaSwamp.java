package diarr.caveuberhaul.features;

import diarr.caveuberhaul.CaveUberhaul;
import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;
import useless.profiler.Profiler;

import java.util.Random;


public class WorldFeatureLavaSwamp extends WorldFeature {
    public boolean generate(World world, Random random, int x, int y, int z) {
        Profiler.methodStart(CaveUberhaul.MOD_ID,"lava-swamp");
        int radius = random.nextInt(20)+6;
        int height = random.nextInt(5)+1;
        for(int circleX = x-radius;circleX<=x+radius;circleX++)
        {
            for(int circleZ = z-radius;circleZ<=z+radius;circleZ++)
            {
                for(int circleY = y;circleY<=y+height;circleY++)
                {
                    if(random.nextInt(10)> 2) {

                        double dist = UberUtil.distanceAB(circleX, circleY, circleZ, x, circleY, z);

                        if (dist <= radius && UberUtil.isSurroundedAndFreeAbove(circleX, circleY, circleZ, world)) {
                            world.setBlock(circleX, circleY, circleZ, Block.fluidLavaStill.id);
                            world.setBlock(circleX, circleY-1, circleZ, Block.fluidLavaStill.id);
                        }
                    }
                }
            }
        }
        Profiler.methodEnd(CaveUberhaul.MOD_ID,"lava-swamp");
        return true;
    }
}

