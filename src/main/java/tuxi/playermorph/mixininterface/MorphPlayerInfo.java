package tuxi.playermorph.mixininterface;

import net.minecraft.client.multiplayer.PlayerInfo;

public interface MorphPlayerInfo {
    PlayerInfo wolfBugs$getMorphedPlayerInfo();
    boolean wolfBugs$isMorphed();
    void wolfBugs$morph(PlayerInfo morphIntoPlayerInfo);
    void wolfBugs$unmorph();
}
