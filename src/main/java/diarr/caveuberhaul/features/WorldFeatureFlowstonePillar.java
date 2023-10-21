package diarr.caveuberhaul.features;

import diarr.caveuberhaul.CaveUberhaul;
import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;

public class WorldFeatureFlowstonePillar extends WorldFeature {
    private final int perPillarLength;
    private final int perPillarFlowstonePillarLength;
    public WorldFeatureFlowstonePillar(int length,int perPillarFlowstonePillarLength)
    {
        this.perPillarLength = length;
        this.perPillarFlowstonePillarLength = perPillarFlowstonePillarLength;
    }

    @Override
    public boolean generate(World world, Random random, int i, int j, int k) {
        int groundY = UberUtil.getFloor(i,j,k,30,world);

            for(int length = 0; length<= perPillarLength-perPillarFlowstonePillarLength; length++)
            {
                if(length<perPillarFlowstonePillarLength) {
                    if (world.isAirBlock(i, j - length, k)) {
                        world.setBlockWithNotify(i, j - length, k, CaveUberhaul.flowstonePillar.id);
                    }
                    if (groundY != 0 && world.isAirBlock(i, groundY + length, k)&&world.getBlockId(i,groundY-1,k)==CaveUberhaul.flowstone.id||world.getBlockId(i,groundY-1,k)==CaveUberhaul.flowstonePillar.id) {
                        world.setBlockWithNotify(i, groundY + length, k, CaveUberhaul.flowstonePillar.id);
                    }
                }
                else
                {
                    if (world.isAirBlock(i, j - length, k)) {
                        world.setBlockWithNotify(i, j - length, k, CaveUberhaul.flowstoneStalagtite1.id);
                    }
                    if (groundY!=0&&world.isAirBlock(i, groundY + length, k)&&world.getBlockId(i,groundY-1,k)==CaveUberhaul.flowstone.id||world.getBlockId(i,groundY-1,k)==CaveUberhaul.flowstonePillar.id) {
                        world.setBlockWithNotify(i, groundY + length, k, CaveUberhaul.flowstoneStalagmite1.id);
                    }
                }
            }

        return false;
    }
}
