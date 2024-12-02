package tuxi.playermorph.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import tuxi.playermorph.PlayerMorph;
import tuxi.playermorph.networking.ClientboundMorphPacket;
import tuxi.playermorph.networking.ClientboundUnmorphPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MorphCommands {
    private static final Map<UUID, UUID> morphedPlayers = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("morph")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("morphPlayer", EntityArgument.player())
                        .then(Commands.argument("morphIntoTarget", EntityArgument.player())
                                .executes(ctx -> {
                                    ServerPlayer morphPlayer = EntityArgument.getPlayer(ctx, "morphPlayer");
                                    ServerPlayer morphIntoTarget = EntityArgument.getPlayer(ctx, "morphIntoTarget");
                                    if (!morphPlayer.equals(morphIntoTarget)) {
                                        morphPlayer(morphPlayer, morphIntoTarget);
                                    }else {
                                        unMorphPlayer(morphPlayer);
                                    }
                                    return 0;
                                })
                        )
                )
        );
        dispatcher.register(Commands.literal("unmorph")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("morphedPlayer", EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer morphedPlayer = EntityArgument.getPlayer(ctx, "morphedPlayer");
                            unMorphPlayer(morphedPlayer);
                            return 0;
                        })
                )
        );
    }

    public static void morphPlayer(ServerPlayer morphPlayer, ServerPlayer morphIntoTarget) {
        morphedPlayers.put(morphPlayer.getUUID(), morphIntoTarget.getUUID());
        PlayerMorph.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundMorphPacket(morphPlayer.getUUID(), morphIntoTarget.getUUID()));
    }

    public static void unMorphPlayer(ServerPlayer morphedPlayer) {
        morphedPlayers.remove(morphedPlayer.getUUID());
        PlayerMorph.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundUnmorphPacket(morphedPlayer.getUUID()));
    }

    public static void unMorphPlayer(UUID morphedPlayer) {
        morphedPlayers.remove(morphedPlayer);
        PlayerMorph.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundUnmorphPacket(morphedPlayer));
    }

    public static void removePlayer(ServerPlayer morphedPlayer) {
        morphedPlayers.remove(morphedPlayer.getUUID());
    }

    public static void sendAllMorphedToPlayer(ServerPlayer player) {
        if (player.getServer() != null) {
            for (UUID morphedPlayer : morphedPlayers.keySet()) {
                if (player.getServer().getPlayerList().getPlayer(morphedPlayer) == null)
                    unMorphPlayer(morphedPlayer);
            }
        }
        morphedPlayers.forEach((morphPlayer, morphIntoTarget) -> PlayerMorph.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundMorphPacket(morphPlayer, morphIntoTarget)));
    }
}
