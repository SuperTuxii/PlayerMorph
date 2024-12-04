package tuxi.playermorph.mixin;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tuxi.playermorph.mixininterface.MorphPlayerInfo;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
    @Inject(method = "getPlayerInfo", at = @At("TAIL"), cancellable = true)
    private void replaceWithMorphedPlayerInfo(CallbackInfoReturnable<PlayerInfo> cir) {
        if (cir.getReturnValue() != null && ((MorphPlayerInfo) cir.getReturnValue()).wolfBugs$isMorphed())
            cir.setReturnValue(((MorphPlayerInfo) cir.getReturnValue()).wolfBugs$getMorphedPlayerInfo());
    }
}
