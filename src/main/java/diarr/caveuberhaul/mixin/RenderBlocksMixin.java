package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.blocks.BlockStalactiteBase;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.Tessellator;
import net.minecraft.core.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value= RenderBlocks.class,remap = false)
public class RenderBlocksMixin {
    @Inject(method = "renderBlockCross", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderBlocks;renderCrossedSquares(Lnet/minecraft/core/block/Block;IDDD)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    public void renderBlockReed(Block block, int i, int j, int k, CallbackInfoReturnable<Boolean> cir, Tessellator tessellator, float f, int l, float f1, float f2, float f3, double d, double d1, double d2) {
        if(block instanceof BlockStalactiteBase)
        {
            long l2 = (i * 3129871L) ^ (long)k * 116129781L ;
            l2 = l2 * l2 * 42317861L + l2 * 11L;
            d += ((double)((float)(l2 >> 16 & 15L) / 15.0F) - 0.5) * 0.5;
            d1 += ((double)((float)(l2 >> 24 & 15L) / 15.0F) - 0.5) * 0.5;
        }
    }
}
