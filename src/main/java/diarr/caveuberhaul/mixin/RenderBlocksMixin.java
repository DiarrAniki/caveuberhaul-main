package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.blocks.BlockConnectable;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.color.BlockColor;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value= RenderBlocks.class,remap = false)
public class RenderBlocksMixin {
    @Shadow
    private WorldSource blockAccess;
    @Shadow
    private World world;

    @Inject(method = "renderBlockCross", at = @At("HEAD"),cancellable = true)
    public void renderBlockReed(Block block, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        Tessellator tessellator = Tessellator.instance;
        float f = ((RenderBlocks)(Object)this).getBlockBrightness(this.blockAccess, i, j, k);
        int l = ((BlockColor)BlockColorDispatcher.getInstance().getDispatch(block)).getWorldColor(this.world, i, j, k);
        float f1 = (float)(l >> 16 & 255) / 255.0F;
        float f2 = (float)(l >> 8 & 255) / 255.0F;
        float f3 = (float)(l & 255) / 255.0F;
        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        double d = (double)i;
        double d1 = (double)j;
        double d2 = (double)k;
        if (block == Block.tallgrass || block == Block.tallgrassFern || block == Block.spinifex) {
            long l1 = (long)(i * 3129871) ^ (long)k * 116129781L ^ (long)j;
            l1 = l1 * l1 * 42317861L + l1 * 11L;
            d += ((double)((float)(l1 >> 16 & 15L) / 15.0F) - 0.5) * 0.5;
            d1 += ((double)((float)(l1 >> 20 & 15L) / 15.0F) - 1.0) * 0.2;
            d2 += ((double)((float)(l1 >> 24 & 15L) / 15.0F) - 0.5) * 0.5;
        }
        else if(block instanceof BlockConnectable)
        {
            long l1 = (long)(i * 3129871) ^ (long)k * 116129781L ;
            l1 = l1 * l1 * 42317861L + l1 * 11L;
            d += ((double)((float)(l1 >> 16 & 15L) / 15.0F) - 0.5) * 0.5;
            d2 += ((double)((float)(l1 >> 24 & 15L) / 15.0F) - 0.5) * 0.5;
        }

        ((RenderBlocks)(Object)this).renderCrossedSquares(block, this.blockAccess.getBlockMetadata(i, j, k), d, d1, d2);
        cir.setReturnValue(true);
    }
}
