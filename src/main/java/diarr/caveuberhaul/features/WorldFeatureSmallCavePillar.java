package diarr.caveuberhaul.features;

import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.HitResult;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLog;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;

public class WorldFeatureSmallCavePillar extends WorldFeature
{
    int highPos;
    int blockFloor,blockCeil;
    float radius;
    int height;
    public WorldFeatureSmallCavePillar(int highPos, int blockFloor, int blockCeil,float radius,int height)
    {
        this.highPos = highPos;
        this.blockFloor = blockFloor;
        this.blockCeil = blockCeil;
        this.radius = radius;
        this.height = height;
    }
    public boolean generate(World world, Random random, int i, int j, int k) {

        int blockIdFloor = blockFloor;
        int blockIdCeil = blockCeil;
        Vec3d start = Vec3d.createVector(i, j, k);
        Vec3d end = Vec3d.createVector(i + random.nextInt(5) - 2, j + 20, k + random.nextInt(5) - 2);
        HitResult hit = world.checkBlockCollisionBetweenPoints(start, end);
        if(blockIdCeil == 0 || blockIdFloor ==0 || blockIdFloor ==Block.grass.id || blockIdCeil ==Block.grass.id|| Block.getBlock(blockIdFloor).blockMaterial==Material.leaves || Block.getBlock(blockIdCeil).blockMaterial==Material.leaves|| Block.getBlock(blockIdFloor)instanceof BlockLog || Block.getBlock(blockIdCeil)instanceof BlockLog||j<10)
        {
            return false;
        }
        else
        {
            if (hit != null) {
                int heightDif = hit.y - j;
                if (heightDif > 3) {
                    int radius_int = Math.round(radius);
                    if (world.isAirBlock(i - radius_int, j - 5, k) || world.isAirBlock(i, j - 5, k - radius_int) || world.isAirBlock(i + radius_int, j - 5, k) || world.isAirBlock(i, j - 5, k + radius_int) || world.isAirBlock(hit.x - radius_int, hit.y + 5, hit.z) || world.isAirBlock(hit.x, hit.y + 5, hit.z - radius_int) || world.isAirBlock(hit.x + radius_int, hit.y + 5, hit.z) || world.isAirBlock(hit.x, hit.y + 5, hit.z + radius_int) || world.canBlockSeeTheSky(i, j, k) || world.canBlockSeeTheSky(hit.x, hit.y, hit.z)) {
                        return false;
                    } else {
                        int minX = Math.min(i, hit.x);
                        int minZ = Math.min(k, hit.z);
                        int maxX = Math.max(i, hit.x);
                        int maxZ = Math.max(k, hit.z);
                        float midX = minX + (maxX - minX) *0.5f;
                        float midZ = minZ + (maxZ - minZ) *0.5f;
                        int h;
                        float dist;
                        int randModif;
                        float distModif;
                        float midHeight = heightDif * 0.5f + 3;
                        for (int x = i - radius_int; x <= i + radius_int; x++) {
                            for (int z = k - radius_int; z <= k + radius_int; z++) {
                                dist = UberUtil.distanceAB(x, j, z, midX, j, midZ);
                                if (dist <= radius) {
                                    distModif = dist/radius;
                                    randModif = random.nextInt(Math.round(heightDif *0.25f * (distModif)) + 1);
                                    h = (int) MathHelper.clamp(Math.round(randModif + (midHeight - midHeight * Math.pow(distModif, 2))), 0, (int) midHeight);
                                    for (int y = j - 5; y < j + h; y++) {
                                        if (world.isAirBlock(x, y, z)) {
                                            if (y > j + (h - h *0.3f) && random.nextInt(3) == 0) {
                                                world.setBlock(x, y, z, blockIdCeil);
                                            } else {
                                                world.setBlock(x, y, z, blockIdFloor);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        for (int x2 = hit.x - radius_int; x2 <= hit.x + radius_int; x2++) {
                            for (int z2 = hit.z - radius_int; z2 <= hit.z + radius_int; z2++) {
                                dist = UberUtil.distanceAB(x2, hit.y, z2, midX, hit.y, midZ);
                                if (dist <= radius) {
                                    distModif = dist/radius;
                                    randModif = random.nextInt(Math.round(heightDif *0.25f * (distModif)) + 1);
                                    h = MathHelper.clamp(Math.round(randModif + (midHeight - midHeight * (distModif))), 0, (int) midHeight);
                                    for (int y = hit.y - h; y < hit.y + 5; y++) {
                                        if (world.isAirBlock(x2, y, z2)) {
                                            world.setBlock(x2, y, z2, blockIdCeil);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
