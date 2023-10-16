package diarr.caveuberhaul.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class, remap = false, priority = 1001)
public class MinecraftMixin {
    @Shadow private static Minecraft theMinecraft;
    @Inject(method = "getMinecraft(Ljava/lang/Class;)Lnet/minecraft/client/Minecraft;", at = @At("HEAD"), cancellable = true)
    private static void returnMinecraft(Class<?> caller, CallbackInfoReturnable<Minecraft> cir){
        cir.setReturnValue(theMinecraft);
    }
}
