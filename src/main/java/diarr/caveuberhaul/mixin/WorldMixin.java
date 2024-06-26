package diarr.caveuberhaul.mixin;

import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = World.class,remap = false)
public class WorldMixin {

    @Shadow
    public Random rand = new Random();
    @Inject(method = "randomDisplayUpdates", at = @At(value = "INVOKE",target ="Lnet/minecraft/core/world/World;getBlockId(III)I",shift = At.Shift.AFTER))
    private void randomDisplayUpdatesMixin(int i, int j, int k, CallbackInfo ci)
    {
        int i1 = i + this.rand.nextInt(16) - this.rand.nextInt(16);
        int j1 = j + this.rand.nextInt(16) - this.rand.nextInt(16);
        int k1 = k + this.rand.nextInt(16) - this.rand.nextInt(16);
        int l1 = ((World)(Object)this).getBlockId(i1, j1, k1);
        if(this.rand.nextInt(12) > j1 && l1 == 0) {
            ((World)(Object)this).spawnParticle("voidFog", ((float)i1 + this.rand.nextFloat()), ((float)j1 + this.rand.nextFloat()), ((float)k1 + this.rand.nextFloat()), 0.0D, 0.0D, 0.0D, 0);
        }
    }
}
