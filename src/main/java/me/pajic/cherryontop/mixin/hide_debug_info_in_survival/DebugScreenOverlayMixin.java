package me.pajic.cherryontop.mixin.hide_debug_info_in_survival;

import me.pajic.cherryontop.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "renderLines",
            at = @At("HEAD")
    )
    private void filterLines(GuiGraphics guiGraphics, List<String> lines, boolean leftSide, CallbackInfo ci) {
        if (Main.CONFIG.hideDebugInfoInSurvival() && minecraft.showOnlyReducedInfo()) {
            if (leftSide) {
                lines.removeIf(text -> text.startsWith("Chunk-relative: "));
                lines.removeIf(text -> text.startsWith("hunger: "));
            }
            else {
                // sodium 0.6 translucency sorting debug
                lines.removeIf(text -> text.startsWith("N="));
            }
        }
    }
}
