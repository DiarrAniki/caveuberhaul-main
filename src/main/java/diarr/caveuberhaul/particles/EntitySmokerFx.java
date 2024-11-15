package diarr.caveuberhaul.particles;

import diarr.caveuberhaul.UberUtil;
import net.minecraft.client.entity.fx.EntityFX;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.world.World;

public class EntitySmokerFx extends EntityFX {
    float smokeScale;
    public EntitySmokerFx(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x,y, z, 0.0, 0.0, 0.0);
        this.xd *= 0.1;
        this.yd *= 0.1;
        this.zd *= 0.1;
        this.xd += motionX;
        this.yd += motionY;
        this.zd += motionZ;
        this.particleRed = this.particleGreen = this.particleBlue = (float)(Math.random() * 0.9);
        this.particleScale = 3;
        this.smokeScale = this.particleScale;
        this.particleMaxAge = (int)(48.0 / (Math.random() * 0.8 + 0.2));
        this.particleMaxAge = (int)((float)this.particleMaxAge);
        this.noPhysics = false;

    }

    public void renderParticle(Tessellator t, float partialTick, float rotationX, float rotationXZ, float rotationZ, float rotationYZ, float rotationXY) {
        float agePercentage = ((float)this.particleAge + partialTick) / (float)this.particleMaxAge * 32.0F;
        agePercentage = UberUtil.clamp(agePercentage,0,1);

        this.particleScale = this.smokeScale * (1+agePercentage);
        super.renderParticle(t, partialTick, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.remove();
        }

        int val = 7 - this.particleAge * 8 / this.particleMaxAge;
        if (val >= 0) {
            this.particleTexture = TextureRegistry.getTexture("minecraft:particle/puff_" + (8-val));
        } else {
            this.particleTexture = null;
        }

        this.yd += 0.001;
        this.move(this.xd, this.yd, this.zd);

        this.yd *= 0.96;
        if (this.onGround) {
            this.xd *= 0.7;
            this.zd *= 0.7;
        }

    }
}
