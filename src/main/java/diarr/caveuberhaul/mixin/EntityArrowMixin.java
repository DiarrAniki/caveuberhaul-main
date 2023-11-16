package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.blocks.BlockStalactiteBase;
import net.minecraft.core.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityArrow.class,remap = false)
public class EntityArrowMixin {
    @Shadow
    protected int xTile;
    @Shadow
    protected int yTile;
    @Shadow
    protected int zTile;
    @Inject(method = "inGroundAction",at = @At("HEAD"))
    private void inGroundMixin(CallbackInfo ci)
    {
        EntityArrow a = ((EntityArrow)(Object)this);
        if(a.world.getBlock(xTile,yTile,zTile) instanceof BlockStalactiteBase)
        {
            ((BlockStalactiteBase) a.world.getBlock(xTile,yTile,zTile)).doFall(a.world,xTile,yTile,zTile);
        }
    }
}
