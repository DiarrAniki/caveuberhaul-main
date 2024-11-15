package diarr.caveuberhaul.features;

import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;


public class WorldFeatureLavaSwamp extends WorldFeature {
    public boolean generate(World world, Random random, int x, int y, int z) {
        int radius = random.nextInt(24)+6;
        int height = random.nextInt(7)+1;
        for(int circleX = x-radius;circleX<=x+radius;circleX++)
        {
            for(int circleZ = z-radius;circleZ<=z+radius;circleZ++)
            {
                if (random.nextFloat() < 0.3f) {continue;}
                if (Math.hypot(circleX - x, circleZ- z) > radius) {continue;}
                for (int circleY = y; circleY <= y + height; circleY += 2) {
                    if (UberUtil.isSurroundedAndFreeAbove(circleX, circleY, circleZ, world)) {
                        world.setBlock(circleX, circleY, circleZ, Block.fluidLavaStill.id);
                        world.setBlock(circleX, circleY - 1, circleZ, Block.fluidLavaStill.id);
                    }
                }
            }
        }
        return true;
    }
}

