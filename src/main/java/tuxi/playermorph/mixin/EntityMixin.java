package tuxi.playermorph.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import tuxi.playermorph.mixininterface.GlowEntity;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements GlowEntity {
    @Shadow @Final protected static EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID;

    @Unique
    ArrayList<ServerPlayer> playermorph$glowViewers = new ArrayList<>();
    @Unique
    ArrayList<ServerPlayer> playermorph$dirtyViewers = new ArrayList<>();

    @Override
    public EntityDataAccessor<Byte> playermorph$getSharedFlagsAccessor() {
        return DATA_SHARED_FLAGS_ID;
    }

    @Override
    public void playermorph$setGlow(ServerPlayer player, boolean glow) {
        if (glow && !playermorph$glowViewers.contains(player)) {
            playermorph$glowViewers.add(player);
            playermorph$setDirty(player);
        }else if (!glow && playermorph$glowViewers.contains(player)) {
            playermorph$glowViewers.remove(player);
            playermorph$setDirty(player);
        }
    }

    @Override
    public void playermorph$clearGlow() {
        playermorph$dirtyViewers.addAll(playermorph$glowViewers);
        playermorph$glowViewers.clear();
    }

    @Override
    public boolean playermorph$isGlowing(ServerPlayer player) {
        return playermorph$glowViewers.contains(player);
    }

    @Override
    public void playermorph$setDirty(ServerPlayer player) {
        playermorph$dirtyViewers.add(player);
    }

    @Override
    public List<ServerPlayer> playermorph$getDirtyAndClear() {
        List<ServerPlayer> list = List.copyOf(playermorph$dirtyViewers);
        playermorph$dirtyViewers.clear();
        return list;
    }

    @Override
    public boolean playermorph$isDirty() {
        return !playermorph$dirtyViewers.isEmpty();
    }
}
