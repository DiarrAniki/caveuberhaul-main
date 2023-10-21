package diarr.caveuberhaul.features;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockGrass;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;

public class WorldGenTreeShapeDefaulti extends WorldFeature {
    protected int logID;
    protected int heightMod;

    public WorldGenTreeShapeDefaulti(int logID, int heightMod) {
        this.logID = logID;
        this.heightMod = heightMod;
    }
    @Override
    public boolean generate(World world, Random random, int i, int j, int k) {
        int l = random.nextInt(3) + this.heightMod;
        boolean flag = true;
        if (j >= 1 && j + l + 1 <= world.getHeightBlocks()) {
            int i1;
            int k1;
            int j2;
            int i3;
            int k3;
            for(i1 = j; i1 <= j + 1 + l; ++i1) {
                k1 = 1;
                if (i1 == j) {
                    k1 = 0;
                }

                if (i1 >= j + 1 + l - 2) {
                    k1 = 2;
                }

                for(j2 = i - k1; j2 <= i + k1 && flag; ++j2) {
                    for(i3 = k - k1; i3 <= k + k1 && flag; ++i3) {
                        if (i1 >= 0 && i1 < world.getHeightBlocks()) {
                            k3 = world.getBlockId(j2, i1, i3);
                            if (k3 != 0) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                i1 = world.getBlockId(i, j - 1, k);
                if ((Block.blocksList[i1] instanceof BlockGrass || i1 == Block.dirt.id) && j < world.getHeightBlocks() - l - 1) {
                    world.setBlockWithNotify(i, j - 1, k, Block.dirt.id);

                    for(k1 = 0; k1 < l; ++k1) {
                        j2 = world.getBlockId(i, j + k1, k);
                        if (j2 == 0) {
                            world.setBlockWithNotify(i, j + k1, k, this.logID);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
