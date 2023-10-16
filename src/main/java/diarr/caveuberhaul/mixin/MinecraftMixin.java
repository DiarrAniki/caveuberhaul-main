package diarr.caveuberhaul.mixin;

import diarr.caveuberhaul.CaveUberhaul;
import diarr.caveuberhaul.Profiler;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
    @Shadow private int ticksRan;

    @Inject(method = "runTick()V", at = @At("HEAD"))
    private void checkProfileTimes(CallbackInfo ci){
        if (ticksRan % 100 == 0){
            Profiler.printTimesInRespectToID("Minecraft");
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_BACK)){
            CaveUberhaul.LOGGER.info("CLEARING TIMES");
            Profiler.clearTimes();
        }
        Profiler.methodStart("Minecraft");
    }
    @Inject(method = "runTick()V", at = @At("TAIL"))
    private void tickEnd(CallbackInfo ci){
        Profiler.methodEnd("Minecraft");
    }
}
