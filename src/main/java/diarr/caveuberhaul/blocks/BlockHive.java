package diarr.caveuberhaul.blocks;

import diarr.caveuberhaul.entity.EntityWasp;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.animal.EntityWolf;
import net.minecraft.core.world.World;

public class BlockHive extends Block {
    public BlockHive(String key, int id, Material material) {
        super(key, id, material);
    }

    @Override
    public void onBlockRemoved(World world, int x, int y, int z, int data) {
        if(world.rand.nextInt(100)<20) {
            EntityWasp entityWasp = new EntityWasp(world);
            world.entityJoinedWorld(entityWasp);
            entityWasp.setPos(x, y, z);
            entityWasp.setRot(world.rand.nextFloat() * 360.0F, 90.0F);
        }
    }
}
