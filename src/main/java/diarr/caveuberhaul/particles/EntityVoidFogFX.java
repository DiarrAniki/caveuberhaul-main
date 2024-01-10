package diarr.caveuberhaul.particles;

import net.minecraft.client.entity.fx.EntityFX;
import net.minecraft.client.render.Tessellator;
import net.minecraft.core.world.World;

public class EntityVoidFogFX  extends EntityFX {

    public EntityVoidFogFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        float var14 = this.random.nextFloat() * 0.1F + 0.2F;
        this.particleRed = var14;
        this.particleGreen = var14;
        this.particleBlue = var14;
        this.particleTextureIndex = 0;
        this.setSize(0.02F, 0.02F);
        this.particleScale *= this.random.nextFloat() * 0.6F + 0.5F;
        this.xd *= 0.02F;
        this.yd *= 0.02F;
        this.zd *= 0.02F;
        this.particleMaxAge = (int)(20.0D / (Math.random() * 0.8D + 0.2D));
        this.collision = false;
    }

    public void renderParticle(Tessellator t, float renderPartialTicks, float rotationX, float rotationXZ, float rotationZ, float rotationYZ, float rotationXY) {
        super.renderParticle(t,renderPartialTicks,rotationX,rotationXZ,rotationZ,rotationYZ,rotationXY);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.99D;
        this.yd *= 0.99D;
        this.zd *= 0.99D;
        if(this.particleMaxAge-- <= 0) {
            this.remove();
        }
    }
}
