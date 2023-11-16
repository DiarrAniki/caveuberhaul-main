package diarr.caveuberhaul.features;

import diarr.caveuberhaul.UberUtil;
import net.minecraft.core.HitResult;
import net.minecraft.core.block.Block;
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
        if(blockIdCeil == 0 || blockIdFloor ==0 || blockIdFloor ==Block.grass.id || blockIdCeil ==Block.grass.id|| Block.getBlock(blockIdFloor).blockMaterial==Material.leaves || Block.getBlock(blockIdCeil).blockMaterial==Material.leaves||j<10)
        {
            return false;
        }
        if (hit != null) {
            int heightDif = hit.y - j;
            if (heightDif > 3) {
                if(world.isAirBlock(i,j-5,k)||world.isAirBlock(hit.x,hit.y+5,hit.z)||world.canBlockSeeTheSky(i,j,k)||world.canBlockSeeTheSky(hit.x,hit.y,hit.z))
                {
                    return false;
                }
                int radius_int = Math.round(radius);
                int minX = Math.min(i , hit.x);
                int minZ = Math.min(k, hit.z);
                int maxX = Math.max(i, hit.x);
                int maxZ = Math.max(k, hit.z);
                int midX = minX+(maxX-minX)/2;
                int midZ = minZ+(maxZ-minZ)/2;
                int h;
                float dist;
                int randModif;
                float midHeight = heightDif / 2 + 3;
                for(int x= i-radius_int;x<=i+radius_int;x++)
                {
                    for(int z= k-radius_int;z<=k+radius_int;z++)
                    {
                        dist = (float) UberUtil.distanceAB(x, j, z, midX, j, midZ);
                        if (dist <= radius) {
                            randModif = random.nextInt(Math.round(heightDif / 4 * (dist / radius)) + 1);
                            h = (int) MathHelper.clamp(Math.round(randModif + (midHeight - midHeight * Math.pow(dist / radius, 2))), 0, (int) midHeight);
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
                for(int x2= hit.x-radius_int;x2<=hit.x+radius_int;x2++)
                {
                    for(int z2= hit.z-radius_int;z2<=hit.z+radius_int;z2++)
                    {
                        dist = (float) UberUtil.distanceAB(x2, hit.y, z2, midX, hit.y, midZ);
                        if (dist <= radius) {
                            randModif = random.nextInt(Math.round(heightDif / 4 * (dist / radius)) + 1);
                            h = MathHelper.clamp(Math.round(randModif + (midHeight - midHeight * (dist / radius))), 0, (int) midHeight);
                            for (int y = hit.y-h; y < hit.y+5; y++) {
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
}
