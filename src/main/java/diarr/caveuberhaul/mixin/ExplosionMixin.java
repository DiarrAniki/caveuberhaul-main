package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.blocks.BlockStalactiteBase;
import diarr.caveuberhaul.blocks.BlockIcicle;
import diarr.caveuberhaul.blocks.BlockStalagtite;
import net.minecraft.core.HitResult;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.Explosion;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = Explosion.class,remap = false)
public class ExplosionMixin {
    @Shadow public float explosionSize;
    @Shadow protected Random ExplosionRNG;
    @Shadow public double explosionX;
    @Shadow public double explosionY;
    @Shadow public double explosionZ;
    @Shadow protected World worldObj;
    @Inject(method = "doExplosionB",at = @At("TAIL"))
    private void doExplode(boolean particles, CallbackInfo ci)
    {
        int destruction = Math.round(this.explosionSize*4f);
        for(int i=0;i<=destruction*2;i++)
        {
            int x = MathHelper.floor_double(explosionX)+ExplosionRNG.nextInt(destruction)-destruction/2;
            int z = MathHelper.floor_double(explosionZ)+ExplosionRNG.nextInt(destruction)-destruction/2;
            int y = (MathHelper.floor_double(explosionY)+8+destruction);
            Vec3d start = Vec3d.createVector(explosionX,explosionY,explosionZ);
            Vec3d end = Vec3d.createVector(x,y,z);
            HitResult hit = this.worldObj.checkBlockCollisionBetweenPoints(start, end);
            if(hit !=null &&(worldObj.getBlock(hit.x,hit.y,hit.z) instanceof BlockStalagtite||worldObj.getBlock(hit.x,hit.y,hit.z) instanceof BlockIcicle))
            {
                ((BlockStalactiteBase) worldObj.getBlock(hit.x,hit.y,hit.z)).doFall(worldObj,hit.x,hit.y,hit.z);
            }
        }
    }

}
