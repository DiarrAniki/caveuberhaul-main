package diarr.caveuberhaul.entity;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityFlying;
import net.minecraft.core.entity.monster.EntityMonster;
import net.minecraft.core.entity.monster.EntityPigZombie;
import net.minecraft.core.entity.monster.IEnemy;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.entity.projectile.EntityFireball;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pathfinder.Path;
import org.useless.dragonfly.model.entity.AnimationState;

import java.util.List;

public class EntityWasp extends EntityFlying implements IEnemy {
    public AnimationState idleState = new AnimationState();
    public AnimationState angryState = new AnimationState();
    protected Path pathToEntity;
    public Entity entityToAttack = null;
    private int attackStrength = 1;
    private int timeTick = 0;
    private int angerLevel = 0;
    private int randomSoundDelay = 0;
    public EntityWasp(World world) {
        super(world);
        this.attackStrength = 1;
        this.setSize(.5f,.5f);
        idleState.start(entityAge);
        this.moveSpeed = 5f;
        this.speed = 5f;
        this.flySpeed = 3f;
        this.scoreValue = 25;
        this.setHealthRaw(6);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    public int getAngerLevel()
    {return this.angerLevel;}

    public void tick() {
        this.moveSpeed = this.entityToAttack == null ? 0.5F : 0.95F;
        if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
            this.world.playSoundAtEntity((Entity)null, this, "mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.8F);
        }

        super.tick();
    }

    @Override
    public boolean hurt(Entity attacker, int i, DamageType type) {
        if (type == DamageType.FIRE) {
            return false;
        } else {
            if (attacker instanceof EntityPlayer) {
                    List list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.bb.expand(32.0, 32.0, 32.0));

                    for (int j = 0; j < list.size(); ++j) {
                        Entity entity1 = (Entity) list.get(j);
                        if (entity1 instanceof EntityWasp) {
                            EntityWasp entitywasp = (EntityWasp) entity1;
                            entitywasp.becomeAngryAt(attacker);

                        }
                    }
                    this.becomeAngryAt(attacker);
            }

            return super.hurt(attacker, i, type);
        }
    }

    protected void updatePlayerActionState() {
        super.updatePlayerActionState();
        if(entityToAttack!=null) {
            this.faceEntity(entityToAttack,5,5);
            this.moveEntityWithHeading((float) Math.sin(this.entityAge/10)*2f,9);
            this.yd = (entityToAttack.y- (double) random.nextInt(7) /10-this.y)*0.01d;
            attackEntity(entityToAttack,distanceTo(entityToAttack));
        }
        else
        {
            this.angerLevel = 0;
            if(timeTick>10)
            {
                this.xRot += (random.nextInt(90)-45);
                this.yRot += (random.nextInt(90)-45);
                this.yd  = (random.nextFloat()*2-1)*0.03f;
                timeTick = 0;
            }
            else
            {
                timeTick++;
            }
            this.moveEntityWithHeading((float) Math.sin(this.entityAge),1);
        }
    }

    protected void attackEntity(Entity entity, float distance) {
        if (this.attackTime <= 0 && distance < 2.0F && entity.bb.maxY > this.bb.minY && entity.bb.minY < this.bb.maxY) {
            this.attackTime = 10;
            entity.hurt(this, this.attackStrength, DamageType.COMBAT);
        }
        if(entityToAttack == null)
        {
            angryState.stop();
            idleState.start(entityAge);
        }

    }
    private void becomeAngryAt(Entity entity) {
        idleState.stop();
        angryState.start(entityAge);
        this.entityToAttack = entity;
        this.angerLevel = 400 + this.random.nextInt(400);
        this.randomSoundDelay = this.random.nextInt(40);
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putShort("WaspAnger", (short)this.angerLevel);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.angerLevel = tag.getShort("WaspAnger");
        if(this.angerLevel>0)
        {
            angryState.start(world.dayCountLastTick);
        }
        else
        {
            idleState.start(world.dayCountLastTick);
        }
    }

    public String getEntityTexture() {
        return "/assets/caveuberhaul/entity/wasp_worker.png";
    }

    public String getDefaultEntityTexture() {
        return "/assets/caveuberhaul/entity/wasp_worker.png";
    }
}
