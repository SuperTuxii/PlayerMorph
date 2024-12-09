package tuxi.playermorph;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import tuxi.playermorph.commands.MorphCommands;
import tuxi.playermorph.networking.ClientboundMorphPacket;
import tuxi.playermorph.networking.ClientboundUnmorphPacket;

@Mod(PlayerMorph.MODID)
public class PlayerMorph {

    public static final String MODID = "playermorph";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent joinEvent) {
        if (joinEvent.getEntity() instanceof ServerPlayer player) {
            MorphCommands.sendAllMorphedToPlayer(player);
        }
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent leaveEvent) {
        if (leaveEvent.getEntity() instanceof ServerPlayer player) {
            MorphCommands.removePlayer(player.getUUID());
        }
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        MorphCommands.register(event.getDispatcher());
    }

    public PlayerMorph() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        //Register packets
        int messageId = 0;
        CHANNEL.messageBuilder(ClientboundMorphPacket.class, messageId)
                .encoder(ClientboundMorphPacket::write)
                .decoder(ClientboundMorphPacket::new)
                .consumerMainThread(ClientboundMorphPacket::handle)
                .add();
        messageId++;
        CHANNEL.messageBuilder(ClientboundUnmorphPacket.class, messageId)
                .encoder(ClientboundUnmorphPacket::write)
                .decoder(ClientboundUnmorphPacket::new)
                .consumerMainThread(ClientboundUnmorphPacket::handle)
                .add();
    }
}
