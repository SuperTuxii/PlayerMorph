package tuxi.playermorph.mixininterface;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;

public interface MorphPlayerInfo {
    MorphPlayerInfo playermorph$getMorphedPlayerInfo();
    boolean playermorph$isMorphed();
    void playermorph$morph(MorphPlayerInfo morphIntoPlayerInfo);
    void playermorph$unmorph();
    GameProfile playermorph$getTrueProfile();
    int playermorph$getTrueLatency();
    boolean playermorph$isCapeTrulyLoaded();
    boolean playermorph$isSkinTrulyLoaded();
    String playermorph$getTrueModelName();
    ResourceLocation playermorph$getTrueSkinLocation();
    @Nullable ResourceLocation playermorph$getTrueCapeLocation();
    @Nullable ResourceLocation playermorph$getTrueElytraLocation();
    @Nullable PlayerTeam playermorph$getTrueTeam();
    @Nullable Component playermorph$getTrueTabListDisplayName();
}
