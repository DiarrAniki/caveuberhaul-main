package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.UberUtil;
import net.minecraft.client.GLAllocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.enums.RenderDistance;
import net.minecraft.client.render.FogManager;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

@Mixin(value = FogManager.class,remap = false)
public class FogManagerMixin {
    @Unique
    FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
    @Unique
    private float caveFogMinStart;
    @Shadow
    private float fogRed;
    @Shadow
    private float fogGreen;
    @Shadow
    private float fogBlue;
    @Final
    @Shadow
    private Minecraft mc;

    @Inject(method = "setupFog", at = @At("TAIL"))
    private void fogMixin(int fogMode, float farPlaneDistance, float partialTick, CallbackInfo ci) {
        EntityLiving player = this.mc.thePlayer;
        Vec3d vec = player.getPosition(partialTick);
        int xCord = MathHelper.floor_double(vec.xCoord);
        int yCord = MathHelper.floor_double(vec.yCoord);
        int zCord = MathHelper.floor_double(vec.zCoord);
        if (yCord < 48) {
            float min = (float)(RenderDistance.TINY.chunks * 16)/4;
            float max = (float)((this.mc.gameSettings.renderDistance.value).chunks * 16);
            float skylightFogValue = this.mc.theWorld.getSavedLightValue(LightLayer.Sky,xCord,yCord,zCord);
            float caveFogdist = UberUtil.clampedLerp(min,max,(vec.yCoord-8)/40,min,max)+skylightFogValue;
            if(fogMode < 0)
            {
                caveFogMinStart = 0.8f;
            }
            else
            {
                caveFogMinStart = 0.25f;
            }
            float caveFogMinStartLerped = UberUtil.clampedLerp(0.00f,caveFogMinStart,(vec.yCoord-8)/40,0.05f,caveFogMinStart);
            GL11.glFogf(2915, caveFogdist *caveFogMinStartLerped);
            GL11.glFogf(2916, caveFogdist);
            GL11.glFogf(2914, 1.0F);
            GL11.glEnable(2903);
            GL11.glColorMaterial(1028, 4608);
        }
    }
    @Inject(method = "updateFogColor", at = @At("TAIL"))
    private void fogColorMixin(float renderPartialTicks, CallbackInfo ci) {
        Vec3d vec = this.mc.thePlayer.getPosition(renderPartialTicks);
        float yCord = (float) vec.yCoord;
        if (yCord < 48) {
            float caveCol = UberUtil.lerp(0,1,(yCord-8)/40);
            Vec3d vec3d1 = this.mc.theWorld.getFogColor(renderPartialTicks);
            this.fogRed *= caveCol;
            this.fogGreen *= caveCol;
            this.fogBlue *= caveCol;
            GL11.glClearColor(this.fogRed, this.fogGreen, this.fogBlue, 1.0F);
        }
    }
    private FloatBuffer getFogColor(float r, float g, float b, float a) {
        this.fogColorBuffer.clear();
        this.fogColorBuffer.put(r).put(g).put(b).put(a);
        this.fogColorBuffer.flip();
        return this.fogColorBuffer;
    }
}
