package diarr.caveuberhaul.blocks;

import com.mojang.nbt.CompoundTag;
import diarr.caveuberhaul.CaveUberhaul;
import diarr.caveuberhaul.blocks.BlockStalagtite;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;

public class EntityFallingStalagtite extends Entity {
    public int blockID;
    public int fallTime;

    public EntityFallingStalagtite(World world) {
        super(world);
        this.blockID = CaveUberhaul.flowstoneStalagtite1.id;
        this.fallTime = 0;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    public EntityFallingStalagtite(World world, double d, double d1, double d2, int i) {
        super(world);
        this.fallTime = 0;
        this.blockID = i;
        this.blocksBuilding = true;
        this.setSize(0.98F, 0.98F);
        this.heightOffset = this.bbHeight / 2.0F;
        this.setPos(d, d1, d2);
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.xo = d;
        this.yo = d1;
        this.zo = d2;
    }
    protected boolean canTriggerWalking() {
        return false;
    }


    public boolean canBeCollidedWith() {
        return !this.removed;
    }

    public void tick() {
        if (this.blockID == 0) {
            this.remove();
        } else {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            ++this.fallTime;
            this.yd -= 0.03999999910593033;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.9800000190734863;
            this.yd *= 0.9800000190734863;
            this.zd *= 0.9800000190734863;
            int i = MathHelper.floor_double(this.x);
            int j = MathHelper.floor_double(this.y);
            int k = MathHelper.floor_double(this.z);
            if (this.world.getBlockId(i, j, k) == this.blockID) {
                this.world.setBlockWithNotify(i, j, k, 0);
            }

            if (this.onGround) {
                this.xd *= 0.699999988079071;
                this.zd *= 0.699999988079071;
                this.yd *= -0.5;
                this.remove();
                if ((!this.world.canBlockBePlacedAt(this.blockID, i, j, k, true, Side.BOTTOM) || BlockStalagtite.canFallBelow(this.world, i, j - 1, k) || !this.world.setBlockWithNotify(i, j, k, this.blockID)) && !this.world.isClientSide) {
                    this.spawnAtLocation(CaveUberhaul.flowstoneStalagtiteItem.id, 1);
                    world.playBlockSoundEffect(i,j,k, Block.stone, EnumBlockSoundEffectType.MINE);
                    //Play crashing sound
                    for (Entity e:world.getEntitiesWithinAABB(EntityLiving.class, AABB.getBoundingBox(i-0.5F,j-0.5F,k-0.5F,i+0.5F,j+0.5F,k+0.5F)))
                    {
                        e.hurt(null,this.fallTime, DamageType.COMBAT);
                    }
                }
            } else if (this.fallTime > 100 && !this.world.isClientSide) {
                //this.spawnAtLocation(CaveUberhaul.flowstoneStalagtiteItem.id, 1);
                this.remove();
            }
        }
    }

    /*protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Tile", (short)this.blockID);
    }

    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        this.blockID = nbttagcompound.getShort("Tile") & 16383;
    }*/

    public float getShadowSize() {
        return 0.0F;
    }

    public World getWorld() {
        return this.world;
    }
}
