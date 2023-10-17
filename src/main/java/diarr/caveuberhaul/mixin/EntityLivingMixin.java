package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.blocks.BlockStalagmite;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value= EntityLiving.class,remap = false)
public abstract class EntityLivingMixin extends Entity {
    public EntityLivingMixin(World world) {
        super(world);
    }
    @Redirect(method = "causeFallDamage", at = @At(value = "INVOKE", target = "Ljava/lang/Math;ceil(D)D"))
    public double fall(double a) {
        double f = a+3;
        int j = this.world.getBlockId(MathHelper.floor_double(this.x), MathHelper.floor_double(this.y - 0.2 - (double) this.heightOffset), MathHelper.floor_double(this.z));
        if (Block.getBlock(j) instanceof BlockStalagmite) {
            return (int) Math.ceil((f * 3 - 3.0F));
        }
        return (int) Math.ceil((f - 3.0F));
    }
}


