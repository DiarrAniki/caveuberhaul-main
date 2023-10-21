package diarr.caveuberhaul.features;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class WorldFeatureDungeoni extends WorldFeature {
    public int blockIdWalls;
    public int blockIdFloor;
    public String mobOverride;

    public WorldFeatureDungeoni(int blockIdWalls, int blockIdFloor, String mobOverride) {
        this.blockIdWalls = blockIdWalls;
        this.blockIdFloor = blockIdFloor;
        this.mobOverride = mobOverride;
    }

    public boolean generate(World world, Random random, int x, int y, int z) {
        byte height = 3;
        int width = random.nextInt(2) + 2;
        int length = random.nextInt(2) + 2;
        int j1 = 0;

        int l1;
        int k2;
        int k3;
        for(l1 = x - width - 1; l1 <= x + width + 1; ++l1) {
            for(k2 = y - 1; k2 <= y + height + 1; ++k2) {
                for(k3 = z - length - 1; k3 <= z + length + 1; ++k3) {
                    Material material = world.getBlockMaterial(l1, k2, k3);
                    if (k2 == y - 1 && !material.isSolid()) {
                        return false;
                    }

                    if (k2 == y + height + 1 && !material.isSolid()) {
                        return false;
                    }

                    if ((l1 == x - width - 1 || l1 == x + width + 1 || k3 == z - length - 1 || k3 == z + length + 1) && k2 == y && world.isAirBlock(l1, k2, k3) && world.isAirBlock(l1, k2 + 1, k3)) {
                        ++j1;
                    }
                }
            }
        }

        if (j1 >= 1 && j1 <= 5) {
            for(l1 = x - width - 1; l1 <= x + width + 1; ++l1) {
                for(k2 = y + height; k2 >= y - 1; --k2) {
                    for(k3 = z - length - 1; k3 <= z + length + 1; ++k3) {
                        if (l1 != x - width - 1 && k2 != y - 1 && k3 != z - length - 1 && l1 != x + width + 1 && k2 != y + height + 1 && k3 != z + length + 1) {
                            world.setBlockWithNotify(l1, k2, k3, 0);
                        } else if (k2 >= 0 && !world.getBlockMaterial(l1, k2 - 1, k3).isSolid()) {
                            world.setBlockWithNotify(l1, k2, k3, 0);
                        } else if (world.getBlockMaterial(l1, k2, k3).isSolid()) {
                            if (k2 == y - 1 && random.nextInt(4) != 0) {
                                world.setBlockWithNotify(l1, k2, k3, 0);
                            } else {
                                world.setBlockWithNotify(l1, k2, k3, this.blockIdWalls);
                            }
                        }
                    }
                }
            }

            label111:
            for(l1 = 0; l1 < 2; ++l1) {
                for(k2 = 0; k2 < 3; ++k2) {
                    k3 = x + random.nextInt(width * 2 + 1) - width;
                    int i4 = z + random.nextInt(length * 2 + 1) - length;
                    if (world.isAirBlock(k3, y, i4)) {
                        int j4 = 0;
                        if (world.getBlockMaterial(k3 - 1, y, i4).isSolid()) {
                            ++j4;
                        }

                        if (world.getBlockMaterial(k3 + 1, y, i4).isSolid()) {
                            ++j4;
                        }

                        if (world.getBlockMaterial(k3, y, i4 - 1).isSolid()) {
                            ++j4;
                        }

                        if (world.getBlockMaterial(k3, y, i4 + 1).isSolid()) {
                            ++j4;
                        }

                        if (j4 == 1) {
                            world.setBlockWithNotify(k3, y, i4, Block.chestPlanksOak.id);
                            int k4 = 0;

                            while(true) {
                                if (k4 >= 8) {
                                    continue label111;
                                }



                                ++k4;
                            }
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

}
