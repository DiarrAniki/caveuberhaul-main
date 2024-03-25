package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.blocks.BlockIcicle;
import net.minecraft.core.entity.projectile.EntityProjectile;
import net.minecraft.core.entity.projectile.EntitySnowball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityProjectile.class,remap = false)
public class EntitySnowballMixin {
    @Inject(method= "onHit(Lnet/minecraft/core/HitResult;)V",at=@At(value = "INVOKE",target ="Lnet/minecraft/core/entity/projectile/EntityProjectile;remove()V",shift = At.Shift.BEFORE))
    private void tickMixin(CallbackInfo ci)
    {
        EntityProjectile s = ((EntityProjectile)(Object)this);
        int x = (int) Math.floor(s.x);
        int y = (int) Math.floor(s.y);
        int z = (int) Math.floor(s.z);
        if(s.world.getBlock(x,y,z)instanceof BlockIcicle)
        {
            ((BlockIcicle) s.world.getBlock(x,y,z)).doFall(s.world,x,y,z);
        }
    }
}
