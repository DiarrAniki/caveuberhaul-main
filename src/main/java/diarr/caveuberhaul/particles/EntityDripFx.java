package diarr.caveuberhaul.particles;

import net.minecraft.client.render.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.fx.EntityFX;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;

public class EntityDripFx extends EntityFX {
    public EntityDripFx(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.particleTextureIndex= 22;
        this.particleScale *= this.random.nextFloat() * 0.5F + 1F;
        this.xd *= 0.009999999776482582;
        this.yd = motionY  -0.35F;
        this.zd *= 0.009999999776482582;
        this.particleMaxAge = 128;
    }

    public void renderParticle(Tessellator t, float renderPartialTicks, float rotationX, float rotationXZ, float rotationZ, float rotationYZ, float rotationXY) {
        super.renderParticle(t,renderPartialTicks,rotationX,rotationXZ,rotationZ,rotationYZ,rotationXY);
    }

    public void tick()
    {
        super.tick();
        Material mat = this.world.getBlockMaterial(MathHelper.floor_double(this.x), MathHelper.floor_double(this.y), MathHelper.floor_double(this.z));
        if(mat == Material.lava)
        {
            //this.world.spawnParticle("smoke", this.x, this.y, this.z, 0.0, 0.1f, 0.0);
            this.world.playSoundAtEntity(this, "random.fizz", 0.1F+(random.nextFloat()-0.5f)*0.2f, 0.5F+random.nextFloat()*0.5f);
            this.remove();
        }
        else if (mat == Material.water||this.collision)
        {
            this.world.spawnParticle("splash", this.x, this.y+0.01f, this.z, 0.0, 0.0f, 0.0);
            this.world.playSoundAtEntity(this, "note.snare", 0.02f, 0.5F+random.nextFloat()*0.5f);
            this.remove();
        }
    }



}
