package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.blocks.EntityFallingIcicle;
import diarr.caveuberhaul.blocks.EntityFallingStalactite;
import diarr.caveuberhaul.blocks.RenderFallingIcicle;
import diarr.caveuberhaul.blocks.RenderFallingStalactite;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value= EntityRenderDispatcher.class,remap = false)
public class RenderManagerMixin {
    @Final
    @Shadow
    private Map<Class<?>, EntityRenderer<?>> renderers;
    @Inject(method = "<init>", at = @At("TAIL"))
    private void RenderManager(CallbackInfo ci)
    {
        RenderFallingStalactite fallingStalactite = new RenderFallingStalactite();
        RenderFallingIcicle fallingIcicle = new RenderFallingIcicle();
        renderers.put(EntityFallingStalactite.class, fallingStalactite);
        renderers.put(EntityFallingIcicle.class, fallingIcicle);
        fallingStalactite.setRenderDispatcher((EntityRenderDispatcher) (Object)this);
        fallingIcicle.setRenderDispatcher((EntityRenderDispatcher) (Object)this);
    }
}
