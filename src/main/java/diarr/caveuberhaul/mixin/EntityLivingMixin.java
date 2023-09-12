package diarr.caveuberhaul.mixin;

import com.mojang.nbt.CompoundTag;
import diarr.caveuberhaul.blocks.BlockStalagmite;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value= EntityLiving.class,remap = false)
public class EntityLivingMixin extends Entity {


    public EntityLivingMixin(World world) {
        super(world);
    }

    @Override
    public void init() {

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {

    }


    @Inject(method = "causeFallDamage", at = @At("HEAD"),cancellable = true)
    public void fall(float f, CallbackInfo ci) {
        super.causeFallDamage(f);
        int i;
        int j = this.world.getBlockId(MathHelper.floor_double(this.x), MathHelper.floor_double(this.y - 0.2 - (double)this.heightOffset), MathHelper.floor_double(this.z));
        if (Block.getBlock(j) instanceof BlockStalagmite) {
            i = (int) Math.ceil((double) (f*3 - 3.0F));
        }
        else
        {
            i = (int) Math.ceil((double) (f - 3.0F));
        }
        if (i > 0) {
            this.hurt((Entity)null, i, DamageType.FALL);
            if (j > 0) {
                this.world.playBlockSoundEffect(this.x, this.y - (double)this.heightOffset, this.z, Block.blocksList[j], EnumBlockSoundEffectType.ENTITY_LAND);
            }
        }
        ci.cancel();
    }
}
