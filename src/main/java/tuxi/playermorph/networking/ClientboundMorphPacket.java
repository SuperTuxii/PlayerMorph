package tuxi.playermorph.networking;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import tuxi.playermorph.PlayerMorph;
import tuxi.playermorph.mixininterface.MorphPlayerInfo;

import java.util.UUID;
import java.util.function.Supplier;

public record ClientboundMorphPacket(UUID morphPlayer, UUID morphIntoPlayer) {
    public ClientboundMorphPacket(FriendlyByteBuf buffer) {
        this(buffer.readUUID(), buffer.readUUID());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(morphPlayer);
        buffer.writeUUID(morphIntoPlayer);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getNetworkManager().getPacketListener() instanceof ClientPacketListener clientPacketListener) {
            MorphPlayerInfo morphPlayerInfo = (MorphPlayerInfo) clientPacketListener.getPlayerInfo(morphPlayer);
            PlayerInfo morphIntoPlayerInfo = clientPacketListener.getPlayerInfo(morphIntoPlayer);
            if (morphPlayerInfo != null)
                morphPlayerInfo.playermorph$morph(((MorphPlayerInfo) morphIntoPlayerInfo));
        }else {
            PlayerMorph.LOGGER.warn("Expected PacketListener to be ClientPacketListener, but is {}", ctx.get().getNetworkManager().getPacketListener().getClass());
        }
    }
}
