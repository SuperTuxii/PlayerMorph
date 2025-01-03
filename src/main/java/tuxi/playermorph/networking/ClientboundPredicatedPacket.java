package tuxi.playermorph.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ClientboundPredicatedPacket implements Packet<ClientGamePacketListener> {
    private final Predicate<ServerPlayer> predicate;
    private final Packet<ClientGamePacketListener> isTrue;
    private final Packet<ClientGamePacketListener> isFalse;

    public ClientboundPredicatedPacket(@NotNull Predicate<ServerPlayer> predicate, @NotNull Packet<ClientGamePacketListener> isTrue, @Nullable Packet<ClientGamePacketListener> isFalse) {
        this.predicate = predicate;
        this.isTrue = isTrue;
        this.isFalse = isFalse;
    }

    public void handle(@NotNull ServerPlayerConnection playerConnection) {
        if (predicate.test(playerConnection.getPlayer()))
            playerConnection.send(isTrue);
        else if (isFalse != null)
            playerConnection.send(isFalse);
    }

    @Override
    public void write(@NotNull FriendlyByteBuf friendlyByteBuf) {
        throw new IllegalStateException("ClientboundPredicatedPacket should not be prepared to be sent");
    }

    @Override
    public void handle(@NotNull ClientGamePacketListener packetListener) {
        throw new IllegalStateException("ClientboundPredicatedPacket should not be received by Client");
    }
}
