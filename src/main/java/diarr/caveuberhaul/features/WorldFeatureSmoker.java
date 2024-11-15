package diarr.caveuberhaul.features;

import diarr.caveuberhaul.CaveUberhaul;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFluid;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;

public class WorldFeatureSmoker extends WorldFeature {

    @Override
    public boolean generate(World world, Random random, int i, int j, int k) {
        if(world.isAirBlock(i,j,k) && !world.isAirBlock(i,j-1,k)  && !(world.getBlock(i,j-1,k)instanceof BlockFluid))
        {
            for(int yt = 0; yt<4;yt++)
            {
                if(!world.isAirBlock(i,j+yt,k))
                {
                    return false;
                }
            }
            int height = Math.max(random.nextInt(5),random.nextInt(5));
            for(int y = 0;y<=height;y++)
            {
                world.setBlock(i,j+y,k, CaveUberhaul.smokerRock.id);
                if(y==0)
                {
                    int r = random.nextInt(100);
                    if(r==99)
                    {
                        world.setBlock(i,j+y,k, Block.oreDiamondBasalt.id);
                    }
                    else if(r>80)
                    {
                        world.setBlock(i,j+y,k, Block.oreGoldBasalt.id);
                    }
                    else if(r<40)
                    {
                        world.setBlock(i,j+y,k, Block.oreCoalBasalt.id);
                    }
                    else if(r<15)
                    {
                        world.setBlock(i,j+y,k, Block.oreIronBasalt.id);
                    }
                }
                if(y == height)
                {
                    world.setBlock(i,j+y,k,CaveUberhaul.smokerRockMaw.id);
                }
            }
            generateSmokerSide(i+1,j,k,world,4,random);
            generateSmokerSide(i-1,j,k,world,4,random);
            generateSmokerSide(i,j,k+1,world,4,random);
            generateSmokerSide(i,j,k-1,world,4,random);
            if(random.nextInt(8)==1)
            {
                generateSmokerSide(i-1,j,k-1,world,2,random);
                generateSmokerSide(i+1,j,k-1,world,2,random);
                generateSmokerSide(i-1,j,k+1,world,2,random);
                generateSmokerSide(i+1,j,k+1,world,2,random);
            }
            return true;
        }
        return false;
    }
    private void generateSmokerSide(int i, int j, int k, World world,int maxHeight, Random rand)
    {
        if(!world.isAirBlock(i,j-1,k)&& !(world.getBlock(i,j-1,k)instanceof BlockFluid))
        {
            int h = rand.nextInt(maxHeight);
            for(int y = 0; y<= h; y++)
            {
                world.setBlock(i,j+y,k,CaveUberhaul.smokerRock.id);
            }
        }
    }
}
