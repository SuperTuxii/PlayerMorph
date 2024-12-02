package tuxi.playermorph.networking;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import tuxi.playermorph.PlayerMorph;
import tuxi.playermorph.mixininterface.MorphPlayerInfo;

import java.util.UUID;
import java.util.function.Supplier;

public record ClientboundUnmorphPacket(UUID morphedPlayer) {
    public ClientboundUnmorphPacket(FriendlyByteBuf buffer) {
        this(buffer.readUUID());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(morphedPlayer);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getNetworkManager().getPacketListener() instanceof ClientPacketListener clientPacketListener) {
            MorphPlayerInfo morphedPlayerInfo = (MorphPlayerInfo) clientPacketListener.getPlayerInfo(morphedPlayer);
            if (morphedPlayerInfo != null)
                morphedPlayerInfo.wolfBugs$unmorph();
        }else {
            PlayerMorph.LOGGER.warn("Expected PacketListener to be ClientPacketListener, but is {}", ctx.get().getNetworkManager().getPacketListener().getClass());
        }
    }
}
