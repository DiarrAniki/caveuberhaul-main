package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.particles.EntityDripFx;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.RenderEngine;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.fx.*;
import net.minecraft.core.item.Item;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value= RenderGlobal.class,remap = false)
public class RenderGlobalMixin {

    @Shadow
    private Minecraft mc;
    @Shadow
    private World worldObj;
    @Shadow
    private RenderEngine renderEngine;

    @Inject(method = "addParticle(Ljava/lang/String;DDDDDDD)V", at = @At("TAIL"), cancellable = true)
    private void addParticle(String particleId, double x, double y, double z, double motionX, double motionY, double motionZ, double maxDistance, CallbackInfo ci) {
        if (this.mc != null && this.mc.activeCamera != null && this.mc.effectRenderer != null) {
            double d6 = this.mc.activeCamera.getX() - x;
            double d7 = this.mc.activeCamera.getY() - y;
            double d8 = this.mc.activeCamera.getZ() - z;
            if (!(d6 * d6 + d7 * d7 + d8 * d8 > maxDistance * maxDistance)) {
                if (particleId.equals("bubble")) {
                    this.mc.effectRenderer.addEffect(new EntityBubbleFX(this.worldObj, x, y, z, motionX, motionY, motionZ));
                } else if (particleId.equals("smoke")) {
                    this.mc.effectRenderer.addEffect(new EntitySmokeFX(this.worldObj, x, y, z, motionX, motionY, motionZ));
                } else if (particleId.equals("note")) {
                    this.mc.effectRenderer.addEffect(new EntityNoteFX(this.worldObj, x, y, z, motionX, motionY, motionZ));
                } else if (particleId.equals("portal")) {
                    this.mc.effectRenderer.addEffect(new EntityPortalFX(this.worldObj, x, y, z, motionX, motionY, motionZ));
                } else if (particleId.equals("explode")) {
                    this.mc.effectRenderer.addEffect(new EntityExplodeFX(this.worldObj, x, y, z, motionX, motionY, motionZ));
                } else if (particleId.equals("flame")) {
                    this.mc.effectRenderer.addEffect(new EntityFlameFX(this.worldObj, x, y, z, motionX, motionY, motionZ, EntityFlameFX.Type.ORANGE));
                } else if (particleId.equals("blueflame")) {
                    this.mc.effectRenderer.addEffect(new EntityFlameFX(this.worldObj, x, y, z, motionX, motionY, motionZ, EntityFlameFX.Type.BLUE));
                } else if (particleId.equals("soulflame")) {
                    this.mc.effectRenderer.addEffect(new EntityFlameFX(this.worldObj, x, y, z, motionX, motionY, motionZ, EntityFlameFX.Type.SOUL));
                } else if (particleId.equals("lava")) {
                    this.mc.effectRenderer.addEffect(new EntityLavaFX(this.worldObj, x, y, z));
                } else if (particleId.equals("footstep")) {
                    this.mc.effectRenderer.addEffect(new EntityFootStepFX(this.renderEngine, this.worldObj, x, y, z));
                } else if (particleId.equals("splash")) {
                    this.mc.effectRenderer.addEffect(new EntitySplashFX(this.worldObj, x, y, z, motionX, motionY, motionZ));
                } else if (particleId.equals("largesmoke")) {
                    this.mc.effectRenderer.addEffect(new EntitySmokeFX(this.worldObj, x, y, z, motionX, motionY, motionZ, 2.5F));
                } else if (particleId.equals("reddust")) {
                    this.mc.effectRenderer.addEffect(new EntityReddustFX(this.worldObj, x, y, z, (float) motionX, (float) motionY, (float) motionZ));
                } else if (particleId.equals("snowballpoof")) {
                    this.mc.effectRenderer.addEffect(new EntitySlimeFX(this.worldObj, x, y, z, Item.ammoSnowball));
                } else if (particleId.equals("snowshovel")) {
                    this.mc.effectRenderer.addEffect(new EntitySnowShovelFX(this.worldObj, x, y, z, motionX, motionY, motionZ));
                } else if (particleId.equals("slime")) {
                    this.mc.effectRenderer.addEffect(new EntitySlimeFX(this.worldObj, x, y, z, Item.slimeball));
                } else if (particleId.equals("heart")) {
                    this.mc.effectRenderer.addEffect(new EntityHeartFX(this.worldObj, x, y, z, motionX, motionY, motionZ));
                } else if (particleId.equals("slimechunk")) {
                    if ((Boolean) this.mc.gameSettings.slimeParticles.value) {
                        this.mc.effectRenderer.addEffect(new EntitySlimeChunkFX(this.worldObj, x, y, z, Item.slimeball));
                    }
                } else if (particleId.equals("fireflyGreen")) {
                    this.mc.effectRenderer.addEffect(new EntityFireflyFX(this.worldObj, x, y, z, motionX, motionY, motionZ, 2.5F, 0));
                } else if (particleId.equals("fireflyBlue")) {
                    this.mc.effectRenderer.addEffect(new EntityFireflyFX(this.worldObj, x, y, z, motionX, motionY, motionZ, 2.5F, 1));
                } else if (particleId.equals("fireflyOrange")) {
                    this.mc.effectRenderer.addEffect(new EntityFireflyFX(this.worldObj, x, y, z, motionX, motionY, motionZ, 2.5F, 2));
                } else if (particleId.equals("fireflyRed")) {
                    this.mc.effectRenderer.addEffect(new EntityFireflyFX(this.worldObj, x, y, z, motionX, motionY, motionZ, 2.5F, 3));
                } else if (particleId.equals("arrowtrail")) {
                    this.mc.effectRenderer.addEffect(new EntityArrowGoldenFX(this.worldObj, x, y, z, motionX, motionY, motionZ));
                } else if (particleId.equals("arrowbreak")) {
                    this.mc.effectRenderer.addEffect(new EntitySlimeFX(this.worldObj, x, y, z, Item.ammoArrowPurple));
                } else if (particleId.equals("fallingleaf")) {
                    int id = this.worldObj.getBlockId((int) x, (int) y, (int) z);
                    if (id != 0) {
                        this.mc.effectRenderer.addEffect((new EntityLeafFX(this.worldObj, x, y, z, motionX, motionY, motionZ)).func_4041_a((int) x, (int) y, (int) z));
                    }
                } else if (particleId.equals("boatbreak")) {
                    this.mc.effectRenderer.addEffect(new EntityDiggingFX(this.worldObj, x, y, z, motionX, motionY, motionZ, Block.planksOak, 0, 0));
                } else if (particleId.equals("drip")) {
                    this.mc.effectRenderer.addEffect(new EntityDripFx(this.worldObj, x, y, z, motionX, motionY, motionZ));
                }

            }
        }
        ci.cancel();
    }
}

