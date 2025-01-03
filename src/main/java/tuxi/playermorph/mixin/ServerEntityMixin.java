package tuxi.playermorph.mixin;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tuxi.playermorph.mixininterface.GlowEntity;
import tuxi.playermorph.networking.ClientboundPredicatedPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Unique
    private static final int FLAG_GLOWING = 6;

    @Shadow @Final private Entity entity;

    @Shadow protected abstract void broadcastAndSend(Packet<?> p_8539_);

    @Inject(method = "sendPairingData", at = @At("HEAD"))
    private void setGlowDirty(ServerPlayer player, Consumer<Packet<ClientGamePacketListener>> broadcast, CallbackInfo ci) {
        if (((GlowEntity) this.entity).playermorph$isGlowing(player))
            ((GlowEntity) this.entity).playermorph$setDirty(player);
    }

    @Redirect(method = "sendDirtyEntityData", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerEntity;broadcastAndSend(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 0))
    private void redirectBroadcast(ServerEntity instance, Packet<?> packet) {
        if (packet instanceof ClientboundSetEntityDataPacket dataPacket) {
            GlowEntity glowEntity = (GlowEntity) this.entity;
            if (!this.entity.isCurrentlyGlowing()) {
                ClientboundSetEntityDataPacket glowPacket = playermorph$modifyGlowData(glowEntity, dataPacket);
                if (glowPacket != null) {
                    this.broadcastAndSend(new ClientboundPredicatedPacket(glowEntity::playermorph$isGlowing, glowPacket, dataPacket));
                }else {
                    this.broadcastAndSend(packet);
                }
            }else {
                this.broadcastAndSend(packet);
            }
            glowEntity.playermorph$getDirtyAndClear();
        }
    }

    @Inject(method = "sendDirtyEntityData", at = @At("TAIL"))
    private void sendDirtyGlowData(CallbackInfo ci) {
        GlowEntity glowEntity = (GlowEntity) this.entity;
        if (glowEntity.playermorph$isDirty()) {
            List<ServerPlayer> dirty = glowEntity.playermorph$getDirtyAndClear();
            this.broadcastAndSend(new ClientboundPredicatedPacket(
                    dirty::contains,
                     new ClientboundPredicatedPacket(
                             glowEntity::playermorph$isGlowing,
                             new ClientboundSetEntityDataPacket(
                                     this.entity.getId(),
                                     List.of(
                                             SynchedEntityData.DataValue.create(
                                                     glowEntity.playermorph$getSharedFlagsAccessor(),
                                                     (byte) (this.entity.getEntityData().get(glowEntity.playermorph$getSharedFlagsAccessor()) | (1 << FLAG_GLOWING))))),
                             new ClientboundSetEntityDataPacket(
                                     this.entity.getId(),
                                     List.of(
                                             SynchedEntityData.DataValue.create(
                                                     glowEntity.playermorph$getSharedFlagsAccessor(),
                                                     this.entity.getEntityData().get(glowEntity.playermorph$getSharedFlagsAccessor()))))
                     ),
                    null));
        }
    }

    @Unique
    @Nullable
    private ClientboundSetEntityDataPacket playermorph$modifyGlowData(GlowEntity glowEntity, ClientboundSetEntityDataPacket dataPacket) {
        SynchedEntityData.DataValue<Byte> sharedFlagsValue = dataPacket.packedItems().stream()
                .filter(dataValue -> dataValue.id() == glowEntity.playermorph$getSharedFlagsAccessor().getId())
                .map(dataValue -> (SynchedEntityData.DataValue<Byte>) dataValue)
                .findFirst().orElse(null);
        if (sharedFlagsValue != null) {
            SynchedEntityData.DataValue<Byte> glowingValue = SynchedEntityData.DataValue.create(
                    glowEntity.playermorph$getSharedFlagsAccessor(),
                    (byte) (sharedFlagsValue.value() | (1 << FLAG_GLOWING)));
            return new ClientboundSetEntityDataPacket(
                    dataPacket.id(),
                    dataPacket.packedItems().stream()
                            .map(dataValue -> {
                                if (dataValue.id() == glowingValue.id())
                                    return glowingValue;
                                return dataValue;
                            })
                            .toList());
        }else if (glowEntity.playermorph$isDirty()) {
            List<SynchedEntityData.DataValue<?>> glowingPackedItems = new ArrayList<>(dataPacket.packedItems());
            glowingPackedItems.add(SynchedEntityData.DataValue.create(
                    glowEntity.playermorph$getSharedFlagsAccessor(),
                    (byte) (this.entity.getEntityData().get(glowEntity.playermorph$getSharedFlagsAccessor()) | (1 << FLAG_GLOWING))));
            return new ClientboundSetEntityDataPacket(dataPacket.id(), glowingPackedItems);
        }
        return null;
    }
}
