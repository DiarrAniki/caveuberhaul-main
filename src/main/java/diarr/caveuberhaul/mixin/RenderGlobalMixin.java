package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.particles.EntityDripFx;
import diarr.caveuberhaul.particles.EntityVoidFogFX;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value= RenderGlobal.class,remap = false)
public class RenderGlobalMixin {
    @Shadow
    private Minecraft mc;
    @Shadow
    private World worldObj;
    @Inject(method = "addParticle(Ljava/lang/String;DDDDDDD)V", at = @At("TAIL"), cancellable = true)
    private void addParticle(String particleId, double x, double y, double z, double motionX, double motionY, double motionZ, double maxDistance, CallbackInfo ci) {
        if (particleId.equals("drip")) {
            this.mc.effectRenderer.addEffect(new EntityDripFx(this.worldObj, x, y, z, motionX, motionY, motionZ));
            ci.cancel();
        }
        else if(particleId.equals("voidFog"))
        {
            this.mc.effectRenderer.addEffect(new EntityVoidFogFX(this.worldObj, x, y, z, motionX, motionY, motionZ));
            ci.cancel();
        }
    }
}

