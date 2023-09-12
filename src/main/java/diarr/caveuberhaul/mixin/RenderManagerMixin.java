package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.blocks.EntityFallingStalagtite;
import diarr.caveuberhaul.blocks.RenderFallingStalagtite;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;

@Mixin(value= EntityRenderDispatcher.class,remap = false)
public class RenderManagerMixin {
    @Shadow
    private Map<Class<?>, EntityRenderer<?>> renderers;
    @Inject(method = "<init>", at = @At("TAIL"),cancellable = false)
    private void RenderManager(CallbackInfo ci)
    {
        renderers.put(EntityFallingStalagtite.class, new RenderFallingStalagtite());

        Iterator var1 = this.renderers.values().iterator();

        while(var1.hasNext()) {
            EntityRenderer<?> renderer = (EntityRenderer)var1.next();
            renderer.setRenderDispatcher((EntityRenderDispatcher) (Object)this);
        }
        //ci.cancel();
    }
}
