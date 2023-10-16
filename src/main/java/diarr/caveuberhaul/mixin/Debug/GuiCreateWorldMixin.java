package diarr.caveuberhaul.mixin.Debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.world.save.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mixin(value = GuiCreateWorld.class, remap = false)
public class GuiCreateWorldMixin {
    @Redirect(method = "buttonPressed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;startWorld(Ljava/lang/String;Ljava/lang/String;J)V"))
    private void hardCodeSeed(Minecraft instance, String worldDirName, String worldName, long seed){
        instance.startWorld(worldDirName, "DEBUG_WORLD_" + DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss").format(LocalDateTime.now()), 9999);
    }
    @Redirect(method = "buttonPressed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/EntityPlayerSP;setGamemode(Lnet/minecraft/core/player/gamemode/Gamemode;)V"))
    private void hardCodeGamemode(EntityPlayerSP instance, Gamemode gamemode){
        instance.setGamemode(Gamemode.creative);
    }
    @Redirect(method = "buttonPressed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/save/LevelData;setCheatsEnabled(Z)V"))
    private void hardCheats(LevelData instance, boolean enabled){
        instance.setCheatsEnabled(true);
    }
}
