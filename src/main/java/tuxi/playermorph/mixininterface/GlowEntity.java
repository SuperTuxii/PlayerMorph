package tuxi.playermorph.mixininterface;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public interface GlowEntity {
    EntityDataAccessor<Byte> playermorph$getSharedFlagsAccessor();

    void playermorph$setGlow(ServerPlayer player, boolean glow);
    void playermorph$clearGlow();
    boolean playermorph$isGlowing(ServerPlayer player);

    void playermorph$setDirty(ServerPlayer player);
    List<ServerPlayer> playermorph$getDirtyAndClear();
    boolean playermorph$isDirty();
}
