package diarr.caveuberhaul.features;

import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.HitResult;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;

public class WorldFeatureCavePillar extends WorldFeature {
    int highPos;
    int blockFloor,blockCeil;

    public WorldFeatureCavePillar(int highPos, int blockFloor, int blockCeil)
    {
        this.highPos = highPos;
        this.blockFloor = blockFloor;
        this.blockCeil = blockCeil;
    }

    public boolean generate(World world, Random random, int i, int j, int k) {
        int blockIdFloor = blockFloor;
        int blockIdCeil = blockCeil;
        Vec3d start = Vec3d.createVector(i, j, k);
        Vec3d end = Vec3d.createVector(i + random.nextInt(9) - 4, j + 50, k + random.nextInt(9) - 4);
        HitResult hit = world.checkBlockCollisionBetweenPoints(start, end);
        if(blockIdCeil == 0 || blockIdFloor ==0 || blockIdFloor ==Block.grass.id || blockIdCeil ==Block.grass.id|| Block.getBlock(blockIdFloor).blockMaterial==Material.leaves || Block.getBlock(blockIdCeil).blockMaterial==Material.leaves||j<10)
        {
            return false;
        }
        if (hit != null) {
            int heightDif = hit.y - j;
            if (heightDif > 3) {
                float radius = heightDif * 0.3f + random.nextInt(4) - 2;
                int radius_int = Math.round(radius);
                if (!canPlace(world, i, j - 5, k, radius_int) && !canPlace(world, hit.x, hit.y + 5, hit.z, radius_int) || world.canBlockSeeTheSky(i, j, k) || world.canBlockSeeTheSky(hit.x, hit.y, hit.z)) {
                    return false;
                }
                int minX = Math.min(i, hit.x);
                int minZ = Math.min(k, hit.z);
                int maxX = Math.max(i, hit.x);
                int maxZ = Math.max(k, hit.z);
                int midX = minX + (maxX - minX) / 2;
                int midZ = minZ + (maxZ - minZ) / 2;
                int h;
                float dist;
                int randModif;
                for (int x = i - radius_int; x <= i + radius_int; x++) {
                    for (int z = k - radius_int; z <= k + radius_int; z++) {
                        dist = (float) UberUtil.distanceAB(x, j, z, midX, j, midZ);
                        if (dist <= radius) {
                            randModif = Math.round(UberUtil.clampedLerp(3,1,(double)(dist/radius), 1, 3));
                            h = (int) Math.round (Math.pow((radius - dist)*(0.9-random.nextFloat()*0.3f), 2)  + random.nextInt(randModif));
                            for (int y = j - 5; y < j + h; y++) {
                                if (world.isAirBlock(x, y, z)) {
                                    if(y>j+(h-h/3)&&random.nextInt(3)==0)
                                    {
                                        world.setBlock(x, y, z, blockIdCeil);
                                    }
                                    else {
                                        world.setBlock(x, y, z, blockIdFloor);
                                    }
                                }
                            }
                        }
                    }
                }
                for (int x2 = hit.x - radius_int; x2 <= hit.x + radius_int; x2++) {
                    for (int z2 = hit.z - radius_int; z2 <= hit.z + radius_int; z2++) {
                        dist = (float) UberUtil.distanceAB(x2, hit.y, z2, midX, hit.y, midZ);
                        if (dist <= radius) {
                            randModif = Math.round(UberUtil.clampedLerp(3,1,(double)(dist/radius), 1, 3));
                            h = (int) Math.round (Math.pow((radius - dist)*(0.9-random.nextFloat()*0.4f), 2)  + random.nextInt(randModif));
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
        return true;
    }

    private boolean canPlace(World world,int i, int j, int k,int radius)
    {
        //boolean state=true;
        for(int x=i-radius/2;x<=i+radius/2;x++)
        {
            for(int z=k-radius/2;z<=k+radius/2;z++)
            {
                if(world.isAirBlock(x,j,z))
                {
                    return false;
                }
            }
        }
        return true;
    }
}
