package diarr.caveuberhaul.features;

import diarr.caveuberhaul.CaveUberhaul;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;

public class WorldFeatureBigIcicle extends WorldFeature {

    public boolean generate(World world, Random random, int i, int j, int k) {
        if(CaveUberhaul.icicle1.canPlaceBlockAt(world,i,j,k)) {
            int length = random.nextInt(6);
            for (int y = j; y > j - length; y--) {
                if (CaveUberhaul.icicle1.canPlaceBlockAt(world, i, y, k)) {
                    world.setBlockWithNotify(i, y, k, CaveUberhaul.icicle1.id);
                }
            }
            return true;
        }
        else return false;
    }
}
