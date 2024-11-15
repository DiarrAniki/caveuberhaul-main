package diarr.caveuberhaul.entity;

import net.minecraft.core.entity.EntityLiving;
import org.useless.dragonfly.helper.AnimationHelper;
import org.useless.dragonfly.model.entity.BenchEntityModel;
import org.useless.dragonfly.model.entity.animation.Animation;

import static diarr.caveuberhaul.CaveUberhaul.MOD_ID;

public class EntityWaspModel extends BenchEntityModel {
    public static EntityWasp wasp;
    @Override
    public void setLivingAnimations(EntityLiving entityliving, float limbSwing, float limbYaw, float partialTick) {
        super.setLivingAnimations(entityliving, limbSwing, limbYaw, partialTick);
        if (entityliving instanceof EntityWasp) {
            wasp = (EntityWasp) entityliving;
        }
    }
    public void setRotationAngles(float limbSwing, float limbYaw, float ticksExisted, float headYaw, float headPitch, float scale) {
        // If you need play some animation. you should reset with this
        this.getIndexBones().forEach((s, benchEntityBones) -> benchEntityBones.resetPose());
        super.setRotationAngles(limbSwing, limbYaw, ticksExisted, headYaw, headPitch, scale);
        /*if (this.getIndexBones().containsKey("Head")) {
            this.getIndexBones().get("Head")
                    .setRotationAngle((float) Math.toRadians((double) headPitch), (float) Math.toRadians((double) headYaw), 0.0F);
        }

        if (this.getIndexBones().containsKey("bone")) {
            this.getIndexBones().get("bone").setRotationAngle(0.0F, ticksExisted, 0.0F);
        }*/

        Animation waspAnimations = AnimationHelper.getOrCreateEntityAnimation(MOD_ID, "workerbee.animation");
        if(wasp != null) {
            if(wasp.entityToAttack==null) {
                animate(wasp.idleState, waspAnimations.getAnimations().get("animation.WorkerBee.flutterIdle"), ticksExisted, 1.0F);
            }
            else {
                animate(wasp.angryState, waspAnimations.getAnimations().get("animation.WorkerBee.flutterAngry"), ticksExisted, 1.0F);
            }
        }
    }
}
