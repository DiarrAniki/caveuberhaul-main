package diarr.caveuberhaul.blocks;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;

import java.util.Random;

public class BlockSmoker extends Block {
    public BlockSmoker(String key, int id, Material material) {
        super(key, id, material);
        this.setTicking(true);
    }
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {
        if(rand.nextInt(2)==0) {
            if (anyPlayerInRange(world, x, y, z)) {
                double xp = (double) x + 0.5;
                double yp = (double) y + 1;
                double zp = (double) z + 0.5;
                world.spawnParticle("smokerSmoke", xp, yp, zp, +0.015f, +0.022f, +0.015f, 0,32);
            }
        }
    }
    public boolean anyPlayerInRange(World world, int x,int y,int z) {
        return world.getClosestPlayer((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, 32.0) != null;
    }
}
