package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.blocks.BlockStalagtite;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.Explosion;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = Explosion.class,remap = false)
public class ExplosionMixin {
    @Shadow public float explosionSize;
    @Shadow protected Random ExplosionRNG;
    @Shadow public double explosionX;
    @Shadow public double explosionY;
    @Shadow public double explosionZ;
    @Shadow protected World worldObj;
    @Inject(method = "doExplosionB",at = @At("TAIL"))
    private void doExplode(boolean particles, CallbackInfo ci)
    {
        int destruction = (int) Math.round(this.explosionSize*4f);
        //System.out.println(destruction);
        for(int i=0;i<=destruction*2;i++)
        {
            int x = MathHelper.floor_double(explosionX)+ExplosionRNG.nextInt(destruction)-destruction/2;
            int z = MathHelper.floor_double(explosionZ)+ExplosionRNG.nextInt(destruction)-destruction/2;
            int y = (int) (MathHelper.floor_double(explosionY)+this.explosionSize+ExplosionRNG.nextInt(Math.round( this.explosionSize*2F+5)));
            if(Block.getBlock(worldObj.getBlockId(x,y,z))instanceof BlockStalagtite)
            {
                //worldObj.setBlockWithNotify(x,y,z,0);
                ((BlockStalagtite) Block.getBlock(worldObj.getBlockId(x,y,z))).doFall(worldObj,x,y,z);
            }
        }
    }

}
